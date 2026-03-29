CREATE TABLE `auth` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码：MD5(UTF-8 明文) 小写十六进制 32 位',
  `phone` VARCHAR(11) COMMENT '手机号',
  `role` VARCHAR(20) NOT NULL DEFAULT 'villager' COMMENT '角色：admin.sql-管理员/cadre-村干部/villager-村民',
  `avatar` VARCHAR(200) COMMENT '头像 URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证表';

-- 假数据（本地/联调）
-- 加密方式：MD5；明文字节：UTF-8；输出：32 位小写十六进制（无 salt、无多次迭代）。
-- 联调统一明文密码：123456 → MD5 = e10adc3949ba59abbe56e057f20f883e
-- 登录校验：对输入密码做同上 MD5，与库中 password 字段比较（相等则通过）。
INSERT INTO `auth` (`username`, `password`, `phone`, `role`, `avatar`, `status`, `is_deleted`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '13800001001', 'admin', '/upload/avatar/admin.png', 1, 0),
('cadre_wang', 'e10adc3949ba59abbe56e057f20f883e', '13800001002', 'cadre', '/upload/avatar/cadre.png', 1, 0),
('zhang_san', 'e10adc3949ba59abbe56e057f20f883e', '13800001003', 'villager', NULL, 1, 0),
('li_si', 'e10adc3949ba59abbe56e057f20f883e', '13800001004', 'villager', '/upload/avatar/user4.jpg', 1, 0);
