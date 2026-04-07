package com.backend.auth.service;

import com.backend.auth.vo.JwtResponse;
import com.backend.auth.entity.AuthEntity;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
/**
 * @author chenyang
 * @date 2026/4/2
 * @description 认证服务
 */
public interface AuthService extends IService<AuthEntity> {
    Result<JwtResponse> login(String username, String password);
    Result<String> logout();
}