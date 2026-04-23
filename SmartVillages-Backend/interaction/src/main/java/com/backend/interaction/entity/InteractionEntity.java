package com.backend.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenyang
 * &#064;date 2026/4/15
 * &#064;description 村民留言实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("interaction")
@Tag(name = "留言",description = "村民留言")
public class InteractionEntity {

    @TableId(type = IdType.AUTO)
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

    @Schema(description = "删除状态")
    @TableLogic
    private Integer deleted;

}
