package com.backend.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * @author chenyang
 * &#064;date 2026/4/25
 * &#064;description 人口台账实体
 */
@Data
@TableName("village_population")
@Schema(description = "人口台账实体")
public class VillagePopulationEntity {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

