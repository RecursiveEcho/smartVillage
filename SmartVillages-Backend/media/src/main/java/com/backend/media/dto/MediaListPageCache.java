package com.backend.media.dto;

import com.backend.media.vo.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * @author chenyang &#064;date 2026/5/7 &#064;description 媒体列表分页缓存VO
 */
@Data
@Schema(description = "媒体列表分页缓存VO")
public class MediaListPageCache {
  @Schema(description = "当前页数据")
  private List<PageVO> records;

  @Schema(description = "总条数")
  private long total;

  @Schema(description = "当前页码")
  private long current;

  @Schema(description = "每页条数")
  private long size;

  @Schema(description = "总页数")
  private long pages;
}
