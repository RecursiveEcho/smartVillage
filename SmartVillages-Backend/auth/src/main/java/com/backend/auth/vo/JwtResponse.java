package com.backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功返回的用户标识与 JWT。
 *
 * @author chenyang
 * @date 2026/4/2
 * @description JWT 响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT 响应")
public class JwtResponse {

    @Schema(description = "用户 ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "访问令牌")
    private String token;
}
