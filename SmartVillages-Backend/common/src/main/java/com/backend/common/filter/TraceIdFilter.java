package com.backend.common.filter;

import com.backend.common.trace.TraceConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/** 追踪 ID 过滤器。 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
          HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {
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
