package com.backend.auth.service.impl;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.auth.service.AuthService;
import com.backend.auth.vo.JwtResponse;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.result.Result;
import com.backend.common.utils.JwtUtils;
import com.backend.common.utils.RedisDistributedLock;
import com.backend.common.utils.RedisRateLimiter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/** 认证业务：校验账号状态与密码（明文入参 -> 服务端 MD5 校验），签发 JWT。 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl extends ServiceImpl<AuthMapper, AuthEntity> implements AuthService {

  private final HttpServletRequest request;
  private final RedisRateLimiter redisRateLimiter;
  private final RedisDistributedLock redisDistributedLock;
  private final AuthMapper authMapper;
  private final JwtUtils jwtUtils;

  /** 登录主流程：非空校验 -> 查用户 -> 逻辑删除/禁用拦截 -> MD5 比对 -> 生成 token。 */
  @Override
  public JwtResponse login(String username, String password) {
    // 限流检查
    String clientIp = request.getRemoteAddr();
    String rateKey = "rate_limit:login:" + clientIp + ":" + username;
    redisRateLimiter.check(rateKey, 5);
    if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
      throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }

    AuthEntity user = findByUsername(username);
    if (user == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    // 用户已逻辑删除
    if (Objects.equals(user.getIsDeleted(), 1)) {
      throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }
    // 用户已禁用
    if (Objects.equals(user.getStatus(), 0)) {
      throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
    }

    if (!passwordMatches(password, user.getPassword())) {
      throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }

    String token =
        jwtUtils.generateToken(String.valueOf(user.getId()), user.getUsername(), user.getRole());
    return new JwtResponse(
        user.getId(), user.getUsername(), user.getRole(), user.getAvatar(), token);
  }

  @Override
  public String logout() {
    return Result.SUCCESS_MESSAGE;
  }

  /** 绑定上传后的媒体 URL 到用户记录；目前仅支持头像（slot=AVATAR）。 */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void bindUploadedMedia(
      Long userId, String slot, String mediaUrl, String uploadedFileType, Integer operatorUserId) {
    String lockKey = "lock:bindUpload:user:" + userId;
    String lockInstance = RedisDistributedLock.generateInstanceId();
    boolean locked = redisDistributedLock.tryLock(lockKey, lockInstance);
    if (!locked) {
      throw new BusinessException(ErrorCode.SYSTEM_BUSY, "上传功能业务繁忙");
    }
    if (userId == null) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "用户 ID 不能为空");
    }
    AuthEntity entity = getById(userId);
    if (entity == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    if (!"AVATAR".equalsIgnoreCase(slot.trim())) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "bindSlot 须为 AVATAR");
    }
    if (!"image".equals(uploadedFileType)) {
      throw new BusinessException(ErrorCode.PARAM_INVALID, "头像仅支持图片");
    }
    try {
      entity.setAvatar(mediaUrl);
      updateById(entity);
    } finally {
      redisDistributedLock.unlock(lockKey, lockInstance);
    }
  }

  /** 按用户名取一条记录；依赖库侧唯一约束或业务保证不重复。 */
  private AuthEntity findByUsername(String username) {
    return authMapper.selectOne(
        new LambdaQueryWrapper<AuthEntity>().eq(AuthEntity::getUsername, username).last("LIMIT 1"));
  }

  private boolean passwordMatches(String rawPassword, String storedMd5) {
    String hashedInput = DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
    return hashedInput.equals(storedMd5);
  }
}
