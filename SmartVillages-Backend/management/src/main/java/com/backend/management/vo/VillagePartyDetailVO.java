package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "党建组织信息详情VO")
public class VillagePartyDetailVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "党组织名称")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

