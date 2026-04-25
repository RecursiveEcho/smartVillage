package com.backend.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "党建组织信息列表VO")
public class VillagePartySimpleVO {

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

