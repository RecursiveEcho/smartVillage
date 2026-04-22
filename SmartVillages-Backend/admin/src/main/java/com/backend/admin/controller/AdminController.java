package com.backend.admin.controller;

import com.backend.admin.service.AdminService;
import com.backend.admin.vo.AdminVO;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import com.backend.auth.dto.AuthDTO;
import com.backend.media.vo.UploadVO;
import com.backend.auth.vo.AuthVO;
/**
 * 管理员侧 HTTP 接口：当前登录信息、用户分页与状态维护。
 * <p>
 * 路径统一前缀 {@code /admin}；用户列表与状态实际作用于认证表数据，经 {@link AdminService} 编排。
 *
 * @author chenyang
 * &#064;date 2026/4/2
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "管理员接口")
public class AdminController {

    private final AdminService adminService;

    /**
     * @author chenyang
     * &#064;date 2026/4/2
     * &#064;description 获取当前用户信息
     * @param request HTTP 请求（解析登录上下文）
     * @return 当前用户 authId、username、role
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me(HttpServletRequest request) {
        //获取上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 返回
        log.info("当前用户：authId={}, username={}, role={}", authentication.getPrincipal(), authentication.getName(), authentication.getAuthorities());
        return Result.success(Map.of(
                "id", String.valueOf(authentication.getDetails()),
                "username", authentication.getName(),
                "role", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","))
        )); 
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/7
     * &#064;description 分页查询用户
     * @param username 用户名（可选，预留查询条件）
     * @param role     角色（可选）
     * @param status   状态（可选）
     * @param current  当前页，默认 1
     * @param size     每页条数，默认 10
     * @return 分页后的 {@link AdminVO} 列表
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/users")
    public Result<IPage<AuthVO>> pageUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return Result.success(adminService.pageUsers(username, role, status, current, size));
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/8
     * &#064;description 启用/禁用用户
     * @param id     用户 ID
     * @param status 状态
     * @return 操作结果文案
     */
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success("更新用户状态成功");
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/18
     * &#064;description 创建村干部账号
     * @param authDTO 账号信息
     * @return 操作结果文案
     */
    @Operation(summary = "创建村干部")
    @PostMapping("users/cadre")
    public Result<Integer> createCadre(@Valid @RequestBody AuthDTO authDTO)  {
        Integer id = adminService.createCadre(authDTO);
        return Result.success(id);
    }

    /**
     * @author chenyang
     * &#064;date 2026/4/21
     * &#064;description 上传头像
     * @param avatar 头像文件
     * @param request HTTP 请求
     * @return 上传结果
     */
    @Operation(summary = "上传头像")
    @PostMapping(value = "/users/cadre/avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadCadreAvatar(
            @PathVariable Integer id,
            MultipartFile avatar,
            HttpServletRequest request
    ) {
        UploadVO uploadVO = adminService.uploadCadreAvatar(id, avatar, request);
        return Result.success(uploadVO.getFileUrl());
    }
}
