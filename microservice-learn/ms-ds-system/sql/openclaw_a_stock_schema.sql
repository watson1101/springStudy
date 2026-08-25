
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `openclaw_a_analysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_analysis` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `sync_date` date NOT NULL,
  `news_id` bigint unsigned DEFAULT NULL,
  `title` varchar(500) NOT NULL,
  `impact_direction` enum('positive','negative','neutral') NOT NULL,
  `impact_weight` decimal(4,2) DEFAULT '0.00',
  `certainty` decimal(4,2) DEFAULT '0.00',
  `score` decimal(6,3) DEFAULT '0.000',
  `is_urgent` tinyint NOT NULL DEFAULT '0',
  `sector_code` varchar(32) DEFAULT NULL,
  `affected_stocks` varchar(2000) DEFAULT NULL,
  `logic` text,
  `risk_note` varchar(1000) DEFAULT NULL,
  `advice` varchar(1000) DEFAULT NULL COMMENT '买卖建议+止损止盈',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`sync_date`),
  KEY `idx_sector` (`sector_code`),
  KEY `idx_urgent` (`is_urgent`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时事影响分析结果表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_brief`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_brief` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `brief_date` date NOT NULL,
  `session` varchar(16) NOT NULL,
  `brief_text` longtext,
  `push_status` enum('pending','sent','failed') DEFAULT 'pending',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_session` (`brief_date`,`session`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时简报归档表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_day_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_day_report` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `report_date` date NOT NULL,
  `stock_code` varchar(16) NOT NULL,
  `stock_name` varchar(64) NOT NULL,
  `direction` enum('positive','negative') NOT NULL,
  `close_price` decimal(10,3) DEFAULT NULL,
  `close_pct` decimal(8,3) DEFAULT NULL,
  `match_result` enum('correct','wrong','skip') DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_code` (`report_date`,`stock_code`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='每日收盘对比报表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_day_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_day_tracking` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `track_date` date NOT NULL,
  `stock_code` varchar(16) NOT NULL,
  `stock_name` varchar(64) NOT NULL,
  `direction` enum('positive','negative') NOT NULL,
  `sector_code` varchar(32) DEFAULT NULL,
  `source_type` enum('affected','advice') DEFAULT 'affected',
  `source_analysis_id` bigint unsigned DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_code` (`track_date`,`stock_code`)
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='每日资讯涉及股票追踪表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_news` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `sync_date` date NOT NULL,
  `fetch_at` datetime NOT NULL,
  `title` varchar(500) NOT NULL,
  `summary` text,
  `url` varchar(1000) DEFAULT NULL,
  `source` varchar(100) DEFAULT NULL,
  `region` enum('domestic','foreign','market') DEFAULT 'domestic',
  `published_at` datetime DEFAULT NULL,
  `hash` char(32) NOT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hash` (`hash`),
  KEY `idx_date_region` (`sync_date`,`region`)
) ENGINE=InnoDB AUTO_INCREMENT=3715 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时事新闻原始表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_push_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_push_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `analysis_id` bigint unsigned DEFAULT NULL,
  `push_date` date NOT NULL,
  `push_at` datetime NOT NULL,
  `channel` varchar(32) DEFAULT NULL,
  `content` text,
  `status` enum('sent','failed') DEFAULT 'sent',
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`push_date`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='推送归档表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_schedule` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `run_time` time NOT NULL,
  `label` varchar(32) DEFAULT NULL,
  `enabled` tinyint NOT NULL DEFAULT '1',
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_time` (`run_time`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='抓取分析时间点配置表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_sector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_sector` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  `keywords` varchar(2000) DEFAULT NULL,
  `watch_weight` decimal(4,2) DEFAULT '1.00',
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关注板块定义表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_stock_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_stock_mapping` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `sector_code` varchar(32) NOT NULL,
  `stock_code` varchar(16) NOT NULL,
  `stock_name` varchar(64) NOT NULL,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sector_stock` (`sector_code`,`stock_code`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='板块-代表个股映射表';
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `openclaw_a_watchlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `openclaw_a_watchlist` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(16) NOT NULL COMMENT '股票代码',
  `stock_name` varchar(64) NOT NULL COMMENT '股票名称',
  `source` varchar(32) DEFAULT 'manual' COMMENT '来源 manual/auto_buy',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`stock_code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户自定义关注个股表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

