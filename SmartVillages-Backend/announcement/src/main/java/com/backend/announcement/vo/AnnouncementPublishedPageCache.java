package com.backend.announcement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 前台「已发布公告」分页列表的 Redis 缓存载体（与 {@link com.baomidou.mybatisplus.core.metadata.IPage} 字段对齐）。
 */
@Data
@Schema(description = "已发布公告分页缓存")
public class AnnouncementPublishedPageCache {

    @Schema(description = "当前页数据")
    private List<AnnouncementVO> records;

    @Schema(description = "总条数")
    private long total;

    @Schema(description = "当前页码")
    private long current;

    @Schema(description = "每页条数")
    private long size;

    @Schema(description = "总页数")
    private long pages;
}
