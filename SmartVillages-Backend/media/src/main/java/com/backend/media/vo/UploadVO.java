package com.backend.media.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 上传VO
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 上传VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "上传VO")
public class UploadVO {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "对象键")
    private String objectKey;
}