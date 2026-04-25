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
@TableName("village_affair")
@Schema(description = "村务事项/公示实体")
public class VillageAffairEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "事项类型：FINANCE财务 PROJECT项目 POLICY政策 OTHER其他")
    private String affairType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "正文（富文本 HTML）")
    private String content;

    @Schema(description = "金额，仅 affairType=FINANCE 使用")
    private BigDecimal amount;

    @Schema(description = "附件 URL 列表，JSON 数组字符串")
    private String attachments;

    @Schema(description = "状态：0草稿 1待审核 2已发布 3已下架")
    private Integer status;

    @Schema(description = "审核人")
    private Integer auditUserId;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "创建人")
    private Integer createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

