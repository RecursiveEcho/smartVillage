package com.backend.common.utils;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import java.time.Duration;
@Component
@RequiredArgsConstructor
public class RedisRateLimiter {
    private final StringRedisTemplate stringRedisTemplate;

    private static final long WINDOW_SECONDE=60;
    /**
     * 检查限流，超限直接抛 RATE_LIMIT_EXCEEDED
     *
     * @param key         Redis key（建议格式 rate_limit:场景:标识）
     * @param maxRequests 窗口内最大请求次数
     */
    public void check(String key,int maxRequests){
        Long count = stringRedisTemplate.opsForValue().increment(key);
        //到期自动删除
        if(count!=null&&count==1){
            stringRedisTemplate.expire(key,Duration.ofSeconds(WINDOW_SECONDE));
        }
        if(count!=null&&count>maxRequests){
        throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }
}
