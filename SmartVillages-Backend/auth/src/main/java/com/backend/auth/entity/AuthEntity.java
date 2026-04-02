package com.backend.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenyang
 * @date 2026/4/2
 * @description 权限实体类
 */
@Data 
@AllArgsConstructor
@NoArgsConstructor
@TableName("auth")
public class AuthEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;// 权限 ID

    @NotBlank(message = "用户名不能为空")
    private String username;// 用户名

    @NotBlank(message = "密码不能为空")
    @Size(min = 32, max = 32, message = "密码 MD5 必须为 32 位（小写十六进制）")
    @Pattern(regexp = "^[0-9a-f]{32}$", message = "密码必须是小写 32 位 MD5 十六进制")
    private String password;// 密码

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{8}$", message = "手机号格式错误")
    private String phone;// 手机号

    @NotBlank(message = "角色不能为空")
    private String role;// 角色

    private Integer status;// 状态

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;// 逻辑删除

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;// 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;// 更新时间

}