package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "村务事项/公示详情VO")
public class VillageAffairDetailVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "事项类型")
    private String affairType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "正文（富文本 HTML）")
    private String content;

    @Schema(description = "金额")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

