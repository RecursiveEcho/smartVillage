package com.backend.business.support;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.backend.business.client.AuthUserClient;
import com.backend.common.enums.ErrorCode;
import com.backend.common.exception.BusinessException;
import com.backend.common.result.Result;
import com.backend.common.support.AuthUserQueryService;

import lombok.RequiredArgsConstructor;

@Primary
@Component
@RequiredArgsConstructor
public class RemoteAuthUserQueryService implements AuthUserQueryService {

  private final AuthUserClient authUserClient;

  @Override
  public Map<Integer, String> getUsernameMap(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
    }

    try {
      Result<Map<Integer, String>> result = authUserClient.getUsernameMap(ids);
      if (result == null || result.getData() == null) {
        return Collections.emptyMap();
      }
      return result.getData();
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用 auth-service 查询用户名失败");
    }
  }
}
