package com.backend.common.exception;

import com.backend.common.enums.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常：携带 {@link ErrorCode}，供 {@link GlobalExceptionHandler} 转为统一返回体。
 * <p>
 * 三种用法：仅枚举；仅自定义文案；枚举 + 覆盖文案。
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9196224339724626039L;

    private final ErrorCode errorCode;//错误码
    private final String customMessage;//自定义消息
    private final boolean customMessageMode;//是否用自定义/覆盖消息"模式"

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.customMessageMode = false;
    }

    /**
     * 灵活方式：仅自定义文案；错误码使用 {@link ErrorCode#INVALID_OPERATION}。
     */
    public BusinessException(String customMessage) {
        super(customMessage);
        this.errorCode = ErrorCode.INVALID_OPERATION;
        this.customMessage = customMessage;
        this.customMessageMode = true;
    }

    /** 灵活方式：保留枚举中的 code，消息用自定义覆盖 */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.customMessageMode = true;
    }

    @Override
    public String getMessage() {
        return customMessageMode ? customMessage : errorCode.getMessage();
    }

    /** 业务错误码（数字） */
    public int getCode() {
        return errorCode.getCode();
    }
}
