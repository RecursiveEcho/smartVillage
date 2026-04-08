package com.backend.announcement.dto;

import lombok.Data;

/**
 * @author chenyang
 * @date 2026/4/8
 * @description 公告DTO
 */
@Data
public class AnnouncementCreateDTO {
    private String title;
    private String content;
    private Integer status;//0-草稿 1-发布
}