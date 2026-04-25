package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "村务事项/公示列表VO")
public class VillageAffairSimpleVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "事项类型")
    private String affairType;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "状态：0草稿 1待审核 2已发布 3已下架")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

