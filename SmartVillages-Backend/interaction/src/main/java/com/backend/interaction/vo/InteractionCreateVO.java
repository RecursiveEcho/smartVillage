package com.backend.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "村民留言创建VO")
@Data
@NoArgsConstructor
public class InteractionCreateVO {
    @Schema(description = "留言者id")
    private Integer userId;

    @Schema(description = "留言者用户名（公开列表展示）")
    private String username;

    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "类型：consult-咨询/complaint-投诉/suggest-建议")
    private String type;
}
