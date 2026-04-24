package com.backend.feature.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.TableLogic;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("feature")
@Tag(name = "乡村风采", description = "乡村风采实体类")
public class FeatureEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "内容")
    private String content;
    
    @Schema(description = "类型")
    private String type;
    
    @Schema(description = "封面")
    private String cover;

    @Schema(description = "视频")
    private String video;

    @Schema(description = "图片")
    private String images;
    
    @Schema(description = "创建用户")
    private Integer createUser;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "排序")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "逻辑删除")
    private Integer deleted;
}
