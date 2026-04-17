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
        //获取缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        //如果缓存为空，返回null
        if (!StringUtils.hasText(cached)) {
            return null;
        }
        try {
            //将缓存转换为对象
            return objectMapper.readValue(cached, type);
        } catch (JsonProcessingException e) {
            log.warn("redis cache json parse failed, key={}", key, e);
            //如果转换失败，删除缓存
            stringRedisTemplate.delete(key);
            return null;
        }
    }

    public void setObject(String key, Object value) {
        try {
            //将对象转换为缓存
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(value),
                    CACHE_TTL.toMillis(),
                    TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            //如果转换失败，删除缓存
            log.warn("redis cache json write failed, key={}", key, e);
        }
    }

    public void delete(String key) {
        //删除缓存
        stringRedisTemplate.delete(key);
    }
}
