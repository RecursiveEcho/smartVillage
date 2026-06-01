package com.backend.common.support;


import java.util.Map;
import java.util.Set;

public interface AuthUserQueryService {

  Map<Integer, String> getUsernameMap(Set<Integer> ids);
}
