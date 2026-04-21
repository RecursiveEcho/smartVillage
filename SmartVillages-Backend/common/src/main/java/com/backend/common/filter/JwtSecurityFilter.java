package com.backend.common.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import com.backend.common.utils.JwtUtils;
import java.util.List;
import java.util.Locale;

/**
 * @author chenyang
 * @date 2026/4/16
 * @description JwtSecurityFilter
 */
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
     throws ServletException, IOException {
        String token = request.getHeader("token");
        
        if (StringUtils.hasText(token)) {
            try {
                String subject = jwtUtils.parseToken(token);
                String[] parts = subject.split(":");
                
                String authId = parts[0];
                String username = parts[1];
                String role = parts[2];
                
                request.setAttribute("authId", authId);
                request.setAttribute("username", username);
                request.setAttribute("role", role);

                // 创建认证
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, List.of(new SimpleGrantedAuthority(normalizeRole(role))));
                
                authentication.setDetails(authId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // token 无效时清除认证，让 Spring Security 处理
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
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
