package com.ms.learn.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 积分发放返回 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 变动流水号 */
    private String recordNo;

    /** 实际发放积分（1 分钱 = 1 积分） */
    private Long points;

    /** 按积分规则的 earn_rate（一般=1） */
    private Integer earnRate;

    /** 关联消费金额（分），用于核对 points = amountCent * earnRate */
    private Long amountCent;

    /** 发放后余额 */
    private Long balanceAfter;

    /** 关联业务单号（入参回显） */
    private String refNo;
}
