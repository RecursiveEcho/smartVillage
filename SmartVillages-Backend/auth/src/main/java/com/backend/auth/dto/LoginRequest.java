package com.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求体（当前登录逻辑仅使用用户名与密码）。
 *
 * @author chenyang
 * @date 2026/4/2
 * @description 登录请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录请求")
public class LoginRequest {

    /** 预留字段，服务端未参与校验逻辑。 */
    @Schema(description = "预留 ID，可选")
    private String id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码（32 位小写 MD5 十六进制）")
    @NotBlank(message = "密码不能为空")
    @Size(min = 32, max = 32, message = "密码 MD5 必须为 32 位（小写十六进制）")
    @Pattern(regexp = "^[0-9a-f]{32}$", message = "密码必须是小写 32 位 MD5 十六进制")
    private String password;
}
