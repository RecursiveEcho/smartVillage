package com.backend.media.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
@Data
@Schema(description = "分页VO")
public class PageVO {

    @Schema(description = "媒体资源id")
    private Integer id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "上传用户")
    private Integer uploadUser;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
