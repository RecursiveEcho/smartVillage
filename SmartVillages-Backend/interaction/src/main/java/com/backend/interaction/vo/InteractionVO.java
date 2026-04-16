package com.backend.interaction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "留言VO")
public class InteractionVO {
    
    @Schema(description = "留言id")
    private Integer id;

    @Schema(description = "留言用户 ID")
    private Integer userId;

    @Schema(description = "留言内容")
    private String content;
    
    @Schema(description = "类型：consult-咨询/complaint-投诉/suggest-建议")
    private String type;

    @Schema(description = "官方回复")
    private String reply;

    @Schema(description = "状态：0-待处理 1-处理中 2-已回复 3-已关闭")
    private Integer status;
    
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    @Schema(description = "回复人 ID")
    private Integer replyUser;

    @Schema(description = "满意度：1-满意 2-一般 3-不满意")
    private Integer satisfaction;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}