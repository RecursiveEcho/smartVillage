package com.backend.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.backend.admin.service.AdminService;
import com.backend.common.context.LoginUserContext;
import com.backend.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;
/**
 * @author chenyang
 * @date 2026/3/27
 * @description 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员接口", description = "管理员接口")
public class AdminController {
    @Resource
    private final AdminService adminService;

    // JWT 拦截 v1 验证接口：不带 token 应该 401，带 token 应该 200
    @GetMapping("/me")
    public Result<?> me(HttpServletRequest request) {
        String authId = LoginUserContext.getAuthId(request);
        String username = LoginUserContext.getUsername(request);
        String role = LoginUserContext.getRole(request);
        return Result.success(Map.of(
                "authId", authId,
                "username", username,
                "role", role
        ));
    }
}