package com.backend.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员入参 DTO。
 *
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理员 DTO")
public class AdminDTO {

    @Schema(description = "管理员 ID")
    @NotNull(message = "ID 不能为空")
    private Integer id;

    @Schema(description = "管理员姓名")
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "名字长度 2-20 位")
    private String realName;

    @Schema(description = "权限列表")
    @NotEmpty(message = "权限不能为空")
    private List<String> permissions;
}
