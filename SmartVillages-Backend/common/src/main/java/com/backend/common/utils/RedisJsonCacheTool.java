package com.backend.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisJsonCacheTool {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final long DEFAULT_TTL = 20 * 60 * 1000; // 20分钟
    private static final String DEFAULT_PREFIX = "smartVillages:";
    public <T> T getObject(String key, Class<T> type) {
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(cached)) {
            return null;
        }
        try {
            return objectMapper.readValue(cached, type);
        } catch (JsonProcessingException e) {
            log.warn("redis cache json parse failed, key={}", key, e);
            stringRedisTemplate.delete(key);
            return null;
        }
    }

    public void setObject(String key, Object value, long ttlMillis) {
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    ttlMillis,
                    TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            log.warn("redis cache json write failed, key={}", key, e);
        }
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
