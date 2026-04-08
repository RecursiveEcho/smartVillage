package com.backend.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员VO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthVO {
    private Integer id;// 权限 ID

    private String username;// 用户名

    private String role;// 角色

    private String phone;

    private LocalDateTime createTime;// 创建时间

    private String avatar;//头像URL

    private LocalDateTime updateTime;// 更新时间

    private Integer deleted;// 逻辑删除

    private Integer status;// 状态
}