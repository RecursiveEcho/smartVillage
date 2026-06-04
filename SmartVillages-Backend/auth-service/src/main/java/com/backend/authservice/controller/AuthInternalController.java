package com.backend.authservice.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthInternalController {

  private final AuthMapper authMapper;

  @GetMapping("/internal/auth/usernames")
  public Result<Map<Integer, String>> getUsernameMap(
      @RequestParam Set<Integer> ids) {

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
}
