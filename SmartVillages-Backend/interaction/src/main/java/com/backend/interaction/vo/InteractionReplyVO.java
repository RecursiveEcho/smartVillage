package com.backend.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;  
import lombok.Data;

@Data
@Schema(description = "官方回复VO")
public class InteractionReplyVO {
    @Schema(description = "留言id")
    private Integer id;

    @Schema(description = "回复内容")
    private String content;

    @Schema(description = "状态：0-待处理 1-处理中 2-已回复 3-已关闭")
    private Integer status;
}
