package com.backend.management.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenyang
 * &#064;date 2026/4/24
 * &#064;description 民生服务工单，对应表 {@code village_service_ticket}
 */
@Data
@TableName("village_service_ticket")
public class VillageServiceTicketEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "类型：subsidy/employment/medical/dispute/other")
    private String serviceType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "详细描述")
    private String detail;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "附件（JSON数组字符串）")
    private String attachments;

    @Schema(description = "状态：0待处理 1处理中 2已办结 3已关闭")
    private Integer status;

    @Schema(description = "申请人authId")
    private Integer applicantId;

    @Schema(description = "处理人authId")
    private Integer handlerId;

    @Schema(description = "处理说明")
    private String handleNote;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

