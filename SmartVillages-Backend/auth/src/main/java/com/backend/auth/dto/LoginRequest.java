package com.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 登录请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String Id;
    private String username;
    private String password;
}