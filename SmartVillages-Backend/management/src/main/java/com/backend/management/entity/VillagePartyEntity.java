package com.backend.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("village_party")
@Schema(description = "党建组织信息实体")
public class VillagePartyEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "党组织名称")
    private String orgName;

    @Schema(description = "组织类型：党支部/党总支")
    private String orgType;

    @Schema(description = "书记姓名")
    private String secretaryName;

    @Schema(description = "党员人数")
    private Integer memberCount;

    @Schema(description = "联系电话")
    private String contactPhone;

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

