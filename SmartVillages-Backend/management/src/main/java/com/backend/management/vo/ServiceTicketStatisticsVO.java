package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenyang
 * &#064;date  2026/4/24
 * &#064;description  民生服务工单统计VO
 */
@Data
@Schema(description = "民生服务工单统计VO")
public class ServiceTicketStatisticsVO {
    @Schema(description = "民生服务工单总数")
    private Integer total;

    @Schema(description = "民生服务工单待处理数")
    private Integer pending;

    @Schema(description = "民生服务工单处理中数")
    private Integer processing;

    @Schema(description = "民生服务工单已办结数")
    private Integer completed;
}