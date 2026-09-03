package com.ms.learn.points.service;

import com.ms.learn.points.dto.EarnByConsumeRequest;
import com.ms.learn.points.dto.EarnResponse;
import com.ms.learn.points.entity.PointsAccount;
import com.ms.learn.points.entity.PointsRecord;

import java.util.List;

public interface PointsService {

    /**
     * 消费返积分（由交易模块 Feign 调用）
     * 规则：1 分钱 = 1 积分（按 t_points_rule.EARN_DEFAULT earn_rate 计算，默认 earn_rate=1）
     * 幂等：相同 refType + refNo 重复调用只发放一次。
     */
    EarnResponse earnByConsume(EarnByConsumeRequest request);

    /** 查询用户积分账户（没有则返回NULL或自动创建，根据实现决定） */
    PointsAccount getAccount(Long userId);

    /** 查询某用户积分变动明细（倒序） */
    List<PointsRecord> listRecords(Long userId);
}
