package com.backend.interaction.dto;

import java.util.List;

import com.backend.interaction.vo.InteractionCreateVO;
import com.backend.interaction.vo.InteractionDetailVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * @author chenyang
 * &#064;date 2026/5/7
 * &#064;description 村民留言已发布分页缓存
 */
@Data
@Schema(description = "村民留言已发布分页缓存")
public class InteractionPublishedPageCache {
    @Schema(description = "当前页数据")
    List<InteractionCreateVO> records;

    @Schema(description = "详情页数据")
    List<InteractionDetailVO> detailRecords;
    @Schema(description = "总条数")
    Long total;
    @Schema(description = "当前页码")
    Long current;
    @Schema(description = "每页条数")
    Long size;
    @Schema(description = "总页数")
    Long pages;
}
