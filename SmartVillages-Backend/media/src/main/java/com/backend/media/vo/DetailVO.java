package com.backend.media.vo;

import java.time.LocalDateTime;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
@Data
@Schema(description = "媒体资源详情VO")
public class DetailVO {

    @Schema(description = "媒体资源id")
    private Integer id;
    
    @Schema(description = "文件名")
    private String fileName;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件类型")
    private String fileType;
    
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

    @Schema(description = "逻辑删除")
    private Integer deleted;
}
