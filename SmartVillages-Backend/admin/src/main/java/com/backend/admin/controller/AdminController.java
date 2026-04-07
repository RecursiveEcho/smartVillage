package com.backend.admin.controller;

import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.backend.auth.vo.AuthVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.RequestParam;
import com.backend.admin.entity.AdminEntity;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员接口", description = "管理员接口")
public class AdminController {
    
    @Resource
    private final AdminService adminService;

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
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

    /**
     * 分页查询用户
     * @param current 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/users")
    public Result<Page<AdminVO>> pageUser(
        @RequestParam(defaultValue = "1") long current,
        @RequestParam(defaultValue = "10") long size
    ) {
        return Result.success(adminService.pageUsers(current, size));
    }
}