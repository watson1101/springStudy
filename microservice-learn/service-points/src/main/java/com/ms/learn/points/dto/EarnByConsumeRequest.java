package com.ms.learn.points.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 交易模块调用：消费返积分请求 DTO
 */
@Data
public class EarnByConsumeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 关联业务类型：交易模块传 PAYMENT */
    private String refType;

    /** 关联业务单号：如交易流水号 payNo */
    private String refNo;

    /** 消费金额（分）；若该字段提供，则优先用其计算 1分=1积分 */
    private Long amountCent;

    /** 消费金额（元），与 amountCent 二选一或同时提供，用于 amountCent 缺失时兜底换算 */
    private BigDecimal amountYuan;

    /** 备注 */
    private String remark;
}
