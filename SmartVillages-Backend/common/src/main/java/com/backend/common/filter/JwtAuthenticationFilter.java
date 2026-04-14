package com.backend.common.filter;

import com.backend.common.config.IsWhitelistConfig;
import com.backend.common.enums.ErrorCode;
import com.backend.common.result.Result;
import com.backend.common.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper objectMapper;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
     throws ServletException, IOException {

        String uri = request.getRequestURI();
        boolean isWhitelist = IsWhitelistConfig.getWhitelist().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));

        if (isWhitelist|| "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response, ErrorCode.INVALID_AUTH_HEADER);
            return;
        }

        try {
            jwtUtils.parseToken(token);
        }catch (Exception e){
            writeUnauthorized(response, ErrorCode.INVALID_TOKEN);
            return;
        }
        filterChain.doFilter(request,response);
    }

    private void writeUnauthorized(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Result<?> body = Result.error(errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
