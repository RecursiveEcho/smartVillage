package com.backend.media.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 上传VO
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 上传VO
 */
@Data
@NoArgsConstructor
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

    @Schema(description = "用途说明（入库后回显）")
    private String usageRemark;

    @Schema(description = "上传人账号 ID（当前登录用户）")
    private Integer uploadUserId;
}