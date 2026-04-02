package com.backend.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenyang
 * @date 2026/3/27
 * @description 管理员实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin")
public class AdminEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;// 管理员 ID

    private String authId;// 管理员的权限ID

    @NotBlank(message = "姓名不能为空")
    @Size(min =2, max = 20, message = "名字长度2-20位")
    private String realName;// 管理员姓名

    @NotBlank(message = "权限不能为空")
    private String permission;// 权限列表

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastLoginTime;// 最后登录时间

    private String lastLoginIp;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}