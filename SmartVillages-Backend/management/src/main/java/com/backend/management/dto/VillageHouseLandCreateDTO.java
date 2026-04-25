package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "房屋与土地台账创建DTO")
public class VillageHouseLandCreateDTO {

    @Schema(description = "类型：HOUSE房屋 LAND土地")
    @NotBlank(message = "bizType不能为空")
    private String bizType;

    @Schema(description = "地块/房屋编号")
    private String parcelCode;

    @Schema(description = "坐落")
    private String location;

    @Schema(description = "面积（亩）")
    private BigDecimal areaMu;

    @Schema(description = "权利人/户主")
    private String ownerName;

    @Schema(description = "权证号")
    private String certNo;

    @Schema(description = "备注")
    private String remark;
}

