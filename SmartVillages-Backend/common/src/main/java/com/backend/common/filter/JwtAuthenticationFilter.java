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
import java.util.logging.Handler;

/** 仅对三端 API（{@code /api/user|cadre|admin}）验 Header {@code token}；{@code …/login} 与 OPTIONS 放行。 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri=request.getRequestURI();
        if(uri.contains("login")){
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getMethod().equals("OPTIONS")){
            filterChain.doFilter(request,response);
            return;
        }
        String token= request.getHeader("token");
        try {
            jwtUtils.parseToken( token);
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("token 错误");
        }
        filterChain.doFilter(request,response);
    }
}
