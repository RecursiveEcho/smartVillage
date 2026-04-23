package com.backend.feature.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
/**
 * 乡村风采创建DTO
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 热门推荐创建DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "乡村风采创建DTO")
public class HighlightCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "标题")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "内容")
    private String content;
    
    @NotBlank(message = "封面不能为空")
    @Schema(description = "封面")
    private String cover;
    
    @Schema(description = "视频")
    private String video;

    @Schema(description = "图片")
    private String images;

    @NotBlank(message = "类型不能为空")
    @Schema(description = "类型")
    private String type;


}
