package com.backend.common.exception;

import com.backend.common.enums.ErrorCode;
import com.backend.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常 → 统一 {@link Result}，与 {@link ErrorCode} 对齐。
 */

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage(), e);
        if (e.getCode() == ErrorCode.NO_PERMISSION.getCode()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Result.error(ErrorCode.NO_PERMISSION.getCode(), ErrorCode.NO_PERMISSION.getMessage()));
        }
        if (e.getCode() == ErrorCode.RESOURCE_NOT_FOUND.getCode()
                || e.getCode() == ErrorCode.USER_NOT_FOUND.getCode()
                || e.getCode() == ErrorCode.ANNOUNCEMENT_NOT_FOUND.getCode()
                || e.getCode() == ErrorCode.VILLAGE_AFFAIR_NOT_FOUND.getCode()
                || e.getCode() == ErrorCode.MEDIA_NOT_FOUND.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Result.fail(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数验证失败";
        log.warn("参数校验失败: {}", message, e);
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数验证失败");
        log.warn("参数约束违反: {}", message, e);
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败", e);
        return Result.error(ErrorCode.PARAM_INVALID.getCode(), ErrorCode.PARAM_INVALID.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
        String method = e.getHttpMethod().name();
        String path = e.getResourcePath();
        String message = String.format("接口不存在: %s %s", method, path);
        log.warn("{}", message, e);
        return Result.error(ErrorCode.API_NOT_FOUND.getCode(), message);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        String method = e.getMethod();
        String[] supported = e.getSupportedMethods();
        String supportedStr = supported != null ? String.join(", ", supported) : "未知";
        String message = String.format("请求方法 %s 不被支持，支持: %s", method, supportedStr);
        log.warn("{}", message, e);
        return Result.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), message);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleAllUncaughtException(Exception e) {
        log.error("未处理异常", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
