package com.backend.common.filter;

import com.backend.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description JWT 认证过滤器
 */
/** 除白名单路径外校验 Header {@code token}；白名单含 {@code /auth/login}、Knife4j/OpenAPI 静态资源；OPTIONS 放行。 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isWhitelist(uri) || "OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request,response);
            return;
        }
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try {
            jwtUtils.parseToken(token);
            filterChain.doFilter(request,response);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isWhitelist(String uri) {
        if (uri == null) {
            return false;
        }
        return uri.contains("/auth/login")
                || uri.contains("/v3/api-docs")
                || uri.contains("/doc.html")
                || uri.startsWith("/swagger-ui")
                || uri.contains("/swagger-ui/")
                || uri.startsWith("/webjars")
                || uri.contains("/webjars/");
    }
}
