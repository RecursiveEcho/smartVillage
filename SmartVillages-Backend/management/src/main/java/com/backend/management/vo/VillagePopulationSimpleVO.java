package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "人口台账列表VO")
public class VillagePopulationSimpleVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "户号")
    private String householdNo;

    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

