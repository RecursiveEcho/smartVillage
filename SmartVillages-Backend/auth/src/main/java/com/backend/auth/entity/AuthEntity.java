package com.backend.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 登录账号表 {@code auth} 对应的实体，承载用户名密码、角色与账号状态。
 * <p>
 * 密码存库为 MD5（UTF-8 明文）小写 32 位十六进制；删除走逻辑删除字段 {@link #deleted}。
 *
 * @author chenyang
 * @date 2026/4/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("auth")
@Schema(description = "用户认证实体类")
public class AuthEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码：MD5(UTF-8 明文) 小写十六进制 32 位")
    private String password;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色：admin-管理员/cadre-村干部/villager-村民")
    private String role;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除：0-否 1-是", defaultValue = "0")
    @TableLogic(value = "0", delval = "1")
    @TableField("is_deleted")
    private Integer deleted;
}
