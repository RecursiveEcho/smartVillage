package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author chenyang
 * &#064;date 2026/4/25
 * &#064;description 人口台账创建DTO
 */
@Data
@Schema(description = "人口台账创建DTO")
public class VillagePopulationCreateDTO {

    @Schema(description = "户号")
    private String householdNo;

    @Schema(description = "姓名")
    @NotBlank(message = "fullName不能为空")
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
}

