package com.backend.admin.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员VO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVO {
    private Integer id;// 管理员ID
    private String username;
    private String role;// 角色
    private String phone;// 手机号
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime updateTime;
    private Integer deleted;
    private Integer status;
}