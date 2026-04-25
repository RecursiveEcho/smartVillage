package com.backend.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("village_house_land")
@Schema(description = "房屋与土地台账实体")
public class VillageHouseLandEntity {

    @TableId(type = IdType.AUTO)
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

    @Schema(description = "权证号")
    private String certNo;

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

