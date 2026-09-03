package com.ms.learn.transaction.service;

import com.ms.learn.transaction.dto.PaymentRequest;
import com.ms.learn.transaction.entity.Payment;

import java.util.List;

public interface PaymentService {

    /**
     * 发起一笔支付请求
     * <ol>
     *   <li>落库：状态 PENDING</li>
     *   <li>模拟支付网关处理（sleep 2s）</li>
     *   <li>将交易标记为 SUCCESS（mock 全部成功）</li>
     *   <li>Feign 调用积分模块 service-points 发放积分：每 1 分钱 = 1 积分</li>
     * </ol>
     */
    Payment pay(PaymentRequest request);

    /** 查询某用户的支付交易列表（简化：全量返回） */
    List<Payment> listByUser(Long userId);

    /** 根据交易流水号查询 */
    Payment getByPayNo(String payNo);
}
