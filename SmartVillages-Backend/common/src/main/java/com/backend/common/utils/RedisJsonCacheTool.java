package com.backend.common.utils;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * Redis JSON 缓存工具，统一处理详情缓存、列表缓存、空值占位和列表版本号。
 *
 * <p>详情缓存用于单条资源，列表缓存使用版本号做批量失效，空值占位用于拦截短时间内反复访问不存在资源的请求。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisJsonCacheTool {

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;
  private final RedisDistributedLock redisDistributedLock;
  @Value("${cache.ttl.detail-minutes:5}")
  private long defaultTtlMinutes;

  @Value("${cache.ttl.detail-jitter-seconds:300}")
  private long detailJitterSeconds;

  @Value("${cache.ttl.null-marker-minutes:2}")
  private long nullMarkerTtlMinutes;

  @Value("${cache.ttl.list-minutes:10}")
  private long listTtlMinutes;

  private static final String NULL_MARKER = "_NULL_";

  /**
   * 从 Redis 读取 JSON 并反序列化为指定类型。
   *
   * @param key Redis key
   * @param type 目标类型
   * @return 缓存对象；未命中、空值占位或反序列化失败时返回 {@code null}
   */
  public <T> T getObject(String key, Class<T> type) {
    String redisKey = Objects.requireNonNull(key, "redis key must not be null");
    Class<T> targetType = Objects.requireNonNull(type, "target type must not be null");
    String cached = stringRedisTemplate.opsForValue().get(redisKey);
    if (!StringUtils.hasText(cached)) {
      log.debug("redis cache miss, key={}", redisKey);
      return null;
    }
    if (NULL_MARKER.equals(cached)) {
      log.debug("redis cache is null marker, key={}", redisKey);
      return null;
    }
    try {
      log.debug("redis cache hit, key={}", redisKey);
      return objectMapper.readValue(cached, targetType);
    } catch (JsonProcessingException e) {
      log.warn("redis cache json parse failed, key={}", redisKey, e);
      stringRedisTemplate.delete(redisKey);
      return null;
    }
  }

public <T> T getObject(String key, Class<T> type, Supplier<T> loader){
   String cached = stringRedisTemplate.opsForValue().get(key);
   if(isNullMarker(key)){
    log.debug("redis cache null marker hit, key={}", key);
    return null;
   }
   if(cached!=null){
    try {
      log.debug("redis cache hit, key={}", key);
      return objectMapper.readValue(cached, type);
    } catch (JsonProcessingException e) {
      log.warn("redis cache json parse failed, key={}", key, e);
      stringRedisTemplate.delete(key);
      return null;
    }
   }

   String lockKey="mutex:"+key;
   String instance=RedisDistributedLock.generateInstanceId();
   log.debug("redis cache miss, preparing loader with lockKey={}", lockKey);
   if(redisDistributedLock.tryLock(lockKey, instance)){
    try {
        log.debug("redis cache loader acquired lock, lockKey={}", lockKey);
        T result =loader.get();
        if(result==null){
          setNullMarker(key);
        }else{
          setObject(key,result);
        }
        return result;
    } finally {
      redisDistributedLock.unlock(lockKey,instance);
    }
   }else{
    log.debug("redis cache loader lock busy, retrying lockKey={}", lockKey);
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return getObject(key,type,loader);
   }
}

private Duration buildDetailCacheTtl(){
  if(detailJitterSeconds<=0){
  return Duration.ofMinutes(defaultTtlMinutes);
  }
  long jitterSeconds=ThreadLocalRandom.current().nextLong(detailJitterSeconds+1);
  return Duration.ofMinutes(defaultTtlMinutes).plusSeconds(jitterSeconds);
}

  /** 使用默认详情 TTL 写入缓存对象。 */
  public void setObject(String key, Object value) {
    setObject(key, value,buildDetailCacheTtl());
  }

  /**
   * 写入 JSON 缓存。
   *
   * @param key Redis key
   * @param value 缓存对象
   * @param ttl 过期时间
   */
  public void setObject(String key, Object value, Duration ttl) {
    String redisKey = Objects.requireNonNull(key, "redis key must not be null");
    Object cacheValue = Objects.requireNonNull(value, "cache value must not be null");
    Duration cacheTtl = Objects.requireNonNull(ttl, "cache ttl must not be null");
    if (cacheTtl.isZero() || cacheTtl.isNegative()) {
      throw new IllegalArgumentException("cache ttl must be greater than zero");
    }
    try {
      String json =
          Objects.requireNonNull(
              objectMapper.writeValueAsString(cacheValue), "cache json must not be null");
      stringRedisTemplate
          .opsForValue()
          .set(redisKey, json, cacheTtl.toMillis(), TimeUnit.MILLISECONDS);
      log.debug("redis cache set, key={}, ttl={}ms", redisKey, cacheTtl.toMillis());
    } catch (JsonProcessingException e) {
      log.warn("redis cache json write failed, key={}", redisKey, e);
    }
  }

  /** 读取「版本化列表缓存」的版本号；key 不存在时视为 {@code "0"}（与首次读侧一致）。 */
  public String getListCacheVersionOrZero(String versionKey) {
    String redisKey = Objects.requireNonNull(versionKey, "version key must not be null");
    String version = stringRedisTemplate.opsForValue().get(redisKey);
    return StringUtils.hasText(version) ? version : "0";
  }

  /**
   * 列表缓存批量失效：对版本 key 执行 INCR，读侧使用新版本拼分页 key，旧 key 随 TTL 自然淘汰。
   *
   * @return INCR 后的值
   */
  public long bumpListCacheVersion(String versionKey) {
    String redisKey = Objects.requireNonNull(versionKey, "version key must not be null");
    Long n = stringRedisTemplate.opsForValue().increment(redisKey);
    return n == null ? 1L : n;
  }

  /** 版本化分页列表 Redis key：{@code prefix + version + ":" + current + ":" + size}。 */
  public String buildVersionedListPageKey(String prefix, String version, long current, long size) {
    Objects.requireNonNull(prefix, "prefix must not be null");
    Objects.requireNonNull(version, "version must not be null");
    return prefix + version + ":" + current + ":" + size;
  }

  /** 使用 {@code cache.ttl.list-minutes} 写入列表类缓存。 */
  public void setListCacheObject(String key, Object value) {
    setObject(key, value, Duration.ofMinutes(listTtlMinutes));
  }

  /** 删除指定 Redis key。 */
  public void delete(String key) {
    String redisKey = Objects.requireNonNull(key, "redis key must not be null");
    stringRedisTemplate.delete(redisKey);
   log.debug("redis cache delete, key={}", redisKey);
  }

  /** 使用空值占位默认 TTL 写入 {@code _NULL_}，用于短时间防穿透。 */
  public void setNullMarker(String key) {
    setNullMarker(key, Duration.ofMinutes(nullMarkerTtlMinutes));
  }

  /** 写入指定 TTL 的空值占位。 */
  public void setNullMarker(String key, Duration ttl) {
    String redisKey = Objects.requireNonNull(key, "redis key must not be null");
    Duration cacheTtl = Objects.requireNonNull(ttl, "cache ttl must not be null");
    if (cacheTtl.isZero() || cacheTtl.isNegative()) {
      throw new IllegalArgumentException("cache ttl must be greater than zero");
    }
    stringRedisTemplate
        .opsForValue()
        .set(redisKey, NULL_MARKER, cacheTtl.toMillis(), TimeUnit.MILLISECONDS);
    log.debug("redis cache set null marker, key={}, ttl={}ms", redisKey, cacheTtl.toMillis());
  }

  /**
   * 判断 key 当前是否为空值占位。
   *
   * @param key Redis key
   * @return true 表示缓存中保存的是空值占位
   */
  public boolean isNullMarker(String key) {
    String redisKey = Objects.requireNonNull(key, "redis key must not be null");
    String cached = stringRedisTemplate.opsForValue().get(redisKey);
    return Objects.equals(NULL_MARKER, cached);
  }
}
