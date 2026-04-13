package com.backend.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台用户列表展示 VO（字段与 auth 表对齐部分由服务层填充）。
 *
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员 VO")
public class AdminVO {

    @Schema(description = "用户 ID（auth 主键）")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
