package com.backend.mediaservice.support;

import com.backend.auth.entity.AuthEntity;
import com.backend.auth.mapper.AuthMapper;
import com.backend.common.support.AuthUserQueryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalAuthUserQueryService implements AuthUserQueryService {

  private final AuthMapper authMapper;

  @Override
  public Map<Integer, String> getUsernameMap(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
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

    return idToName;
  }
}