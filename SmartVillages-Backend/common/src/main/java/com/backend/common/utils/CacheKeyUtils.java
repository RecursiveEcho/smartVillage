package com.backend.common.utils;

import java.util.StringJoiner;

public final class CacheKeyUtils {

    //禁止别人 new CacheKeyUtils() 实例
    private CacheKeyUtils() {}

    //生成详情缓存 key
    public static String detailKey(String prefix, Object id) {
        return prefix + id;
    }

    /**
     * 列表分页缓存 key 的筛选维度片段：与 {@code current}/{@code size} 组合使用，避免不同筛选条件命中同一分页 key。
     * <p>值为无符号十进制哈希 + {@code ':'}，可安全拼在 Redis key 前缀后。</p>
     */
    public static String listFilterSegment(Object... parts) {
        if (parts == null || parts.length == 0) {
            return "0:";
        }
        StringJoiner joiner = new StringJoiner("\u001f");
        for (Object p : parts) {
            joiner.add(p == null ? "\0" : String.valueOf(p));
        }
        return Integer.toUnsignedString(joiner.toString().hashCode()) + ":";
    }
}
