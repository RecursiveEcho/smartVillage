CREATE TABLE `feature` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `type` VARCHAR(50) NOT NULL COMMENT '类型：scenery-风景/product-农产品/culture-文化/history-历史',
  `images` TEXT COMMENT '图片列表 (JSON 数组)',
  `sort` INT DEFAULT 0 COMMENT '排序值 (越大越靠前)',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-隐藏 1-显示',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='乡村风采表';
