package com.backend.common.context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 统一读取拦截器写入的登录用户上下文，避免每个接口重复写 attribute key。
 */
/**
 * @author chenyang
 * &#064;date 2026/4/2
 * &#064;description 登录用户上下文，避免每个接口重复写 attribute key。
 */

public final class LoginUserContext {

    private LoginUserContext() {}

    public static Integer getAuthId(HttpServletRequest request) {
        Object value = request.getAttribute("authId");
        return value == null ? null : Integer.valueOf(String.valueOf(value));
    }

    public static String getUsername(HttpServletRequest request) {
        Object value = request.getAttribute("username");
        return value == null ? null : String.valueOf(value);
    }

    public static String getRole(HttpServletRequest request) {
        Object value = request.getAttribute("role");
        return value == null ? null : String.valueOf(value);
    }

    public static String getAvatar(HttpServletRequest request) {
        Object value = request.getAttribute("avatar");
        return value == null ? null : String.valueOf(value);
    }
}
