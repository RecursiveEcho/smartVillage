package com.backend.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息展示 VO。
 *
 * @author chenyang
 * &#064;date 2026/4/7
 * &#064;description 用户 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户 VO")
public class AuthVO {

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
