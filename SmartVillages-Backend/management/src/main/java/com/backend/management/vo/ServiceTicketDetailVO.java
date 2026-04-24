package com.backend.management.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenyang
 * &#064;date  2026/4/24
 * &#064;description  民生服务工单详情VO
 */
@Data
@Schema(description = "民生服务工单详情VO")
public class ServiceTicketDetailVO {
    @Schema(description = "民生服务工单id")
    private Integer id;

    @Schema(description = "民生服务工单类型")
    private String serviceType;

    @Schema(description = "民生服务工单标题")
    private String title;

    @Schema(description = "民生服务工单详细描述")
    private String detail;

    @Schema(description = "民生服务工单联系电话")
    private String contactPhone;

    @Schema(description = "民生服务工单附件")
    private String attachments;

    @Schema(description = "民生服务工单状态")
    private Integer status;

    @Schema(description = "民生服务工单申请人id")
    private Integer applicantId;

    @Schema(description = "民生服务工单处理人id")
    private Integer handlerId;

    @Schema(description = "民生服务工单处理说明")
    private String handleNote;

    @Schema(description = "民生服务工单处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "民生服务工单创建时间")
    private LocalDateTime createTime;

    @Schema(description = "民生服务工单更新时间")
    private LocalDateTime updateTime;
}
