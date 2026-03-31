package com.backend.common.enums;

/**
 * 业务错误码枚举（智慧乡村 smartVillages）
 * <p>
 * 设计原则：
 * - 按业务模块分码段，便于前后端对照与日志检索
 * - 语义化命名；同一语义场景下 code 唯一
 * </p>
 */
public enum ErrorCode {

    // ==================== 用户与认证 (1xxx) ====================
    USER_NOT_FOUND(1001, "用户不存在"),
    LOGIN_FAILED(1002, "用户名或密码错误"),
    LOGIN_EXPIRED(1003, "登录已过期，请重新登录"),
    INVALID_TOKEN(1004, "无效的令牌"),
    INVALID_AUTH_HEADER(1005, "无效的认证头"),

    USERNAME_EXISTS(1011, "用户名已存在"),
    PHONE_EXISTS(1013, "手机号已被注册"),
    PWD_MISMATCH(1014, "两次输入的密码不一致"),
    PHONE_FORMAT_ERROR(1016, "手机号格式不正确"),
    PWD_EMPTY(1018, "密码不能为空"),

    ACCOUNT_DISABLED(1021, "账号已停用"),
    ACCOUNT_LOCKED(1022, "账号已锁定"),

    // ==================== 权限与角色 (2xxx) ====================
    NO_PERMISSION(2001, "没有操作权限"),
    ACCESS_DENIED(2002, "访问被拒绝"),

    ROLE_NOT_FOUND(2011, "角色不存在"),
    ROLE_NAME_EXISTS(2012, "角色名称已存在"),
    ROLE_IN_USE(2015, "角色存在关联用户，无法删除"),

    PERMISSION_CODE_EXISTS(2021, "权限编码已存在"),
    CORE_PERMISSION_UPDATE_FORBIDDEN(2023, "系统核心权限不可修改"),
    CORE_PERMISSION_DELETE_FORBIDDEN(2024, "系统核心权限不可删除"),
    CORE_PERMISSION_SWITCH_FORBIDDEN(2025, "系统核心权限不可禁用"),

    // ==================== 文件与资源 (3xxx) ====================
    FILE_EMPTY(3001, "上传文件为空"),
    FILE_FORMAT_ERROR(3002, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(3003, "文件大小超出限制"),
    FILE_UPLOAD_FAILED(3004, "文件上传失败"),

    CONFIG_NOT_FOUND(3021, "配置项不存在"),
    CONFIG_VALUE_INVALID(3022, "配置值无效"),

    // ==================== 通用业务 (4xxx) ====================
    INVALID_OPERATION(4001, "非法操作"),
    OPERATION_NOT_ALLOWED(4002, "当前状态不允许此操作"),
    RESOURCE_NOT_FOUND(4003, "资源不存在"),
    RESOURCE_EXISTS(4004, "资源已存在"),
    DATA_IN_USE(4005, "数据正在使用，无法删除"),
    DUPLICATE_SUBMIT(4006, "请勿重复提交"),

    PARAM_INVALID(4011, "请求参数无效"),
    PARAM_MISSING(4012, "缺少必要参数"),
    PARAM_FORMAT_ERROR(4013, "参数格式错误"),

    STATUS_INVALID(4021, "状态不合法"),
    WORKFLOW_ERROR(4023, "审核流程处理失败"),

    FOREIGN_KEY_ERROR(4032, "存在关联数据，无法删除"),
    UNIQUE_CONSTRAINT_ERROR(4033, "数据已存在，不能重复"),

    RATE_LIMIT_EXCEEDED(4041, "请求过于频繁，请稍后重试"),

    // ==================== 村务业务域（可按模块继续扩展 41xx、42xx…）====================
    ANNOUNCEMENT_NOT_FOUND(4101, "公告不存在或已下架"),
    VILLAGE_AFFAIR_NOT_FOUND(4102, "村务事项不存在或已下架"),
    INTERACTION_CLOSED(4103, "留言已关闭，无法回复"),
    MEDIA_NOT_FOUND(4104, "媒体资源不存在"),

    // ==================== 系统错误 (5xxx) ====================
    SYSTEM_ERROR(5001, "系统内部错误"),
    DB_ERROR(5002, "数据库操作失败"),
    EXTERNAL_SERVICE_ERROR(5004, "外部服务调用失败"),
    CACHE_ERROR(5005, "缓存操作失败"),

    SYSTEM_BUSY(5011, "系统繁忙，请稍后重试"),
    TIMEOUT_ERROR(5013, "请求超时"),

    UNKNOWN_ERROR(5099, "未知错误"),

    // ==================== 路由与 HTTP 语义 (6xxx) ====================
    API_NOT_FOUND(6001, "接口不存在"),
    METHOD_NOT_ALLOWED(6002, "请求方法不被支持");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
