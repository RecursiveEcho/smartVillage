package com.backend.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员表 {@code admin} 对应的持久化实体。
 * <p>
 * 与认证用户（{@code auth} 表）通过 {@link #authId} 关联；权限展示等字段与业务扩展在此维护。
 *
 * @author chenyang
 * &#064;date 2026/4/7
 * &#064;description 管理员实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin")
@Schema(description = "管理员实体类")
public class AdminEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "管理员ID")
    private Integer id;

    @Schema(description = "关联 auth 表 ID")
    private Integer authId;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "权限列表 (JSON)")
    private List<String> permissions;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录 IP")
    private String lastLoginIp;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除：0-未删除 1-已删除")
    private Integer deleted;
}
