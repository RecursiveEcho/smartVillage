package com.backend.common.utils;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 滑动窗口外的简易限流器。
 *
 * <p>按 key 统计固定时间窗内的请求次数，超限后直接抛出业务异常。
 */
@Component
@RequiredArgsConstructor
public class RedisRateLimiter {
  private final StringRedisTemplate stringRedisTemplate;

  private static final long WINDOW_SECONDE = 60;

  /**
   * 检查限流，超限直接抛出 {@link ErrorCode#RATE_LIMIT_EXCEEDED}。
   *
   * @param key Redis key（建议格式 rate_limit:场景:标识）
   * @param maxRequests 窗口内最大请求次数
   */
  public void check(String key, int maxRequests) {
    Long count = stringRedisTemplate.opsForValue().increment(key);
    // 首次写入时设置过期时间
    if (count != null && count == 1) {
      stringRedisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDE));
    }
    if (count != null && count > maxRequests) {
      throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED);
    }
  }
}
