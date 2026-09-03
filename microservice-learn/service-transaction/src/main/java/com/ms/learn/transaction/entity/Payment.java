package com.ms.learn.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付交易实体（对应 ms_ds_transaction.t_payment）
 */
@Data
@TableName("t_payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 交易流水号（唯一） */
    private String payNo;

    /** 用户ID（来自 service-user） */
    private Long userId;

    /** 关联订单号（可选） */
    private String orderNo;

    /** 支付金额（元，精度到分） */
    private BigDecimal amount;

    /** 支付渠道：MOCK/ALIPAY/WECHAT/UNIONPAY */
    private String channel;

    /** 交易状态：PENDING/SUCCESS/FAIL/CLOSED */
    private String status;

    /** 失败原因 */
    private String failReason;

    /** 客户端请求号（幂等） */
    private String reqNo;

    /** 支付完成时间 */
    private LocalDateTime payTime;

    /** 是否已调用积分模块发放积分：0否 1是 */
    private Integer pointsGranted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
