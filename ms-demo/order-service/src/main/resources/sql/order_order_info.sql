-- =============================================
-- 订单服务数据库表结构
-- 数据库: ms-demo
-- 主键ID使用BIGINT类型，通过雪花算法生成
-- =============================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS order_order_item;
DROP TABLE IF EXISTS order_order_info;

-- =============================================
-- 订单信息表
-- =============================================
CREATE TABLE order_order_info (
    -- 订单ID，使用雪花算法生成64位Long型ID
    id BIGINT NOT NULL PRIMARY KEY COMMENT '订单ID（雪花算法生成）',

    -- 订单基本信息
    order_no VARCHAR(100) NOT NULL UNIQUE COMMENT '订单编号（用户可读）',

    -- 用户信息
    user_id BIGINT NOT NULL COMMENT '用户ID（关联user_user_info）',
    user_name VARCHAR(100) COMMENT '用户名称',

    -- 金额信息
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    pay_amount DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    freight_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '运费',

    -- 状态
    status SMALLINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付 1-已支付 2-已发货 3-已完成 4-已取消',
    pay_type SMALLINT DEFAULT NULL COMMENT '支付方式：1-微信支付 2-支付宝 3-银联',
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',

    -- 时间戳
    pay_time TIMESTAMP COMMENT '支付时间',
    delivery_time TIMESTAMP COMMENT '发货时间',
    receive_time TIMESTAMP COMMENT '收货时间',

    -- 收货信息
    receiver_name VARCHAR(100) COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) COMMENT '收货人电话',
    receiver_address VARCHAR(500) COMMENT '收货地址',

    -- 备注
    remark VARCHAR(500) COMMENT '订单备注',

    -- 审计字段
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',

    -- 索引
    INDEX idx_order_no (order_no) COMMENT '订单编号索引',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_status (status, is_deleted) COMMENT '状态索引',
    INDEX idx_create_time (create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- =============================================
-- 订单明细表
-- =============================================
CREATE TABLE order_order_item (
    -- 明细ID，使用雪花算法生成64位Long型ID
    id BIGINT NOT NULL PRIMARY KEY COMMENT '明细ID（雪花算法生成）',

    -- 关联订单
    order_id BIGINT NOT NULL COMMENT '订单ID（关联order_order_info）',

    -- 商品信息
    product_id BIGINT NOT NULL COMMENT '商品ID（关联goods_product）',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_code VARCHAR(100) COMMENT '商品编码',
    product_image VARCHAR(500) COMMENT '商品图片',
    category_id BIGINT COMMENT '分类ID',
    specification VARCHAR(500) COMMENT '商品规格',

    -- 金额信息
    unit_price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计金额',

    -- 审计
    is_deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(64) COMMENT '创建人',
    update_by VARCHAR(64) COMMENT '更新人',

    -- 索引
    INDEX idx_order_id (order_id) COMMENT '订单ID索引',
    INDEX idx_product_id (product_id) COMMENT '商品ID索引',
    INDEX idx_create_time (create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';