package com.ms.learn.transaction.feign;

import com.ms.learn.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 调用积分模块 service-points
 * <p>在支付成功后，调用该 Feign 发放积分：1 分钱 = 1 积分。</p>
 */
@FeignClient(name = "service-points")
public interface PointsFeignClient {

    /**
     * 发放消费返积分（由交易模块在支付成功后调用）
     * @param body { userId, refType, refNo, amountCent, amountYuan, remark }
     * @return { recordNo, points, balanceAfter }
     */
    @PostMapping("/api/points/earn/consume")
    Result<Map<String, Object>> earnByConsume(@RequestBody Map<String, Object> body);

    // 兼容用：后续积分模块若先提供了明确 DTO 版本，可直接替换为强类型接口。
    // 这里先用 Map 降低两方联调耦合。
}
