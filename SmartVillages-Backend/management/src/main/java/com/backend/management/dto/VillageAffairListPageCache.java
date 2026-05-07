package com.backend.management.dto;

import com.backend.management.vo.VillageAffairSimpleVO;
import lombok.Data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @author chenyang
 * &#064;date 2026/5/7
 * &#064;description 村务列表分页缓存VO
 */
@Data
@Schema(description = "村务列表分页缓存VO")
public class VillageAffairListPageCache {
    @Schema(description = "当前页数据")
    private List<VillageAffairSimpleVO> records;
    @Schema(description = "总条数")
    private long total;
    @Schema(description = "当前页码")
    private long current;
    @Schema(description = "每页条数")
    private long size;
    @Schema(description = "总页数")
    private long pages;
}
