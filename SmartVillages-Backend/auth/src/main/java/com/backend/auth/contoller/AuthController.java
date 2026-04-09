package com.backend.auth.contoller;

import com.backend.auth.vo.JwtResponse;
import com.backend.auth.dto.LoginRequest;
import com.backend.auth.service.AuthService;
import com.backend.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
/**
 * @author chenyang
 * @date 2026/4/2
 * @description 登录控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "权限管理", description = "权限管理接口")
class AuthController {

    @Resource
    private final AuthService authService;
    
    /**
     * @author chenyang
     * @date 2026/4/2
     * @description 登录
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * @author chenyang
     * @date 2026/4/2
     * @description 登出
     * @return 登出响应
     */
    @Operation(summary = "登出")
    @DeleteMapping("/logout")
    public Result<String> logout() {
        return authService.logout();
    }
}