package com.backend.common.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import com.backend.common.utils.JwtUtils;
import com.backend.common.enums.ErrorCode;
import com.backend.common.config.IsWhitelistConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.common.result.Result;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.MediaType;
import java.util.Locale;

/**
 * @author chenyang
 * @date 2026/4/16
 * @description JwtSecurityFilter
 */
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;
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
            writeError(response, ErrorCode.INVALID_AUTH_HEADER);
            return;
        }

        try{
            String subject = jwtUtils.parseToken(token);
            String[] parts = subject.split(":");
            String userId= parts[0];
            String username= parts[1];
            String role= parts[2];
            UsernamePasswordAuthenticationToken authentication= new
                    UsernamePasswordAuthenticationToken(username,null,
                    List.of(new SimpleGrantedAuthority(normalizeRole(role))));
            authentication.setDetails(userId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch(Exception e){
            writeError(response, ErrorCode.INVALID_TOKEN);
            return;
        }
        filterChain.doFilter(request,response);
    }

    private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(errorCode.getCode(), errorCode.getMessage())));
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim();//trim（）去除字符串首尾空格
        if (normalized.regionMatches(true, 0, "ROLE_", 0, 5)/* regionMatches（）判断字符串是否相等*/) {
            normalized = normalized.substring(5);//substring（）截取字符串
        }
        // 将字符串转换为大写
        return "ROLE_" + normalized.toUpperCase(Locale.ROOT);
    }
}
