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
 * @date 2026/3/27
 * @description 字符编码过滤器
 */
@Component
@RequiredArgsConstructor
public class CharacterEncodingFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if(requestURI.contains("/api/login")){
            filterChain.doFilter(request, response);
        }
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
        }
        try{
            JwtUtils.parseToken(token);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}