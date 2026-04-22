package com.backend.interaction.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 村民留言创建DTO
 * @author chenyang
 * &#064;date 2026/4/15
 * &#064;description 村民留言创建DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "村民留言创建DTO")
public class InteractionCreateDTO {

    @Schema(description = "留言内容")
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 200, message = "留言内容长度不能超过200个字符")
    private String content;

    @Schema(description = "类型：consult-咨询/complaint-投诉/suggest-建议")
    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(consult|complaint|suggest)$", message = "类型必须是consult、complaint或suggest")
    private String type;

}
