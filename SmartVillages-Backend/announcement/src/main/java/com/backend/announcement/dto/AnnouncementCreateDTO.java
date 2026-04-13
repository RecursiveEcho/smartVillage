package com.backend.announcement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台创建公告入参。
 *
 * @author chenyang
 * @date 2026/4/8
 * @description 公告创建 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公告创建 DTO")
public class AnnouncementCreateDTO {

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "类型：1-通知 2-公告 3-公示")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "是否置顶：0-否 1-是")
    @NotNull(message = "是否置顶不能为空")
    private Integer isTop;
}
