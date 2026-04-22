package com.backend.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回复村民留言DTO
 * @author chenyang
 * @date 2026/4/22
 * @description 回复村民留言DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "回复村民留言DTO")
public class ReplyInteractionDTO {
    @Schema(description = "回复内容")
    @NotBlank(message = "回复内容不能为空")
    private String reply;
}
