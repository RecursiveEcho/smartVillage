package com.backend.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

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

  @Schema(description = "用途说明")
  private String usageRemark;

  @Schema(description = "上传用户")
  private Integer uploadUser;

  @Schema(description = "上传人姓名")
  private String uploadUserName;

  @Schema(description = "状态：0-待审核 1-已通过 2-已拒绝 3-已下架")
  private Integer status;

  @Schema(description = "审核时间")
  private LocalDateTime auditTime;

  @Schema(description = "审核人ID")
  private Integer auditUser;

  @Schema(description = "审核人姓名")
  private String auditUserName;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}
