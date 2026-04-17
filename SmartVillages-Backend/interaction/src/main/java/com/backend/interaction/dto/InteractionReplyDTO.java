package com.backend.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 官方回复DTO
 * @author chenyang
 * @date 2026/4/15
 * @description 官方回复DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "官方回复DTO")
public class InteractionReplyDTO {
    @Schema(description = "留言id")
    @NotNull(message = "留言id不能为空")
    private Integer userId;

    @Schema(description = "回复内容")
    @NotBlank(message = "回复内容不能为空")
    private String content;

    @Schema(description = "状态：0-待处理 1-处理中 2-已回复 3-已关闭")
    @NotNull(message = "满意度不能为空")
    private Integer status;
}
