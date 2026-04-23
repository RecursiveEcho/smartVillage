-- 索引补充脚本（用于已有库）
-- 建议在业务低峰期执行

-- feature
ALTER TABLE `feature`
  ADD INDEX `idx_feature_status_deleted_sort` (`status`, `deleted`, `sort`),
  ADD INDEX `idx_feature_type_status_deleted` (`type`, `status`, `deleted`);

-- announcement
ALTER TABLE `announcement`
  ADD INDEX `idx_announcement_status_deleted_top_publish` (`status`, `deleted`, `is_top`, `publish_time`),
  ADD INDEX `idx_announcement_type_status_deleted` (`type`, `status`, `deleted`);

-- interaction
ALTER TABLE `interaction`
  ADD INDEX `idx_interaction_status_deleted_create` (`status`, `deleted`, `create_time`),
  ADD INDEX `idx_interaction_type_status_deleted` (`type`, `status`, `deleted`);

-- media
ALTER TABLE `media`
  ADD INDEX `idx_media_category_status_deleted` (`category`, `status`, `deleted`),
  ADD INDEX `idx_media_type_status_deleted` (`file_type`, `status`, `deleted`);

-- auth
ALTER TABLE `auth`
  ADD INDEX `idx_auth_role_status_deleted` (`role`, `status`, `is_deleted`);

-- admin
ALTER TABLE `admin`
  ADD INDEX `idx_admin_auth_deleted` (`auth_id`, `deleted`);
