SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for info_hash_list
-- ----------------------------
DROP TABLE IF EXISTS `info_hash_list`;
CREATE TABLE `info_hash_list` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `info_hash` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'hash',
  PRIMARY KEY (`id`),
  UNIQUE KEY `info_hash` (`info_hash`)
) ENGINE=InnoDB AUTO_INCREMENT=2800553 DEFAULT CHARSET=utf8 COMMENT='hash表';

-- ----------------------------
-- Table structure for torrent
-- ----------------------------
DROP TABLE IF EXISTS `torrent`;
CREATE TABLE `torrent` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `info_hash` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '种子hash',
  `file_name` varchar(255) DEFAULT NULL COMMENT '名称',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `files` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '文件列表',
  `add_time` datetime DEFAULT NULL COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `info_hash` (`info_hash`) USING BTREE,
  KEY `add_time` (`add_time`)
) ENGINE=InnoDB AUTO_INCREMENT=108758879 DEFAULT CHARSET=utf8 COMMENT='种子列表';
