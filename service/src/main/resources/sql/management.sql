-- =============================================================================
-- 村务管理模块（management）：村务事项/公示、人口台账、房屋与土地、党建组织
-- - village_affair：财务/项目/政策等「公示类」事项（富文本、审核、上下架）
-- - village_population：户籍人口台账（与公示分离）
-- - village_house_land：房屋与土地台账
-- - village_party：党建组织与党务摘要（党员明细可再拆 party_member 表）
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- 1. 村务事项/公示（财务、项目、政策、其他公示）
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `village_affair`;
CREATE TABLE `village_affair` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `affair_type` VARCHAR(32) NOT NULL COMMENT '事项类型：FINANCE财务 PROJECT项目 POLICY政策 OTHER其他',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要（列表/卡片展示，可空）',
  `content` MEDIUMTEXT COMMENT '正文（富文本 HTML）',
  `amount` DECIMAL(14,2) DEFAULT NULL COMMENT '金额，仅 affair_type=FINANCE 使用',
  `attachments` JSON DEFAULT NULL COMMENT '附件 URL 列表，JSON 数组',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1待审核 2已发布 3已下架',
  `audit_user_id` BIGINT DEFAULT NULL COMMENT '审核人（关联后台用户）',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '驳回/备注',
  `publish_time` DATETIME DEFAULT NULL COMMENT '对外发布时间',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0否 1逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_affair_type_status` (`affair_type`, `status`, `deleted`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='村务事项/公示主表';

INSERT INTO `village_affair` (`affair_type`,`title`,`summary`,`content`,`amount`,`attachments`,`status`,`audit_user_id`,`audit_time`,`publish_time`,`view_count`,`create_user`,`deleted`) VALUES
('FINANCE','2025年第一季度财务收支公示','本季度集体收入与支出','本季度集体收入与支出明细见附件。',125680.50,JSON_ARRAY('/upload/management/q1_income.pdf','/upload/management/q1_expense.xlsx'),2,1,'2026-01-14 17:00:00','2026-01-15 10:00:00',512,2,0),
('PROJECT','村道路灯安装项目','资金来源与中标说明','资金来源与中标单位说明。',NULL,JSON_ARRAY('/upload/management/streetlight_contract.pdf'),2,1,'2026-02-19 16:00:00','2026-02-20 11:00:00',203,2,0),
('POLICY','耕地地力保护补贴发放说明',NULL,'按镇农业办政策执行。',NULL,JSON_ARRAY(),2,1,'2026-02-28 15:00:00','2026-03-01 09:00:00',88,2,0),
('OTHER','其他事项说明',NULL,'杂项公示示例。',NULL,JSON_ARRAY('/upload/management/misc_notice.docx'),2,1,'2026-03-04 10:00:00','2026-03-05 16:00:00',41,2,0),
('FINANCE','待审核财务条目',NULL,'用于测试待审核。',3200.00,JSON_ARRAY(),1,NULL,NULL,NULL,0,2,0);

-- -----------------------------------------------------------------------------
-- 2. 人口台账
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `village_population`;
CREATE TABLE `village_population` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `household_no` VARCHAR(64) DEFAULT NULL COMMENT '户号',
  `full_name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT DEFAULT NULL COMMENT '0未知 1男 2女',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `id_card_last4` CHAR(4) DEFAULT NULL COMMENT '身份证后四位（示例脱敏）',
  `relation_to_head` VARCHAR(32) DEFAULT NULL COMMENT '与户主关系',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '户籍/居住地址',
  `remark` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_household` (`household_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='户籍人口台账';

INSERT INTO `village_population` (`household_no`,`full_name`,`gender`,`birth_date`,`id_card_last4`,`relation_to_head`,`address`,`deleted`) VALUES
('H2025001','张三',1,'1980-05-01','1001','户主','本村一组1号',0),
('H2025001','李四',2,'1982-08-12','2002','配偶','本村一组1号',0);

-- -----------------------------------------------------------------------------
-- 3. 房屋与土地
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `village_house_land`;
CREATE TABLE `village_house_land` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `biz_type` VARCHAR(16) NOT NULL COMMENT 'HOUSE房屋 LAND土地',
  `parcel_code` VARCHAR(64) DEFAULT NULL COMMENT '地块/房屋编号',
  `location` VARCHAR(255) DEFAULT NULL COMMENT '坐落',
  `area_mu` DECIMAL(10,2) DEFAULT NULL COMMENT '面积（亩）',
  `owner_name` VARCHAR(50) DEFAULT NULL COMMENT '权利人/户主',
  `cert_no` VARCHAR(100) DEFAULT NULL COMMENT '权证号（示例）',
  `remark` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_biz_owner` (`biz_type`,`owner_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房屋与土地台账';

INSERT INTO `village_house_land` (`biz_type`,`parcel_code`,`location`,`area_mu`,`owner_name`,`cert_no`,`deleted`) VALUES
('HOUSE','F-001','本村一组1号',NULL,'张三','房权证字第001号',0),
('LAND','T-101','村东承包地',3.50,'张三','承包合同编号2025-01',0);

-- -----------------------------------------------------------------------------
-- 4. 党建组织信息
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `village_party`;
CREATE TABLE `village_party` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `org_name` VARCHAR(100) NOT NULL COMMENT '党组织名称',
  `org_type` VARCHAR(32) DEFAULT NULL COMMENT '如：党支部/党总支',
  `secretary_name` VARCHAR(50) DEFAULT NULL COMMENT '书记',
  `member_count` INT DEFAULT NULL COMMENT '党员人数（可冗余，定期同步）',
  `contact_phone` VARCHAR(20) DEFAULT NULL,
  `remark` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='党建组织信息';

INSERT INTO `village_party` (`org_name`,`org_type`,`secretary_name`,`member_count`,`contact_phone`,`deleted`) VALUES
('某某村党支部','党支部','王五',28,'13800000000',0);
