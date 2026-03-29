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
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村务公告表';

-- 假数据：类型 1-通知 2-公告 3-公示；状态含待审/已通过/置顶等
INSERT INTO `announcement` (`title`, `content`, `type`, `status`, `is_top`, `publish_time`, `audit_time`, `audit_user`, `view_count`, `create_user`, `deleted`) VALUES
('本周村务工作安排', '请于本周五前完成农田补贴登记，材料交至村委会。', 1, 1, 1, '2026-03-01 08:00:00', '2026-03-01 08:30:00', 1, 186, 2, 0),
('关于道路修缮工程的公告', '县道 X001 段将于4月封闭施工，请绕行。', 2, 1, 0, '2026-03-10 10:00:00', '2026-03-10 10:15:00', 1, 420, 2, 0),
('村集体资产出租公示', '村礼堂一年期出租，公示期7天，异议请书面提交。', 3, 1, 1, '2026-03-12 14:00:00', '2026-03-12 14:00:00', 1, 95, 2, 0),
('待审核草稿', '此条用于测试 status=0。', 1, 0, 0, NULL, NULL, NULL, 0, 2, 0),
('已下架通知', '历史条目，用于测试 status=3。', 1, 3, 0, '2025-12-01 09:00:00', '2025-12-01 09:00:00', 1, 30, 2, 0);
