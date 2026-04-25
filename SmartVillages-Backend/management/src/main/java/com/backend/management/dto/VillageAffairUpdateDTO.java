package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "村务事项/公示更新DTO")
public class VillageAffairUpdateDTO {

    @Schema(description = "事项类型：FINANCE财务 PROJECT项目 POLICY政策 OTHER其他")
    @NotBlank(message = "affairType不能为空")
    private String affairType;

    @Schema(description = "标题")
    @NotBlank(message = "title不能为空")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "正文（富文本 HTML）")
    private String content;

    @Schema(description = "金额，仅 affairType=FINANCE 使用")
    private BigDecimal amount;

    @Schema(description = "附件 URL 列表，JSON 数组字符串")
    private String attachments;
}

