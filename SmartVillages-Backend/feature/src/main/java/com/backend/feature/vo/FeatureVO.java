package com.backend.feature.vo;

import java.time.LocalDateTime;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
@Data
@Schema(description = "乡村风采VO")
public class FeatureVO {

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "封面")
    private String cover;

    @Schema(description = "视频")
    private String video;
    
    @Schema(description = "图片")
    private String images;

    @Schema(description = "创建用户")
    private Integer createUser;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
