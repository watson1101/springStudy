-- ===================================================================
-- exception_log: 异常日志表（全局异常捕获落库）
-- 适用：各业务模块通用（方案1 —— 各服务在自己库中建同名表）
-- 命名规范：功能名（2026-09-21 起废除 OC_ 前缀规则）
-- 删除方式：软删除（deleted 字段），禁止物理删除
-- 落库位置：由 exception-log.db.table-name 配置，默认 exception_log
-- ===================================================================

CREATE TABLE IF NOT EXISTS `exception_log`
(
    `id`              BIGINT       NOT NULL COMMENT '主键（雪花算法）',
    `trace_id`        VARCHAR(64)           DEFAULT NULL COMMENT '链路追踪 ID',
    `service_name`    VARCHAR(64)  NOT NULL COMMENT '服务名',
    `exception_type`  VARCHAR(255) NOT NULL COMMENT '异常全类名',
    `message`         VARCHAR(1000)         DEFAULT NULL COMMENT '异常消息',
    `stack_trace`     TEXT                  DEFAULT NULL COMMENT '异常堆栈',
    `request_uri`     VARCHAR(500)          DEFAULT NULL COMMENT '请求 URI',
    `request_method`  VARCHAR(16)           DEFAULT NULL COMMENT '请求方法',
    `request_params`  TEXT                  DEFAULT NULL COMMENT '请求参数',
    `user_id`         VARCHAR(64)           DEFAULT NULL COMMENT '用户 ID',
    `ip`              VARCHAR(64)           DEFAULT NULL COMMENT '客户端 IP',
    `occur_time`      DATETIME     NOT NULL COMMENT '异常发生时间',
    `deleted`         TINYINT      NOT NULL DEFAULT 0 COMMENT '软删除标记：0未删除 1已删除',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_occur_time` (`occur_time`),
    KEY `idx_service_name` (`service_name`),
    KEY `idx_exception_type` (`exception_type`),
    KEY `idx_trace_id` (`trace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='异常日志表';
