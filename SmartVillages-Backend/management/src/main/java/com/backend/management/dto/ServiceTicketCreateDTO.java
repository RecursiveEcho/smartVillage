package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author chenyang
 * &#064;date 2026/4/24
 * &#064;description 民生服务工单创建DTO
 */
@Data
@Schema(description = "民生服务工单创建DTO")
public class ServiceTicketCreateDTO {

    @Schema(description = "类型：subsidy/employment/medical/dispute/other")
    @NotBlank(message = "serviceType不能为空")
    private String serviceType;

    @Schema(description = "标题")
    @NotBlank(message = "title不能为空")
    private String title;

    @Schema(description = "详细描述")
    private String detail;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "附件（JSON数组字符串）")
    private String attachments;
}

