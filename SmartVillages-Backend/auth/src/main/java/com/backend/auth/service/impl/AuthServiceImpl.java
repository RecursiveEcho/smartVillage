package com.backend.auth.service.impl;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.auth.service.AuthService;
import com.backend.auth.vo.JwtResponse;
import com.backend.common.enums.ErrorCode;
import com.backend.common.result.Result;
import com.backend.common.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 认证业务：校验账号状态与密码（明文入参 -> 服务端 MD5 校验），签发 JWT。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl extends ServiceImpl<AuthMapper, AuthEntity> implements AuthService {

    private final AuthMapper authMapper;
    private final JwtUtils jwtUtils;

    /**
     * 登录主流程：非空校验 → 查用户 → 逻辑删除/禁用拦截 → MD5 比对 → 生成 token。
     */
    @Override
    public Result<JwtResponse> login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), ErrorCode.LOGIN_FAILED.getMessage());
        }

        AuthEntity user = findByUsername(username);
        if (user == null) {
            return Result.fail(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        /** 用户已逻辑删除 */
        if (Objects.equals(user.getIsDeleted(), 1)) {
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), ErrorCode.LOGIN_FAILED.getMessage());
        }
        /** 用户已禁用 */
        if (Objects.equals(user.getStatus(), 0)) {
            return Result.fail(ErrorCode.ACCOUNT_DISABLED.getCode(), ErrorCode.ACCOUNT_DISABLED.getMessage());
        }

        if (!passwordMatches(password, user.getPassword())) {
            return Result.fail(ErrorCode.LOGIN_FAILED.getCode(), ErrorCode.LOGIN_FAILED.getMessage());
        }

        String token = jwtUtils.generateToken(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getRole());
        return Result.success(new JwtResponse(token));
    }

    @Override
    public Result<String> logout() {
        return Result.success(Result.SUCCESS_MESSAGE);
    }

    /** 按用户名取一条记录；依赖库侧唯一约束或业务保证不重复 */
    private AuthEntity findByUsername(String username) {
        return authMapper.selectOne(
                new LambdaQueryWrapper<AuthEntity>()
                        .eq(AuthEntity::getUsername, username)
                        .last("LIMIT 1"));
    }

    private boolean passwordMatches(String rawPassword, String storedMd5) {
          String hashedInput= DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
          return hashedInput.equals(storedMd5);
    }
}
