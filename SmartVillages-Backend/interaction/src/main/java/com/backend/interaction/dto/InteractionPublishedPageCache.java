package com.backend.interaction.dto;

import com.backend.interaction.vo.InteractionDetailVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * @author chenyang &#064;date 2026/5/7 &#064;description 村民留言已发布分页缓存
 */
@Data
@Schema(description = "村民留言已发布分页缓存")
public class InteractionPublishedPageCache {
  @Schema(description = "详情页数据")
  List<InteractionDetailVO> records;

  @Schema(description = "总条数")
  Long total;

  @Schema(description = "当前页码")
  Long current;

  @Schema(description = "每页条数")
  Long size;

  @Schema(description = "总页数")
  Long pages;
}
