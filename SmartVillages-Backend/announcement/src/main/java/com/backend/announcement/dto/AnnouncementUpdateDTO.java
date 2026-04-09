package com.backend.announcement.dto;

import lombok.Data;

/**
 * @author chenyang
 * {@code @date} 2026/4/9
 * {@code @description} 公告更新DTO
 */
@Data
public class AnnouncementUpdateDTO {
    private String title;
    private String content;
    private Integer type;
    private Integer isTop;
    private Integer status;
}