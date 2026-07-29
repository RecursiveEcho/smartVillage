package com.backend.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import static org.mockito.ArgumentMatchers.eq;

import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

//@ExtendWith(MockitoExtension.class)自动处理@Mock 和 @InjectMocks 注解
@ExtendWith(MockitoExtension.class)
class RedisJsonCacheToolTest {

    //@Mock 注解用于创建模拟对象，模拟对象是一个虚拟的对象，它可以模拟真实对象的行为和状态
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    //ValueOperations 是 Spring Data Redis 提供的一个接口，用于操作 Redis 中的值（value）数据类型
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisDistributedLock redisDistributedLock;

    //@InjectMocks
    @InjectMocks
    private RedisJsonCacheTool redisJsonCacheTool;

    // ==================== getObject(key, type) —— 无 loader ====================
    @Test
    //命名规则：被测方法_when条件_结果
    // 缓存未命中 → 返回 null
    void getObject_whenCacheMiss_returnsNull() {
        String key = "cache-key";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class);

        assertThat(actual).isNull();
    }

    @Test
    // 空值占位命中 → 返回 null
    void getObject_whenNullMarkerHit_returnsNull() {
        String key = "cache-key";
        String json = "_NULL_";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class);

        assertThat(actual).isNull();
    }

    @Test
    // 缓存命中 → 返回反序列化对象
    void getObject_whenCacheHit_returnsDeserializedObject() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo expected = new TestPojo(123);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, TestPojo.class)).thenReturn(expected);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    // JSON 反序列化失败 → 返回 null 并删除脏缓存
    void getObject_whenJsonParseFails_returnsNullAndDeletesKey() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, TestPojo.class))
                .thenThrow(new JsonParseException(null, "JSON parse error"));

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class);

        assertThat(actual).isNull();
        verify(objectMapper).readValue(json, TestPojo.class);
        verify(stringRedisTemplate).delete(key);
    }

    // ==================== getObject(key, type, loader) —— 带 loader ====================
    @Test
    // 缓存命中 → 返回反序列化对象，不调用 loader 或锁
    void getObject_whenCacheHit_returnsDeserializedObjectAndDoesNotCallLoaderOrLock() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo expected = new TestPojo(123);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);
        when(objectMapper.readValue(json, TestPojo.class)).thenReturn(expected);

        @SuppressWarnings("unchecked")//压制泛型警告。
        Supplier<TestPojo> loader = mock(Supplier.class);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        //.isSameAs()== 引用比较
        assertThat(actual).isSameAs(expected);
        verify(loader, never()).get();
        verify(redisDistributedLock, never()).tryLock(anyString(), anyString());
        verify(redisDistributedLock, never()).unlock(anyString(), anyString());
    }

    @Test
    // 空值占位命中 → 返回 null，不调用 loader 或锁
    void getObject_whenNullMarkerHit_returnsNullAndDoesNotCallLoaderOrLock() throws Exception {
        String key = "cache-key";
        String json = "_NULL_";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(json);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        assertThat(actual).isNull();
        verify(loader, never()).get();
        verify(redisDistributedLock, never()).tryLock(anyString(), anyString());
        verify(redisDistributedLock, never()).unlock(anyString(), anyString());
    }

    @Test
    // 缓存未命中 + 成功获取锁 → loader 加载对象 → 写缓存、释放锁、返回对象
    void getObject_whenCacheMissAndLockAcquired_loadsWritesAndUnlocks() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo loaded = new TestPojo(123);

        //使用反射工具类设置私有字段的值
        ReflectionTestUtils.setField(redisJsonCacheTool, "defaultTtlMinutes", 5L);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(redisDistributedLock.tryLock(anyString(), anyString())).thenReturn(true);
        when(objectMapper.writeValueAsString(loaded)).thenReturn(json);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);
        when(loader.get()).thenReturn(loaded);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        assertThat(actual).isSameAs(loaded);
        verify(loader).get();
        verify(valueOperations).set(key, json, 300000L, TimeUnit.MILLISECONDS);
        verify(redisDistributedLock).unlock(anyString(), anyString());
    }

    @Test
    // 缓存未命中 + 成功获取锁 + loader 返回 null → 写空值占位、释放锁、返回 null
    void getObject_whenLoaderReturnsNull_writesNullMarkerAndUnlocks() {
        String key = "cache-key";

        // 设定空值占位 TTL 为 2 分钟，使写入缓存的过期时间可预测
        ReflectionTestUtils.setField(redisJsonCacheTool, "nullMarkerTtlMinutes", 2L);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(redisDistributedLock.tryLock(anyString(), anyString())).thenReturn(true);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);
        when(loader.get()).thenReturn(null);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        assertThat(actual).isNull();
        verify(loader).get();
        verify(valueOperations).set(key, "_NULL_", 120000L, TimeUnit.MILLISECONDS);
        verify(redisDistributedLock).unlock(anyString(), anyString());
    }

    @Test
    //重试后命中其他线程重建的缓存
    void getObject_whenLockBusy_retriesAndReturnsRebuiltCache() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo expected = new TestPojo(123);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null).thenReturn(json);
        when(redisDistributedLock.tryLock(anyString(), anyString())).thenReturn(false);
        when(objectMapper.readValue(json, TestPojo.class)).thenReturn(expected);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        assertThat(actual).isSameAs(expected);
        verify(loader, never()).get();
        verify(valueOperations, times(2)).get(key);
        verify(redisDistributedLock).tryLock(anyString(), anyString());
        verify(redisDistributedLock, never()).unlock(anyString(), anyString());
    }

    @Test
// 锁持续被占用 -> 达到最大重试次数后返回 null
    void getObject_whenLockRemainsBusy_stopsAfterMaxRetries() {
        String key = "cache-key";

        ReflectionTestUtils.setField(redisJsonCacheTool, "maxLockRetryCount", 2);

        ReflectionTestUtils.setField(
                redisJsonCacheTool, "lockRetryIntervalMillis", 1L);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(redisDistributedLock.tryLock(anyString(), anyString())).thenReturn(false);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);

        TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

        assertThat(actual).isNull();
        verify(loader, never()).get();
        verify(valueOperations, times(3)).get(key);
        verify(redisDistributedLock, times(3)).tryLock(anyString(), anyString());
        verify(redisDistributedLock, never()).unlock(anyString(), anyString());

    }

    @Test
// 等待重试时线程被中断 -> 立即停止并保留中断标记
    void getObject_whenRetryWaitInterrupted_stopsAndRestoresInterruptFlag() {
        String key = "cache-key";

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        when(redisDistributedLock.tryLock(anyString(), anyString())).thenReturn(false);

        @SuppressWarnings("unchecked")
        Supplier<TestPojo> loader = mock(Supplier.class);

        try {
            Thread.currentThread().interrupt();

            TestPojo actual = redisJsonCacheTool.getObject(key, TestPojo.class, loader);

            assertThat(actual).isNull();
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            verify(loader, never()).get();
            verify(valueOperations).get(key);
            verify(redisDistributedLock).tryLock(anyString(), anyString());
            verify(redisDistributedLock, never()).unlock(anyString(), anyString());
        } finally {
            // 清除中断标记，以免影响其他测试
            Thread.interrupted();
        }
    }

    @Test
// 关闭随机 TTL -> 使用精确的基础过期时间
    void setObject_whenJitterDisabled_usesBaseTtl() throws Exception{
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo value = new TestPojo(123);

        ReflectionTestUtils.setField(redisJsonCacheTool, "detailJitterSeconds", 0L);

        ReflectionTestUtils.setField(redisJsonCacheTool, "defaultTtlMinutes", 5L);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(value)).thenReturn(json);

        redisJsonCacheTool.setObject(key, value);

        verify(valueOperations).set(key, json, 300000L, TimeUnit.MILLISECONDS);
        }

        @Test
        // 开启随机 TTL -> 过期时间位于基础 TTL 和最大 TTL 之间
        void setObject_whenJitterEnabled_usesTtlWithinExpectedRange() throws Exception {
        String key = "cache-key";
        String json = "{\"id\":123}";
        TestPojo value = new TestPojo(123);

        ReflectionTestUtils.setField(redisJsonCacheTool, "detailJitterSeconds", 300L);
        ReflectionTestUtils.setField(redisJsonCacheTool, "defaultTtlMinutes", 5L);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(value)).thenReturn(json);

        redisJsonCacheTool.setObject(key, value);

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(valueOperations).set(eq(key), eq(json), ttlCaptor.capture(), eq(TimeUnit.MILLISECONDS));

        assertThat(ttlCaptor.getValue()).isBetween(300000L, 600000L);
        }
        // ==================== 内部测试类 ====================

    static class TestPojo {

        private final int id;

        TestPojo(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
