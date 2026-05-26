package com.backend.common.trace;

/**
 * @author chenyang &#064;date 2026/5/8 &#064;description 追踪常量
 */
public class TraceConstants {

  private TraceConstants() {}

  /** 请求头 & 响应头里带的链路 ID */
  public static final String TRACE_ID_HEADER = "X-Trace-Id";

  /** 放进 MDC，给日志 pattern 用：%X{traceId} */
  public static final String MDC_TRACE_ID = "traceId";
}
