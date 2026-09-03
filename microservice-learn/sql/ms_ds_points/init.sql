-- ============================================================
-- 积分模块数据库初始化脚本 ms_ds_points
-- 说明：对应微服务 service-points
--   每消费 1 分钱 = 获得 1 积分（即 1 元 = 100 积分）
--   由交易模块在支付成功后通过 Feign 调用发放
-- 执行：mysql -uroot -p < sql/ms_ds_points/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS ms_ds_points
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ms_ds_points;

-- ============================================================
-- 1) 用户积分账户表 t_points_account
--    以用户为维度，维护积分余额、累计发放、累计消耗、累计过期
-- ============================================================
DROP TABLE IF EXISTS t_points_account;

CREATE TABLE t_points_account (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT       NOT NULL COMMENT '用户ID（来自 service-user，全局唯一）',
    total_earned    BIGINT       NOT NULL DEFAULT 0 COMMENT '累计获得积分',
    total_spent     BIGINT       NOT NULL DEFAULT 0 COMMENT '累计消耗积分',
    total_expired   BIGINT       NOT NULL DEFAULT 0 COMMENT '累计过期积分',
    balance         BIGINT       NOT NULL DEFAULT 0 COMMENT '当前可用积分余额',
    version         INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号（积分增减用）',
    last_earn_time  DATETIME              DEFAULT NULL COMMENT '最近一次获得积分时间',
    last_spent_time DATETIME              DEFAULT NULL COMMENT '最近一次消耗积分时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_balance (balance)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户积分账户表（service-points 对应 ms_ds_points.t_points_account）';

-- ============================================================
-- 2) 积分变动明细表 t_points_record
--    记录每一次积分的获得/消耗/过期，可溯源
-- ============================================================
DROP TABLE IF EXISTS t_points_record;

CREATE TABLE t_points_record (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id      BIGINT       NOT NULL COMMENT '用户ID',
    record_no    VARCHAR(64)  NOT NULL COMMENT '流水号（唯一）',
    biz_type     VARCHAR(32)  NOT NULL COMMENT '业务类型：EARN=获得(消费返)/SPEND=消耗/EXPIRE=过期/ADJUST=调账/REFUND=退还',
    change_type  VARCHAR(16)  NOT NULL COMMENT '变动类型：IN 增加 / OUT 扣减',
    points       BIGINT       NOT NULL COMMENT '变动积分数量（正数）',
    balance_aft  BIGINT       NOT NULL COMMENT '变动后余额',
    ref_type     VARCHAR(64)           DEFAULT NULL COMMENT '关联业务类型：PAYMENT/ORDER/ACTIVITY等',
    ref_no       VARCHAR(64)           DEFAULT NULL COMMENT '关联业务单号：如 t_payment.pay_no',
    amount_cent  BIGINT                DEFAULT NULL COMMENT '关联消费金额（分），biz_type=EARN 时填写，用于核对 1分=1积分',
    remark       VARCHAR(512)          DEFAULT NULL COMMENT '备注说明',
    expire_time  DATETIME              DEFAULT NULL COMMENT '该笔积分过期时间（NULL 表示不过期）',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_no (record_no),
    KEY idx_points_record_user_time (user_id, create_time),
    KEY idx_points_record_ref (ref_type, ref_no),
    KEY idx_points_record_biz (biz_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '积分变动明细表（每一次积分的来龙去脉）';

-- ============================================================
-- 3) 积分过期明细(可选扩展) t_points_expire_detail
--    如果要按批次过期（消费返的积分X天内有效），则按批次登记；
--    简化模型可直接用 t_points_record.expire_time + 定时任务。
-- ============================================================
DROP TABLE IF EXISTS t_points_expire_detail;

CREATE TABLE t_points_expire_detail (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id      BIGINT       NOT NULL COMMENT '用户ID',
    record_id    BIGINT       NOT NULL COMMENT '来源 EARN 记录ID（t_points_record.id）',
    total_points BIGINT       NOT NULL COMMENT '该批次总积分',
    remain_points BIGINT      NOT NULL COMMENT '剩余未使用/未过期积分',
    expire_time  DATETIME     NOT NULL COMMENT '过期时间',
    status       VARCHAR(16)  NOT NULL DEFAULT 'VALID' COMMENT '状态：VALID有效 / USED已使用 / EXPIRED已过期',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_expire_user_time (user_id, expire_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '积分批次过期明细表（可选扩展，按 FIFO 消费）';

-- ============================================================
-- 4) 发放规则表 t_points_rule
--    目前业务规则：每消费1分钱=获得1积分，即 earn_rate=1 point/cent
--    后续如需扩展（活动加倍等），可通过此表配置动态化。
-- ============================================================
DROP TABLE IF EXISTS t_points_rule;

CREATE TABLE t_points_rule (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    rule_code   VARCHAR(64)  NOT NULL COMMENT '规则编码（唯一）',
    rule_name   VARCHAR(128) NOT NULL COMMENT '规则名称',
    biz_type    VARCHAR(32)  NOT NULL DEFAULT 'EARN' COMMENT '适用业务类型：EARN/SPEND/EXPIRE',
    earn_rate   INT          NOT NULL DEFAULT 1 COMMENT '获得积分数/每分钱；默认1表示 1分钱=1积分',
    expire_days INT                   DEFAULT NULL COMMENT '获得后N天过期；NULL表示不过期',
    enabled     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用 0否 1是',
    priority    INT          NOT NULL DEFAULT 0 COMMENT '优先级（越大越优先）',
    remark      VARCHAR(512)          DEFAULT NULL COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_code (rule_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '积分规则表（当前默认：EARN_DEFAULT 每分钱=1积分 不过期）';

-- ------------------------------------------------------------
-- 4.1 初始化：默认积分发放规则
-- ------------------------------------------------------------
INSERT INTO t_points_rule(rule_code, rule_name, biz_type, earn_rate, expire_days, enabled, priority, remark)
VALUES ('EARN_DEFAULT', '默认消费返积分', 'EARN', 1, NULL, 1, 0, '每消费1分钱=获得1积分，不过期')
ON DUPLICATE KEY UPDATE earn_rate   = VALUES(earn_rate),
                        expire_days = VALUES(expire_days),
                        enabled     = VALUES(enabled),
                        remark      = VALUES(remark);
