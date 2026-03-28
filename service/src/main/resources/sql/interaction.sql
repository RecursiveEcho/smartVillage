CREATE TABLE `interaction` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL COMMENT '留言用户 ID',
  `content` TEXT NOT NULL COMMENT '留言内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：consult-咨询/complaint-投诉/suggest-建议',
  `reply` TEXT COMMENT '官方回复',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已回复 3-已关闭',
  `reply_time` DATETIME COMMENT '回复时间',
  `reply_user` INT COMMENT '回复人 ID',
  `satisfaction` TINYINT COMMENT '满意度：1-满意 2-一般 3-不满意',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村民留言表';
