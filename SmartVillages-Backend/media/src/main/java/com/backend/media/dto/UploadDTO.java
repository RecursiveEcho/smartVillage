package com.backend.media.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 上传DTO
 * @author chenyang
 * @date 2026/4/20
 * @description 上传DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "上传DTO")
public class UploadDTO {

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "文件名")
    private String fileName;
    
    @NotBlank(message = "文件URL不能为空")
    @Schema(description = "图片URL")
    private String fileUrl;

    @NotBlank(message = "文件类型不能为空")
    @Schema(description = "文件类型")
    private String fileType;

    @NotBlank(message = "对象键不能为空")
    @Schema(description = "对象键")
    private String objectKey;
}
