CREATE TABLE `announcement` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `type` TINYINT DEFAULT 1 COMMENT '类型：1-通知 2-公告 3-公示',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝 3-已下架',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否 1-是',
  `publish_time` DATETIME COMMENT '发布时间',
  `audit_time` DATETIME COMMENT '审核时间',
  `audit_user` INT COMMENT '审核人 ID',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_user` INT COMMENT '创建人 ID',
  `cover_url` VARCHAR(500) COMMENT '封面图片URL',
  `images` TEXT COMMENT '图片列表JSON数组',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村务公告表';

-- 演示数据：覆盖通知 / 公告 / 公示、已发布 / 待审核 / 已下架等典型状态
INSERT INTO `announcement`
(`title`, `content`, `type`, `status`, `is_top`, `publish_time`, `audit_time`, `audit_user`, `view_count`, `create_user`, `cover_url`, `images`, `deleted`)
VALUES
('关于开展防汛隐患排查的通知', '受连续降雨影响，请各组长于本周内完成沟渠、边坡和危旧房排查，并将结果上报村委会。', 1, 1, 1, '2026-06-18 08:30:00', '2026-06-18 08:10:00', 1, 268, 2, '/upload/announcement/flood_notice_cover.jpg', '["/upload/announcement/flood_notice_1.jpg"]', 0),
('村主干道亮化提升项目公告', '经村两委研究，计划对村主干道新增太阳能路灯 36 盏，预计于下月开工，现将项目基本情况予以公告。', 2, 1, 0, '2026-06-19 10:00:00', '2026-06-19 09:40:00', 1, 413, 2, '/upload/announcement/road_light_cover.jpg', '["/upload/announcement/road_light_1.jpg","/upload/announcement/road_light_2.jpg"]', 0),
('2026年上半年村集体收支公示', '现将 2026 年上半年村集体资金收入、支出和结余情况进行公示，接受村民监督。', 3, 1, 1, '2026-06-20 14:00:00', '2026-06-20 13:35:00', 1, 195, 2, '/upload/announcement/finance_public_cover.jpg', '["/upload/announcement/finance_public_1.jpg"]', 0),
('端午文体活动方案（待审核）', '拟于端午节当天在村文化广场组织包粽子、拔河和文艺汇演活动，现提交审核。', 1, 0, 0, NULL, NULL, NULL, 0, 2, '/upload/announcement/dragon_boat_draft_cover.jpg', '[]', 0),
('历史通知示例（已下架）', '该条目仅用于演示已下架状态和后台筛选。', 1, 3, 0, '2026-04-08 09:00:00', '2026-04-08 08:40:00', 1, 37, 2, '/upload/announcement/offline_notice_cover.jpg', '[]', 0);
