package com.backend.common.interceptor;

import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import com.backend.common.utils.JwtUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 拦截器
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (isWhitelist(uri) || "OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            String subject = JwtUtils.parseToken(token);
            if (StringUtils.hasText(subject)) {
                String[] parts = subject.split(":", 3);
                if (parts.length != 3) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                request.setAttribute("authId", parts[0]);
                request.setAttribute("username", parts[1]);
                request.setAttribute("role", parts[2]);
                if (uri.startsWith("/admin") && !"admin".equals(parts[2])) {
                    throw new BusinessException(ErrorCode.NO_PERMISSION);
                }
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    private boolean isWhitelist(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.contains("/auth/login")) {
            return true;
        }
        if (uri.contains("/v3/api-docs")) {
            return true;
        }
        if (uri.contains("/doc.html")) {
            return true;
        }
        if (uri.startsWith("/swagger-ui") || uri.contains("/swagger-ui/")) {
            return true;
        }
        if (uri.startsWith("/webjars") || uri.contains("/webjars/")) {
            return true;
        }
        return false;
    }
}