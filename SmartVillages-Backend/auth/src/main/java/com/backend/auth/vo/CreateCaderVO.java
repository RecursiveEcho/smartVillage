package com.backend.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 创建村干部VO
 * @author chenyang
 * &#064;date 2026/4/22
 * &#064;description 创建村干部VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建村干部VO")
public class CreateCaderVO {
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像")
    private String avatar;
}
