-- ============================================================
-- 交易模块数据库初始化脚本 ms_ds_transaction
-- 说明：对应微服务 service-transaction
--   接收支付请求 -> 模拟2s处理 -> 返回交易成功 -> 调用积分模块发放积分
--   1 分钱 = 1 积分（由积分模块计算）
-- 执行：mysql -uroot -p < sql/ms_ds_transaction/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_transaction
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_transaction;

-- ============================================================
-- 1) 支付交易表 t_payment
--    记录每次支付请求及最终交易结果
-- ============================================================
DROP TABLE IF EXISTS t_payment;

CREATE TABLE t_payment (
    id             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    pay_no         VARCHAR(64)   NOT NULL COMMENT '交易流水号（唯一）',
    user_id        BIGINT        NOT NULL COMMENT '用户ID（来自 service-user）',
    order_no       VARCHAR(64)            DEFAULT NULL COMMENT '关联订单号（可选）',
    amount         DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '支付金额（元，精度到分）',
    channel        VARCHAR(32)   NOT NULL DEFAULT 'MOCK' COMMENT '支付渠道：MOCK/ALIPAY/WECHAT/UNIONPAY',
    status         VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '交易状态：PENDING处理中/SUCCESS成功/FAIL失败/CLOSED关闭',
    fail_reason    VARCHAR(512)           DEFAULT NULL COMMENT '失败原因',
    req_no         VARCHAR(64)            DEFAULT NULL COMMENT '客户端请求号（幂等）',
    pay_time       DATETIME               DEFAULT NULL COMMENT '支付完成时间',
    points_granted TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '是否已调用积分模块发放积分：0否 1是',
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_pay_no (pay_no),
    UNIQUE KEY uk_req_no (req_no),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '支付交易表（service-transaction 对应 ms_ds_transaction.t_payment）';

-- ============================================================
-- 2) 交易流水日志表 t_payment_log
--    记录支付请求、回调、状态变更等每一步流水，便于审计与排障
-- ============================================================
DROP TABLE IF EXISTS t_payment_log;

CREATE TABLE t_payment_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    pay_no      VARCHAR(64)  NOT NULL COMMENT '关联交易流水号',
    action      VARCHAR(64)  NOT NULL COMMENT '动作：CREATE/PROCESS/SUCCESS/FAIL/CALL_POINTS/RETRY',
    status_before VARCHAR(20)         DEFAULT NULL COMMENT '变更前状态',
    status_after  VARCHAR(20)         DEFAULT NULL COMMENT '变更后状态',
    content     VARCHAR(1024)         DEFAULT NULL COMMENT '详细内容/请求响应JSON',
    operator    VARCHAR(64)           DEFAULT 'SYSTEM' COMMENT '操作方：SYSTEM/USER/CHANNEL',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_payment_log_pay_no (pay_no),
    KEY idx_payment_log_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '交易流水日志表';

-- ============================================================
-- 3) 示例数据（可选）：一笔模拟已完成交易
-- ============================================================
-- INSERT INTO t_payment(pay_no, user_id, order_no, amount, channel, status, pay_time, points_granted, req_no)
-- VALUES ('PAY20260903000001', 1, 'ORD20260903000001', 99.99, 'MOCK', 'SUCCESS', NOW(), 1, 'REQ20260903000001');
