package com.ms.learn.transaction.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发起支付请求 DTO
 */
@Data
public class PaymentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 关联订单号（可选） */
    private String orderNo;

    /** 支付金额（元，支持到分，如 99.99 表示 99元99分 = 9999 分） */
    private BigDecimal amount;

    /** 支付渠道：默认 MOCK，其他 ALIPAY/WECHAT/UNIONPAY 可选 */
    private String channel;

    /** 客户端请求号（幂等，建议调用方提供） */
    private String reqNo;
}
