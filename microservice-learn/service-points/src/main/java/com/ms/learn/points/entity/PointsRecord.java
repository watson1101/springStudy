package com.ms.learn.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分变动明细（对应 ms_ds_points.t_points_record）
 */
@Data
@TableName("t_points_record")
public class PointsRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 流水号（唯一） */
    private String recordNo;

    /** 业务类型：EARN/SPEND/EXPIRE/ADJUST/REFUND */
    private String bizType;

    /** 变动类型：IN / OUT */
    private String changeType;

    /** 变动积分数量（正数） */
    private Long points;

    /** 变动后余额 */
    private Long balanceAft;

    /** 关联业务类型：PAYMENT/ORDER/ACTIVITY 等 */
    private String refType;

    /** 关联业务单号：如 t_payment.pay_no */
    private String refNo;

    /** 关联消费金额（分），biz_type=EARN 时填写，用于核对 1分=1积分 */
    private Long amountCent;

    /** 备注说明 */
    private String remark;

    /** 该笔积分过期时间（NULL 表示不过期） */
    private LocalDateTime expireTime;

    private LocalDateTime createTime;
}
