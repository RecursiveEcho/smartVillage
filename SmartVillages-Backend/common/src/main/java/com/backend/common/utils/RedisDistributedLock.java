package com.backend.common.utils;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisDistributedLock {

  private final StringRedisTemplate stringRedisTemplate;

  private static final Duration DEFAULT_TTL = Duration.ofSeconds(10);

  /** Lua 原子解锁脚本。 */
  private static final String UNLOCK_SCRIPT =
      """
      if redis.call('get',KEYS[1]) == ARGV[1] then
        return redis.call('del',KEYS[1])
        else
          return 0;
          end""";

  private static final DefaultRedisScript<Long> UNLOCK =
      new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);

  /** 尝试获取锁。 */
  public boolean tryLock(String lockKey, String instance, Duration ttl) {
    Duration actualTtl = ttl != null ? ttl : DEFAULT_TTL;
    Boolean ok = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, instance, actualTtl);
    boolean locked = Boolean.TRUE.equals(ok);
    log.debug("distributed lock {} for key={}", locked ? "acquired" : "failed", lockKey);
    return locked;
  }

  /** 使用默认 TTL 尝试获取锁。 */
  public boolean tryLock(String lockKey, String instance) {
    return tryLock(lockKey, instance, DEFAULT_TTL);
  }

  /** 释放锁（只有持有者才能释放） */
  public void unlock(String lockKey, String instance) {
    Long deleted =
        stringRedisTemplate.execute(UNLOCK, Collections.singletonList(lockKey), instance);
    if (deleted != null && deleted == 1L) {
      log.debug("distributed lock released, key={}", lockKey);
    }
  }

  public static String generateInstanceId() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
