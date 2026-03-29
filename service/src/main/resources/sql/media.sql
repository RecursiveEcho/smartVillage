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

-- 假数据：file_url 与 feature/open 一致使用 /upload/ 前缀；upload_user 对应 auth.id=1
INSERT INTO `media` (`file_name`, `file_url`, `file_type`, `file_size`, `category`, `upload_user`, `status`, `deleted`) VALUES
('banner_home.jpg', '/upload/media/banner_home.jpg', 'image', 245760, 'banner', 1, 1, 0),
('notice_202603.pdf', '/upload/media/notice_202603.pdf', 'document', 102400, 'announcement', 1, 1, 0),
('feature_thumb_1.jpg', '/upload/media/feature_thumb_1.jpg', 'image', 89600, 'feature', 1, 1, 0),
('village_intro.mp4', '/upload/media/village_intro.mp4', 'video', 5242880, 'other', 1, 1, 0),
('disabled_asset.png', '/upload/media/disabled_asset.png', 'image', 32000, 'other', 1, 0, 0);
