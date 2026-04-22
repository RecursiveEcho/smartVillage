package com.backend.interaction.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "村民留言详情VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionDetailVO {

    @Schema(description = "留言id")
    private Integer id;
    
    @Schema(description = "留言者id")
    private Integer userId;
    
    @Schema(description = "留言内容")
    private String content;

    @Schema(description = "类型：consult-咨询/complaint-投诉/suggest-建议")
    private String type;
    
    @Schema(description = "状态：0-待处理 1-处理中 2-已回复 3-已关闭")
    private Integer status;
    
    @Schema(description = "官方回复")
    private String reply;
    
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;
    
    @Schema(description = "回复人id")
    private Integer replyUser;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
