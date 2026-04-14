package com.backend.common.utils;

public final class CacheKeyUtils {

    //禁止别人 new CacheKeyUtils() 实例
    private CacheKeyUtils() {}

    //生成详情缓存 key
    public static String detailKey(String prefix, Object id) {
        return prefix + id;
    }
}
