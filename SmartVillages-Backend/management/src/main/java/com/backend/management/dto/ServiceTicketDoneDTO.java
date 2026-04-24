package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author chenyang
 * &#064;date  2026/4/24
 * &#064;description  民生服务工单办结DTO
 */
@Data
@Schema(description = "民生服务工单办结DTO")
public class ServiceTicketDoneDTO {

    @Schema(description = "处理说明")
    @NotBlank(message = "handleNote不能为空")
    private String handleNote;
}

