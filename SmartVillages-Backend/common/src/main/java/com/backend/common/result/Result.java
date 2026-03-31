package com.backend.common.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Schema(description = "统一响应包装类")
public class Result<T> {

    // 成功消息
    public static final String SUCCESS_MESSAGE = "success";
    // 成功状态码
    public static final Integer SUCCESS_CODE = 200;

    @Schema(description = "响应状态码，200表示成功", example = "200")
    private Integer code;

    @Schema(description = "响应消息", example = "success")
    private String message;

    @Schema(description = "响应数据")
    private T data;

//    @Schema(description = "追踪ID，用于链路追踪", example = "04552a3a476749d98ea34f3568e3f67c")
//    private String traceId = MDC.get("traceId");

    // 私有构造函数
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    // 业务失败
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 系统级错误
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}

