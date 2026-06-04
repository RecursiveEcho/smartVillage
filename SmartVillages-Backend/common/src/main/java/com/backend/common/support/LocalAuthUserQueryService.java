package com.backend.common.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 本地直接查询 auth 表的 AuthUserQueryService 实现。
 * 用于 monolith 单体模式（service 模块），替代微服务下的 RemoteAuthUserQueryService。
 */
@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class LocalAuthUserQueryService implements AuthUserQueryService {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Map<Integer, String> getUsernameMap(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyMap();
    }

    try {
      StringBuilder sql = new StringBuilder("SELECT id, username FROM auth WHERE id IN (");
      // Build placeholders
      Object[] params = ids.toArray();
      String placeholder = String.join(",", Collections.nCopies(ids.size(), "?"));
      sql.append(placeholder).append(")");

      Map<Integer, String> result = new HashMap<>();
      jdbcTemplate.query(sql.toString(), params, rs -> {
        result.put(rs.getInt("id"), rs.getString("username"));
      });
      return result;
    } catch (Exception e) {
      log.warn("LocalAuthUserQueryService query failed", e);
      return Collections.emptyMap();
    }
  }
}
