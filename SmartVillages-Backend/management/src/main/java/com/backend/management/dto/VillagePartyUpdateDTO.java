package com.backend.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "党建组织信息更新DTO")
public class VillagePartyUpdateDTO {

    @Schema(description = "党组织名称")
    @NotBlank(message = "orgName不能为空")
    private String orgName;

    @Schema(description = "组织类型：党支部/党总支")
    private String orgType;

    @Schema(description = "书记姓名")
    private String secretaryName;

    @Schema(description = "党员人数")
    private Integer memberCount;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "备注")
    private String remark;
}

