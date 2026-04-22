package com.backend.announcement.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告表 {@code announcement} 实体：标题内容、类型、上下架与置顶、浏览量及审计字段。
 * <p>
 * {@code status} 在库表注释中可扩展为多状态；当前业务实现里前台「已发布」与上下架以 Service 层常量为准。
 *
 * @author chenyang
 * &#064;date 2026/4/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("announcement")
@Schema(description = "公告实体类")
public class AnnouncementEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "标题：200字以内")
    private String title;

    @Schema(description = "内容：富文本格式")
    private String content;

    @Schema(description = "类型：1-通知 2-公告 3-公示")
    private Integer type;

    @Schema(description = "状态：0-待审核 1-已通过 2-已拒绝 3-已下架")
    private Integer status;

    @Schema(description = "是否置顶：0-否 1-是")
    private Integer isTop;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "发布时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "审核时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    @Schema(description = "审核人：关联后台用户ID")
    private Integer auditUser;

    @Schema(description = "浏览次数：默认0")
    private Integer viewCount;

    @Schema(description = "创建人：关联后台用户ID")
    private Integer createUser;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间：yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除：0-否 1-是", defaultValue = "0")
    @TableLogic
    private Integer deleted;
}