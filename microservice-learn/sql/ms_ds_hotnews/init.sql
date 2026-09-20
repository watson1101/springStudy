-- =====================================================================
-- 热点资讯模块 - 建库建表脚本
-- 数据库名: ms_ds_hotnews
-- 说明: 头条热榜采集 → RocketMQ → 消费入库
-- 环境: MySQL 8.x (192.168.0.27 Ubuntu)
-- ID策略: 雪花算法（应用层生成，非 AUTO_INCREMENT）
-- 规范: 含 create_time / update_time；软删除 deleted 字段
-- 执行: mysql -uroot -p123456 < sql/ms_ds_hotnews/init.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `ms_ds_hotnews`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `ms_ds_hotnews`;

-- ---------------------------------------------------------------------
-- 热榜数据表
--   id: 应用层雪花算法生成（MyBatis-Plus IdType.ASSIGN_ID）
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `hot_news` (
  `id`           BIGINT       NOT NULL                COMMENT '主键ID（雪花算法）',
  `cluster_id`   VARCHAR(64)  NOT NULL                COMMENT '头条热榜唯一ID(ClusterId)',
  `title`        VARCHAR(512) NOT NULL                COMMENT '热点标题',
  `hot_value`    BIGINT       NOT NULL DEFAULT 0      COMMENT '热度值',
  `rank_no`      INT          NOT NULL DEFAULT 0      COMMENT '榜单排名(从1开始)',
  `source`       VARCHAR(32)  NOT NULL DEFAULT 'toutiao' COMMENT '来源(toutiao等)',
  `url`          VARCHAR(1024) DEFAULT NULL           COMMENT '详情链接',
  `batch_id`     VARCHAR(64)  DEFAULT NULL            COMMENT '采集批次ID',
  `collect_time` DATETIME     NOT NULL                COMMENT '本条采集时间',
  `deleted`      TINYINT      NOT NULL DEFAULT 0      COMMENT '软删除标记 0正常 1删除',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cluster_batch` (`cluster_id`, `batch_id`),
  KEY `idx_collect_time` (`collect_time`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_title` (`title`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='今日头条热榜数据表';

-- ---------------------------------------------------------------------
-- 采集批次日志表
--   记录每次采集的执行情况，便于排查与统计
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `hot_collect_log` (
  `id`           BIGINT       NOT NULL                COMMENT '主键ID（雪花算法）',
  `batch_id`     VARCHAR(64)  NOT NULL                COMMENT '采集批次ID',
  `source`       VARCHAR(32)  NOT NULL DEFAULT 'toutiao' COMMENT '来源(toutiao等)',
  `total_count`  INT          NOT NULL DEFAULT 0      COMMENT '本次采集条数',
  `success`      TINYINT      NOT NULL DEFAULT 1      COMMENT '是否成功 1成功 0失败',
  `error_msg`    VARCHAR(1024) DEFAULT NULL           COMMENT '错误信息',
  `cost_ms`      BIGINT       NOT NULL DEFAULT 0      COMMENT '耗时(毫秒)',
  `collect_time` DATETIME     NOT NULL                COMMENT '采集时间',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_collect_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热榜采集批次日志表';

-- ---------------------------------------------------------------------
-- 初始化示例：无（数据由采集服务写入）
-- ---------------------------------------------------------------------

SELECT '✅ ms_ds_hotnews 建库建表完成' AS message;
