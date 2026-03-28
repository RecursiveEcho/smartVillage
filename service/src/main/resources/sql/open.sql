CREATE TABLE `open` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `category` VARCHAR(50) NOT NULL COMMENT '分类：finance-财务/project-项目/policy-政策/other-其他',
  `amount` DECIMAL(10,2) COMMENT '金额 (财务公开用)',
  `attachments` TEXT COMMENT '附件列表 (JSON 数组)',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-已发布 2-已下架',
  `publish_time` DATETIME COMMENT '发布时间',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_user` INT COMMENT '创建人 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村务公开表';
