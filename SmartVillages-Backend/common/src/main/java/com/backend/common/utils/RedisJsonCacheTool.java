package com.backend.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisJsonCacheTool {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final Duration CACHE_TTL = Duration.ofMinutes(20);
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

    public void setObject(String key, Object value) {
        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    CACHE_TTL.toMillis(),
                    TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            log.warn("redis cache json write failed, key={}", key, e);
        }
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}
