package com.backend.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisJsonCacheTool {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.ttl.detail-minutes:30}")
    private long defaultTtlMinutes;

    @Value("${cache.ttl.null-marker-minutes:2}")
    private long nullMarkerTtlMinutes;

    @Value("${cache.ttl.list-minutes:10}")
    private long listTtlMinutes;

    private static final String NULL_MARKER = "_NULL_";

    public <T> T getObject(String key, Class<T> type) {
        String redisKey = Objects.requireNonNull(key, "redis key must not be null");
        Class<T> targetType = Objects.requireNonNull(type, "target type must not be null");
        String cached = stringRedisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.hasText(cached)){
            log.debug("redis cache miss, key={}", redisKey);
            return null;
        }
        if(NULL_MARKER.equals(cached)){
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

    public void setObject(String key, Object value) {
        setObject(key, value, Duration.ofMinutes(defaultTtlMinutes));
    }

    public void setObject(String key, Object value, Duration ttl) {
        String redisKey = Objects.requireNonNull(key, "redis key must not be null");
        Object cacheValue = Objects.requireNonNull(value, "cache value must not be null");
        Duration cacheTtl = Objects.requireNonNull(ttl, "cache ttl must not be null");
        if (cacheTtl.isZero() || cacheTtl.isNegative()) {
            throw new IllegalArgumentException("cache ttl must be greater than zero");
        }
        try {
            String json = Objects.requireNonNull(
                    objectMapper.writeValueAsString(cacheValue),
                    "cache json must not be null");
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    json,
                    cacheTtl.toMillis(),
                    TimeUnit.MILLISECONDS);
            log.debug("redis cache set, key={}, ttl={}ms", redisKey, cacheTtl.toMillis());
        } catch (JsonProcessingException e) {
            log.warn("redis cache json write failed, key={}", redisKey, e);
        }
    }

    /**
     * 读取「版本化列表缓存」的版本号；key 不存在时视为 {@code "0"}（与首次读侧一致）。
     */
    public String getListCacheVersionOrZero(String versionKey) {
        String redisKey = Objects.requireNonNull(versionKey, "version key must not be null");
        String v = stringRedisTemplate.opsForValue().get(redisKey);
        return StringUtils.hasText(v) ? v : "0";
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

    /**
     * 版本化分页列表 Redis key：{@code prefix + version + ":" + current + ":" + size}。
     */
    public String buildVersionedListPageKey(String prefix, String version, long current, long size) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        Objects.requireNonNull(version, "version must not be null");
        return prefix + version + ":" + current + ":" + size;
    }

    /** 使用 {@code cache.ttl.list-minutes} 写入列表类缓存 */
    public void setListCacheObject(String key, Object value) {
        setObject(key, value, Duration.ofMinutes(listTtlMinutes));
    }

    public void delete(String key) {
        String redisKey = Objects.requireNonNull(key, "redis key must not be null");
        stringRedisTemplate.delete(redisKey);
        log.debug("redis cache delete, key={}", redisKey);
    }

    public void setNullMarker(String key) {
        setNullMarker(key,Duration.ofMinutes(nullMarkerTtlMinutes));
    }

    public void setNullMarker(String key,Duration ttl) {
        String redisKey = Objects.requireNonNull(key, "redis key must not be null");
        Duration cacheTtl = Objects.requireNonNull(ttl, "cache ttl must not be null");
          if (cacheTtl.isZero() || cacheTtl.isNegative()) {
            throw new IllegalArgumentException("cache ttl must be greater than zero");
        }
            stringRedisTemplate.opsForValue().set(
                redisKey,
                NULL_MARKER,
                cacheTtl.toMillis(),
                TimeUnit.MILLISECONDS);
            log.debug("redis cache set null marker, key={}, ttl={}ms", redisKey, cacheTtl.toMillis());
    }

    public boolean isNullMarker(String key) {
        String redisKey = Objects.requireNonNull(key, "redis key must not be null");
        String cached = stringRedisTemplate.opsForValue().get(redisKey);
        return Objects.equals(NULL_MARKER, cached);
    }
}
