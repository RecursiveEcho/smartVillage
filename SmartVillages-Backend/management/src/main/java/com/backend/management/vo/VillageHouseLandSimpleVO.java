package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "房屋与土地台账列表VO")
public class VillageHouseLandSimpleVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "类型：HOUSE房屋 LAND土地")
    private String bizType;

    @Schema(description = "地块/房屋编号")
    private String parcelCode;

    @Schema(description = "坐落")
    private String location;

    @Schema(description = "面积（亩）")
    private BigDecimal areaMu;

    @Schema(description = "权利人/户主")
    private String ownerName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

