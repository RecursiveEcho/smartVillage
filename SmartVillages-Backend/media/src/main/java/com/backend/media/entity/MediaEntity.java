package com.backend.media.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.time.LocalDateTime;

/**
 * @author chenyang
 * @date 2026/4/20
 * @description 媒体资源实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Schema(description = "上传用户")
    private Integer uploadUser;

    @TableField(fill= FieldFill.INSERT)
    @Schema(description = "状态：0-禁用 1-启用", defaultValue = "1")
    private Integer status;

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
