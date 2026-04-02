package com.backend.auth.service.impl;

import com.backend.auth.dto.JwtResponse;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.auth.service.AuthService;
import com.backend.common.enums.ErrorCode;
import com.backend.common.result.Result;
import com.backend.common.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
/**
 * @author chenyang
 * @date 2026/4/2
 * @description 认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<AuthMapper, AuthEntity> implements AuthService {

    private final AuthMapper authMapper;
    @Override
    public Result<JwtResponse> login(String username, String password) {
        if(!StringUtils.hasText(username)||!StringUtils.hasText(password)){
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), "账号或密码为空");
        }
        AuthEntity user = authMapper.selectOne(new LambdaQueryWrapper<AuthEntity>()
                .eq(AuthEntity::getUsername, username)
                .last("LIMIT 1"));
        if(user == null){
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), "用户不存在");
        }
        if(user.getDeleted() != null && user.getDeleted() == 1){
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), ErrorCode.LOGIN_FAILED.getMessage());
        }
        if(user.getStatus() != null && user.getStatus() == 0){
            return Result.fail(ErrorCode.ACCOUNT_DISABLED.getCode(), ErrorCode.ACCOUNT_DISABLED.getMessage());
        }
        String md5Password = DigestUtils.md5DigestAsHex(
                Objects.requireNonNull(password).getBytes(StandardCharsets.UTF_8)
        );
        if(!md5Password.equals(user.getPassword())){
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), "密码错误");
        }
        String token = JwtUtils.generateToken(String.valueOf(user.getId()), user.getUsername(), user.getRole());

        return Result.success(new JwtResponse(user.getId(), user.getUsername(), token));
    }

    @Override
    public Result<String> logout() {
        
    return Result.success("退出登录成功 ");        
    }
}