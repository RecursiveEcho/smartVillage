CREATE TABLE `media` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `file_name` VARCHAR(100) NOT NULL COMMENT '原文件名',
  `file_url` VARCHAR(300) NOT NULL COMMENT '文件访问 URL',
  `file_type` VARCHAR(20) NOT NULL COMMENT '类型：image/video/document',
  `file_size` BIGINT COMMENT '文件大小 (字节)',
  `category` VARCHAR(50) COMMENT '分类：banner-轮播图/announcement-公告/feature-风采/other-其他',
  `usage_remark` VARCHAR(200) NULL COMMENT '用途说明：用于哪个模块/位置（如首页轮播第2张）',
  `upload_user` INT COMMENT '上传人 ID（当前登录账号）',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝 3-已下架',
  `audit_time` DATETIME COMMENT '审核时间',
  `audit_user` INT COMMENT '审核人ID',
  `bind_target` VARCHAR(20) COMMENT '绑定目标模块：FEATURE/ANNOUNCEMENT/AUTH',
  `bind_entity_id` BIGINT COMMENT '绑定实体ID',
  `bind_slot` VARCHAR(20) COMMENT '绑定槽位：COVER/VIDEO/IMAGES_APPEND/AVATAR',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0 comment ' 逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体资源表（含审核流程，绑定参数用于审核通过后自动绑定）';

-- 演示数据：覆盖已通过 / 待审核 / 已拒绝 / 已下架，以及公告封面、头像、风采资源等典型场景
INSERT INTO `media`
(`file_name`, `file_url`, `file_type`, `file_size`, `category`, `usage_remark`, `upload_user`, `status`, `audit_time`, `audit_user`, `bind_target`, `bind_entity_id`, `bind_slot`, `deleted`)
VALUES
('home_banner_summer.jpg', '/upload/media/home_banner_summer.jpg', 'image', 245760, 'banner', '门户首页轮播图第1张', 1, 1, '2026-06-18 09:10:00', 1, NULL, NULL, NULL, 0),
('road_light_cover.jpg', '/upload/media/road_light_cover.jpg', 'image', 138240, 'announcement', '村主干道亮化公告封面', 2, 1, '2026-06-19 09:35:00', 1, 'ANNOUNCEMENT', 2, 'COVER', 0),
('finance_public_attachment.pdf', '/upload/media/finance_public_attachment.pdf', 'document', 189440, 'announcement', '村集体收支公示附件', 2, 1, '2026-06-20 13:30:00', 1, 'ANNOUNCEMENT', 3, 'IMAGES_APPEND', 0),
('cadre_avatar_new.png', '/upload/media/cadre_avatar_new.png', 'image', 86520, 'other', '村干部头像待审核', 2, 0, NULL, NULL, 'AUTH', 2, 'AVATAR', 0),
('feature_tea_garden.jpg', '/upload/media/feature_tea_garden.jpg', 'image', 112640, 'feature', '高山茶园风采封面', 1, 1, '2026-06-17 16:20:00', 1, 'FEATURE', 2, 'COVER', 0),
('rejected_blur_photo.jpg', '/upload/media/rejected_blur_photo.jpg', 'image', 42100, 'feature', '图片模糊，审核驳回示例', 1, 2, '2026-06-16 10:00:00', 1, 'FEATURE', 7, 'COVER', 0),
('offline_history_banner.png', '/upload/media/offline_history_banner.png', 'image', 32000, 'other', '历史资源下架示例', 1, 3, '2026-05-20 11:00:00', 1, NULL, NULL, NULL, 0);
