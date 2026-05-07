package com.backend.feature.dto;

import java.util.List;

import com.backend.feature.vo.FeatureVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(description = "乡村风采已发布分页缓存")
/**
 * @author chenyang
 * &#064;date 2026/5/7
 * &#064;description 乡村风采已发布分页缓存
 */
public class FeaturePublishedPageCache {
    @Schema(description = "当前页数据")
    private List<FeatureVO> records;
    @Schema(description = "总条数")
    private long total;
    @Schema(description = "当前页码")
    private long current;
    @Schema(description = "每页条数")
    private long size;
    @Schema(description = "总页数")
    private long pages;
}
