package com.backend.announcement.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author chenyang
 * @date 2026/4/8
 * @description 公告实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("announcement")
public class AnnouncementEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "内容不能为空")
    private String content;
    @NotNull(message = "类型不能为空")
    private Integer type;
    @NotNull(message = "状态不能为空")
    private Integer status;
    @NotNull(message = "是否置顶不能为空")
    private Integer isTop;
    @TableField(fill = FieldFill.INSERT)
    @NotNull(message = "发布时间不能为空")
    private LocalDateTime publishTime;
    @NotNull(message = "审核时间不能为空")
    private LocalDateTime auditTime;
    @NotNull(message = "审核用户不能为空")
    private Integer auditUser;
    @NotNull(message = "浏览次数不能为空")
    private Integer viewCount;
    @NotNull(message = "创建用户不能为空")
    private Integer createUser;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @NotNull(message = "逻辑删除不能为空")
    @TableLogic
    private Integer deleted;
}