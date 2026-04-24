package com.backend.management.vo;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "民生服务工单简单VO")
@Data
public class ServiceTicketSimpleVO {

    @Schema(description = "民生服务工单id")
    private Integer id;

    @Schema(description = "民生服务工单类型")
    private String serviceType;

    @Schema(description = "民生服务工单标题")
    private String title;

    @Schema(description = "民生服务工单状态")
    private Integer status;

    @Schema(description = "民生服务工单创建时间")
    private LocalDateTime createTime;
}