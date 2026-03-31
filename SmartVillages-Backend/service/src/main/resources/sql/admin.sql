CREATE TABLE `admin` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `auth_id` INT NOT NULL COMMENT '关联 auth 表 ID',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `permissions` TEXT COMMENT '权限列表 (JSON)',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(20) COMMENT '最后登录 IP',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

-- 假数据：依赖 auth 表已插入 id=1 的管理员账号
INSERT INTO `admin` (`auth_id`, `real_name`, `permissions`, `last_login_time`, `last_login_ip`, `deleted`) VALUES
(1, '系统管理员', '["feature:*","announcement:*","management:*","interaction:*","media:*"]', '2026-03-15 09:30:00', '127.0.0.1', 0);
