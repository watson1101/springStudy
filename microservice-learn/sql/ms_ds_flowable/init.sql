-- ============================================================
-- Flowable 工作流模块数据库 MySQL 初始化脚本：ms_ds_flowable
-- 说明：
--   Flowable 7.x 默认把 ACT_GE_BYTEARRAY/ACT_GE_PROPERTY 等全部引擎表建在当前数据库内，
--   本服务对应数据源已改为 jdbc:mysql://192.168.0.27:3306/ms_ds_flowable（库名遵守 ms_ds_ 前缀规范）。
--   注意：Flowable 默认 database-schema-update=true，表结构会由引擎自动创建/升级，
--        该脚本仅负责提前建库 + 统一字符集/排序规则，避免启动时 Unknown database 'ms_ds_flowable'。
-- 执行：mysql -uroot -p < sql/ms_ds_flowable/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_flowable
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_flowable;

-- 说明：
--   引擎表（ACT_* / FLW_*）由 Flowable 自动创建（application.yml 配置 flowable.process.database-schema-update: true）
--   无需在此手工建表；如需手动建表，可执行：
--   flowable-engine 依赖自带
--   org/flowable/db/create/flowable.mysql.create.engine.sql、identity.sql、history.sql 等
