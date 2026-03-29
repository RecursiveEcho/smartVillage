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

-- 假数据：覆盖 category 与财务金额、附件 JSON；create_user 对应 auth 中村干部
INSERT INTO `open` (`title`, `content`, `category`, `amount`, `attachments`, `status`, `publish_time`, `view_count`, `create_user`, `deleted`) VALUES
('2025年第一季度财务收支公示', '本季度集体收入与支出明细见附件。', 'finance', 125680.50, '["/upload/open/q1_income.pdf","/upload/open/q1_expense.xlsx"]', 1, '2026-01-15 10:00:00', 512, 2, 0),
('村道路灯安装项目', '资金来源与中标单位说明。', 'project', NULL, '["/upload/open/streetlight_contract.pdf"]', 1, '2026-02-20 11:00:00', 203, 2, 0),
('耕地地力保护补贴发放说明', '按镇农业办政策执行。', 'policy', NULL, '[]', 1, '2026-03-01 09:00:00', 88, 2, 0),
('其他事项说明', '杂项公开示例。', 'other', NULL, '["/upload/open/misc_notice.docx"]', 1, '2026-03-05 16:00:00', 41, 2, 0),
('待审核财务条目', '用于测试 status=0。', 'finance', 3200.00, '[]', 0, NULL, 0, 2, 0);
