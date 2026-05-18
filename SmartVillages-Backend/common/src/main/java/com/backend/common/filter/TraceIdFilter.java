package com.backend.common.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import com.backend.common.trace.TraceConstants;
import org.springframework.util.StringUtils;
import org.slf4j.MDC;
import java.util.UUID;
/**
 * @author chenyang
 * &#064;date 2026/5/8
 * &#064;description 追踪ID过滤器
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TraceConstants.TRACE_ID_HEADER);
         
        // 如果 traceId 为空，则生成一个
        if (!StringUtils.hasText(traceId)) {
         traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 将 traceId 放入 MDC
         MDC.put(TraceConstants.MDC_TRACE_ID, traceId);
         response.setHeader(TraceConstants.TRACE_ID_HEADER, traceId);

         // 过滤链执行
         try {
            filterChain.doFilter(request, response);
         } finally {
            MDC.clear();
         }
    }
}
