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

-- 假数据：用于本地/联调验证列表、类型筛选、排序等
INSERT INTO `feature` (`title`, `content`, `type`, `images`, `sort`, `status`, `deleted`) VALUES
('村口荷塘', '夏季荷花盛开，是村民散步的好去处。', 'scenery', '["/upload/feature/lotus_1.jpg","/upload/feature/lotus_2.jpg"]', 100, 1, 0),
('高山茶园', '有机种植，云雾缭绕。', 'scenery', '["/upload/feature/tea_1.jpg"]', 80, 1, 0),
('高山绿茶', '明前茶，清香回甘，支持预订。', 'product', '["/upload/feature/green_tea.jpg"]', 90, 1, 0),
('手工红薯粉', '传统工艺，无添加。', 'product', '["/upload/feature/sweet_potato_1.jpg","/upload/feature/sweet_potato_2.jpg"]', 70, 1, 0),
('端午龙舟', '每年端午举办，传承百年。', 'culture', '["/upload/feature/dragon_boat.jpg"]', 85, 1, 0),
('村史陈列室', '记录本村变迁与乡贤事迹。', 'history', '["/upload/feature/museum_1.jpg","/upload/feature/museum_2.jpg"]', 60, 1, 0),
('待上架条目（隐藏）', '仅后台可见，用于测 status。', 'culture', '["/upload/feature/draft_preview.jpg"]', 10, 0, 0);
