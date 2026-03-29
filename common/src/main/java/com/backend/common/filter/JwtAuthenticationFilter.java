package com.backend.common.filter;

import com.backend.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** 仅对三端 API（{@code /api/user|cadre|admin}）验 Header {@code token}；{@code …/login} 与 OPTIONS 放行。 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] API_ROOTS = {"/api/user", "/api/cadre", "/api/admin"};
    private static final String HEADER_TOKEN = "token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 浏览器跨域时会先发 OPTIONS「预检」请求，通常不带 token；直接放行，否则会 401 导致真正请求发不出去。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String uri = request.getRequestURI();
        // 非三端 API 或登录接口，放行（登录接口放行逻辑见下文）。
        if (!underAnyRoot(uri) || isLoginPath(uri)) {
            chain.doFilter(request, response);
            return;
        }
        String token = request.getHeader(HEADER_TOKEN);
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean underAnyRoot(String uri) {
        for (String root : API_ROOTS) {
            if (uri.equals(root) || uri.startsWith(root + "/")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isLoginPath(String raw) {
        if (raw == null) {
            return false;
        }
        String path = raw;
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return "/login".equals(path) || path.endsWith("/login");
    }
}
