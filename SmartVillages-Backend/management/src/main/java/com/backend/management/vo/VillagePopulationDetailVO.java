package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * @author chenyang
 * &#064;date 2026/4/25
 * &#064;description 人口台账详情VO
 */
@Data
@Schema(description = "人口台账详情VO")
public class VillagePopulationDetailVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "户号")
    private String householdNo;

    @Schema(description = "姓名")
    private String fullName;

    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "身份证后四位")
    private String idCardLast4;

    @Schema(description = "与户主关系")
    private String relationToHead;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

