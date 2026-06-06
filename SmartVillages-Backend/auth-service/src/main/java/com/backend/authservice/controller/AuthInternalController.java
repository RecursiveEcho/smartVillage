package com.backend.authservice.controller;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.dto.AuthDTO;
import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.auth.vo.AuthVO;
import com.backend.auth.vo.CreateCaderVO;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthInternalController {

  private final AuthMapper authMapper;

  // 供内部调用，根据用户 ID 列表查询用户名映射。返回的 Map 中仅包含存在且未被删除的用户。
  @GetMapping("/internal/auth/usernames")
  public Result<Map<Integer, String>> getUsernameMap(@RequestParam Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Result.success(Collections.emptyMap());
    }

    Map<Integer, String> idToName = new HashMap<>();
    authMapper
        .selectList(new LambdaQueryWrapper<AuthEntity>().in(AuthEntity::getId, ids))
        .forEach(
            auth -> {
              if (auth.getId() != null) {
                idToName.put(auth.getId(), auth.getUsername());
              }
            });

    return Result.success(idToName);
  }

  // 供管理端调用的用户分页查询接口，支持按用户名（模糊）、角色、状态过滤。返回的分页结果仅包含存在且未被删除的用户。
  @GetMapping("/internal/auth/users")
  public Result<IPage<AuthVO>> pageUsers(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Integer status,
      @RequestParam(defaultValue = "1") long current,
      @RequestParam(defaultValue = "10") long size) {

    LambdaQueryWrapper<AuthEntity> wrapper =
        new LambdaQueryWrapper<AuthEntity>()
            .like(StringUtils.hasText(username), AuthEntity::getUsername, username)
            .eq(role != null, AuthEntity::getRole, role)
            .eq(status != null, AuthEntity::getStatus, status)
            .orderByDesc(AuthEntity::getStatus)
            .orderByDesc(AuthEntity::getCreateTime)
            .orderByAsc(AuthEntity::getId);

    IPage<AuthEntity> result = authMapper.selectPage(new Page<>(current, size), wrapper);
    return Result.success(result.convert(this::toVO));
  }

  // 供管理端调用的用户详情查询接口。返回的用户信息仅当用户存在且未被删除时才会返回，否则返回资源未找到错误。
  @GetMapping("/internal/auth/users/{id}")
  public Result<AuthVO> getUserDetail(@PathVariable Integer id) {
    AuthEntity entity = authMapper.selectById(id);
    if (entity == null) {
      return Result.success(null);
    }
    return Result.success(toVO(entity));
  }

  // 供管理端调用的用户状态更新接口。仅当用户存在且未被删除时才允许更新，否则返回相应的错误。
  @PutMapping("/internal/auth/users/{id}/status")
  public Result<Void> updateUserStatus(@PathVariable Integer id, @RequestParam Integer status) {
    AuthEntity entity = authMapper.selectById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    if (Objects.equals(entity.getIsDeleted(), 1)) {
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
    }
    entity.setStatus(status);
    authMapper.updateById(entity);
    return Result.success();
  }

  // 供管理端调用的村干部账号创建接口。仅创建角色为 "cadre" 的用户，并且默认启用状态。返回创建后的用户信息。
  @PostMapping("/internal/auth/users/cadre")
  public Result<CreateCaderVO> createCadre(@Valid @RequestBody AuthDTO authDTO) {
    AuthEntity entity = new AuthEntity();
    String password =
        DigestUtils.md5DigestAsHex(authDTO.getPassword().getBytes(StandardCharsets.UTF_8));

    BeanUtils.copyProperties(authDTO, entity);
    entity.setPassword(password);
    entity.setRole("cadre");
    entity.setStatus(1);
    entity.setAvatar(authDTO.getAvatar());

    authMapper.insert(entity);

    CreateCaderVO vo = new CreateCaderVO();
    BeanUtils.copyProperties(entity, vo);
    return Result.success(vo);
  }

  //
  @DeleteMapping("/internal/auth/users/{id}")
  public Result<Void> deleteUser(@PathVariable Integer id) {
    AuthEntity entity = authMapper.selectById(id);
    if (entity == null) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    authMapper.deleteById(id);
    return Result.success();
  }

  private AuthVO toVO(AuthEntity entity) {
    AuthVO vo = new AuthVO();
    BeanUtils.copyProperties(entity, vo);
    return vo;
  }
}