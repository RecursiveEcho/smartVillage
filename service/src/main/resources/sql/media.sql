CREATE TABLE `media` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `file_name` VARCHAR(100) NOT NULL COMMENT '原文件名',
  `file_url` VARCHAR(300) NOT NULL COMMENT '文件访问 URL',
  `file_type` VARCHAR(20) NOT NULL COMMENT '类型：image/video/document',
  `file_size` BIGINT COMMENT '文件大小 (字节)',
  `category` VARCHAR(50) COMMENT '分类：banner-轮播图/announcement-公告/feature-风采/other-其他',
  `upload_user` INT COMMENT '上传人 ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0 comment ' 逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源表';
