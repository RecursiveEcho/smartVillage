package com.backend.admin.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.auth.dto.AuthDTO;
import com.backend.auth.vo.AuthVO;
import com.backend.auth.vo.CreateCaderVO;
import com.backend.common.result.Result;

@FeignClient(name = "auth-service")
public interface AuthAdminClient {

   @GetMapping("/internal/auth/users")
  Result<Page<AuthVO>> pageUsers(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Integer status,
      @RequestParam("current") long current,
      @RequestParam("size") long size);

  @GetMapping("/internal/auth/users/{id}")
  Result<AuthVO> getUserDetail(@PathVariable("id") Integer id);

  @PutMapping("/internal/auth/users/{id}/status")
  Result<Void> updateUserStatus(
      @PathVariable("id") Integer id,
      @RequestParam("status") Integer status);

  @PostMapping("/internal/auth/users/cadre")
  Result<CreateCaderVO> createCadre(@RequestBody AuthDTO authDTO);

  @DeleteMapping("/internal/auth/users/{id}")
  Result<Void> deleteUser(@PathVariable("id") Integer id);

}
