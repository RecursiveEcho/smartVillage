package com.backend.management.dto;

import com.backend.management.vo.VillagePartySimpleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * @author chenyang &#064;date 2026/5/7 &#064;description 党组织列表分页缓存VO
 */
@Data
@Schema(description = "党组织列表分页缓存VO")
public class VillagePartyListPageCache {
  @Schema(description = "当前页数据")
  private List<VillagePartySimpleVO> records;

  @Schema(description = "总条数")
  private long total;

  @Schema(description = "当前页码")
  private long current;

  @Schema(description = "每页条数")
  private long size;

  @Schema(description = "总页数")
  private long pages;
}
