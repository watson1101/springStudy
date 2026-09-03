package com.ms.learn.points.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.points.dto.EarnByConsumeRequest;
import com.ms.learn.points.dto.EarnResponse;
import com.ms.learn.points.entity.PointsAccount;
import com.ms.learn.points.entity.PointsRecord;
import com.ms.learn.points.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 积分服务 REST 接口
 * <ul>
 *   <li>POST /api/points/earn/consume —— 交易模块 Feign 调用：按消费金额（分）发放积分，1 分 = 1 积分</li>
 *   <li>GET  /api/points/account/{userId} —— 查询用户积分账户</li>
 *   <li>GET  /api/points/records/{userId} —— 查询用户积分流水</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    /**
     * 交易模块调用 Map 版本（与 PointsFeignClient Map 版本对齐，避免 DTO 绑定冲突）
     * 也提供强类型 DTO 版本在内部复用。
     */
    @PostMapping("/earn/consume")
    public Result<Map<String, Object>> earnByConsume(@RequestBody Map<String, Object> body) {
        EarnByConsumeRequest req = new EarnByConsumeRequest();
        Object userId = body.get("userId");
        req.setUserId(userId == null ? null : Long.valueOf(String.valueOf(userId)));
        req.setRefType(body.get("refType") == null ? null : String.valueOf(body.get("refType")));
        req.setRefNo(body.get("refNo") == null ? null : String.valueOf(body.get("refNo")));
        Object amountCent = body.get("amountCent");
        req.setAmountCent(amountCent == null ? null : Long.valueOf(String.valueOf(amountCent)));
        Object amountYuan = body.get("amountYuan");
        req.setAmountYuan(amountYuan == null ? null : new BigDecimal(String.valueOf(amountYuan)));
        req.setRemark(body.get("remark") == null ? null : String.valueOf(body.get("remark")));

        EarnResponse resp = pointsService.earnByConsume(req);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("userId", resp.getUserId());
        data.put("recordNo", resp.getRecordNo());
        data.put("points", resp.getPoints());
        data.put("earnRate", resp.getEarnRate());
        data.put("amountCent", resp.getAmountCent());
        data.put("balanceAfter", resp.getBalanceAfter());
        data.put("refNo", resp.getRefNo());
        return Result.success(data);
    }

    /** 查询用户积分账户（首次访问自动创建空账户） */
    @GetMapping("/account/{userId}")
    public Result<PointsAccount> getAccount(@PathVariable Long userId) {
        return Result.success(pointsService.getAccount(userId));
    }

    /** 查询用户积分流水（倒序） */
    @GetMapping("/records/{userId}")
    public Result<List<PointsRecord>> listRecords(@PathVariable Long userId) {
        return Result.success(pointsService.listRecords(userId));
    }
}
