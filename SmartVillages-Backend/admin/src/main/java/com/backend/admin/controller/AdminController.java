package com.backend.admin.controller;

import com.backend.admin.vo.AdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "管理员接口")
public class AdminController {
    
    @Resource
    private final AdminService adminService;

    /**
     * @author chenyang
     * @date 2026/4/2
     * @description 获取当前用户信息
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
     * @author chenyang
     * @date 2026/4/7
     * @description 分页查询用户
     * @param current 当前页
     * @param size 每页数量
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/users")
    public Result<IPage<AdminVO>> pageUser(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) Integer status,
        @RequestParam(defaultValue = "1") long current,
        @RequestParam(defaultValue = "10") long size
    ) {
        return Result.success(adminService.pageUsers(username, role, status, current, size));
    }

    /**
     * @author chenyang
     * @date 2026/4/8
     * @description 启用/禁用用户
     * @param id 用户ID
     * @param status 状态
     * @return 更新用户状态成功
     */
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/users/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Integer id,
                                      @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("更新用户状态成功");
    }

    
}