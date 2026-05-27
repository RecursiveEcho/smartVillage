package com.backend.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.backend.common.context.LoginUserContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {

  @Around("@annotation(operationLog)")
  public Object recordOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLog)
      throws Throwable {
    long start = System.currentTimeMillis();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Integer authId = getCurrentAuthId();
    String action = operationLog.value();

    try {
      Object result = joinPoint.proceed();
      long cost = System.currentTimeMillis() - start;
      log.info(
          "operation success, authId={}, action={}, class={}, method={}, cost={}ms",
          authId,
          action,
          className,
          methodName,
          cost);
      return result;
    } catch (Throwable ex) {
      long cost = System.currentTimeMillis() - start;
      log.error(
          "operation failed, authId={}, action={}, class={}, method={}, cost={}ms",
          authId,
          action,
          className,
          methodName,
          cost,
          ex);
      throw ex;
    }
  }

  private Integer getCurrentAuthId() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
      return null;
    }
    HttpServletRequest request = servletRequestAttributes.getRequest();
    return LoginUserContext.getAuthId(request);
  }
}
