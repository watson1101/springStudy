package com.hong.heavenwill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hong.heavenwill.entity.RandomEvent;
import java.util.List;
import java.util.Map;

/**
 * 随机事件服务接口
 * 定义随机事件相关的业务逻辑
 */
public interface RandomEventService extends IService<RandomEvent> {

    /**
     * 生成天灾事件
     * @param params 事件参数
     * @return 生成的随机事件
     */
    RandomEvent generateNaturalDisaster(Map<String, Object> params);

    /**
     * 生成宝物现世事件
     * @param params 事件参数
     * @return 生成的随机事件
     */
    RandomEvent generateTreasureDiscovery(Map<String, Object> params);

    /**
     * 生成仙人降临事件
     * @param params 事件参数
     * @return 生成的随机事件
     */
    RandomEvent generateImmortalDescent(Map<String, Object> params);

    /**
     * 生成神兽出没事件
     * @param params 事件参数
     * @return 生成的随机事件
     */
    RandomEvent generateDivineBeastAppearance(Map<String, Object> params);

    /**
     * 生成灵气潮汐事件
     * @param params 事件参数
     * @return 生成的随机事件
     */
    RandomEvent generateSpiritTide(Map<String, Object> params);

    /**
     * 计算事件发生概率
     * @param eventType 事件类型
     * @param influenceFactors 影响因子
     * @return 事件发生的概率
     */
    double calculateProbability(Integer eventType, Map<String, Object> influenceFactors);

    /**
     * 触发随机事件
     * @param event 要触发的事件
     * @return 触发结果
     */
    boolean triggerEvent(RandomEvent event);

    /**
     * 获取活跃中的事件列表
     * @return 活跃事件列表
     */
    List<RandomEvent> getActiveEvents();

    /**
     * 处理过期事件
     * @return 处理的事件数量
     */
    int handleExpiredEvents();

    /**
     * 生成随机地点
     * @return 随机地点
     */
    String generateRandomLocation();
}