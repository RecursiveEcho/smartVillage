package com.backend.auth.controller;

import com.backend.auth.dto.LoginRequest;
import com.backend.auth.service.AuthService;
import com.backend.auth.vo.JwtResponse;
import com.backend.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关 HTTP：登录颁发 JWT，登出多为无状态客户端丢弃令牌（此处返回成功文案）。
 * <p>
 * 路径前缀 {@code /auth}。
 *
 * @author chenyang
 * &#064;date 2026/4/2
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 校验账号密码后返回 JWT；哈希规则与错误码见 {@link AuthService#login}。
     *
     * @param loginRequest 用户名、密码
     * @return 用户 id、用户名、access token
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * 无服务端会话时可仅返回成功；若接入令牌黑名单，可在此扩展。
     *
     * @return 统一成功提示
     */
    @Operation(summary = "登出")
    @DeleteMapping("/logout")
    public Result<String> logout() {
        return authService.logout();
    }
}
