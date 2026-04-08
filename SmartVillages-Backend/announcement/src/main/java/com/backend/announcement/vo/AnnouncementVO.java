package com.backend.announcement.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
/**
 * @author chenyang
 * @date 2026/4/8
 * @description 公告VO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVO {
    private Integer id;
    private String title;
    private String content;
    private Integer status;
    private Integer type;
    private Integer isTop;
    private LocalDateTime publishTime;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}