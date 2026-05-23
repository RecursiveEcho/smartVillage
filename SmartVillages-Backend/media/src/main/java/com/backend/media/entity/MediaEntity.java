package com.backend.media.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源实体类
 */
@Data
@NoArgsConstructor
@TableName("media")
@Schema(description = "媒体资源实体类")
public class MediaEntity {
    @Schema(description = "媒体资源id")
    private Integer id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "分类：banner-轮播图/announcement-公告/feature-风采/other-其他")
    private String category;

    @Schema(description = "用途说明：对应门户哪个模块/位置（如首页轮播第2张）")
    private String usageRemark;

    @Schema(description = "上传用户")
    private Integer uploadUser;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "状态：0-待审核 1-已通过 2-已拒绝 3-已下架")
    private Integer status;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核人ID")
    private Integer auditUser;

    @Schema(description = "绑定目标模块：FEATURE/ANNOUNCEMENT/AUTH")
    private String bindTarget;

    @Schema(description = "绑定实体ID")
    private Long bindEntityId;

    @Schema(description = "绑定槽位：COVER/VIDEO/IMAGES_APPEND/AVATAR")
    private String bindSlot;

    @TableField(fill= FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill= FieldFill.INSERT)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill= FieldFill.INSERT)
    @Schema(description = "逻辑删除：0-否 1-是", defaultValue = "0")
    private Integer deleted;
}
