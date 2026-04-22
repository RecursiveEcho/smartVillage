package com.backend.announcement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告对外展示字段。
 *
 * @author chenyang
 * &#064;date 2026/4/8
 * &#064;description 公告 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公告 VO")
public class AnnouncementVO {

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人")
    private Integer auditUser;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "创建人")
    private Integer createUser;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
