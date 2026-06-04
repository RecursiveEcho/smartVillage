package com.backend.mediaservice.client;

import java.util.Map;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.common.result.Result;

@FeignClient(name = "auth-user-client", url = "${services.auth.url}")
public interface AuthUserClient {

  @GetMapping("/internal/auth/usernames")
  Result<Map<Integer, String>> getUsernameMap(@RequestParam("ids") Set<Integer> ids);
}
