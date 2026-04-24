package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenyang
 * &#064;date  2026/4/24
 * &#064;description  民生服务工单关闭/退回DTO
 */
@Data
@Schema(description = "民生服务工单关闭/退回DTO")
public class ServiceTicketCloseDTO {

    @Schema(description = "处理说明")
    private String handleNote;
}

