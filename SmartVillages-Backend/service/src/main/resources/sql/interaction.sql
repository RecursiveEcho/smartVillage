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

-- 假数据：user_id / reply_user 对应 auth 表中村民与管理员；覆盖 type 与 status
INSERT INTO `interaction` (`user_id`, `content`, `type`, `reply`, `status`, `reply_time`, `reply_user`, `satisfaction`, `deleted`) VALUES
(3, '请问今年新农合缴费截止日期是什么时候？', 'consult', '请于2026年3月31日前在「医保」小程序完成缴费，有疑问可到村委会咨询。', 2, '2026-03-10 14:00:00', 1, 1, 0),
(4, '村口垃圾桶满溢，请尽快清理。', 'complaint', '已安排保洁今日下午清运，感谢监督。', 2, '2026-03-11 09:20:00', 1, 2, 0),
(3, '建议在广场增设几条长椅，方便老人休息。', 'suggest', NULL, 1, NULL, NULL, NULL, 0),
(4, '咨询独生子女补贴如何申请？', 'consult', NULL, 0, NULL, NULL, NULL, 0);
