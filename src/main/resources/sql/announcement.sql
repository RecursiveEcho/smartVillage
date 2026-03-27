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
