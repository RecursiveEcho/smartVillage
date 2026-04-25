package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "村务事项审核DTO")
public class VillageAffairAuditDTO {

    @Schema(description = "审核结果：2通过发布 1打回待审核（保持待审核但写备注） 3下架")
    @NotNull(message = "status不能为空")
    private Integer status;

    @Schema(description = "审核备注/驳回原因")
    private String auditRemark;
}

