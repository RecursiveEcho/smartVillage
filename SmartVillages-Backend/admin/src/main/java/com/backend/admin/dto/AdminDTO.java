package com.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author chenyang
 * @date 2026/4/7
 * @description 管理员 DTO
 */
@Data
public class AdminDTO {
    
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "名字长度 2-20 位")
    private String realName;
    
    @NotBlank(message = "权限不能为空")
    private String permission;
}