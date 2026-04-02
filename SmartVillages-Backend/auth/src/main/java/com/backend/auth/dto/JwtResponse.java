package com.backend.auth.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description JWT 响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private Integer id;// 权限 ID

    private String username;// 用户名

    private String token;
}