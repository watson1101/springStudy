package com.ms.learn.points.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分账户（对应 ms_ds_points.t_points_account）
 */
@Data
@TableName("t_points_account")
public class PointsAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（唯一） */
    private Long userId;

    /** 累计获得积分 */
    private Long totalEarned;

    /** 累计消耗积分 */
    private Long totalSpent;

    /** 累计过期积分 */
    private Long totalExpired;

    /** 当前可用积分余额 */
    private Long balance;

    /** 乐观锁版本号（积分增减用） */
    @Version
    private Integer version;

    private LocalDateTime lastEarnTime;

    private LocalDateTime lastSpentTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
