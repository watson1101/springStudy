package com.hong.heavenwill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hong.heavenwill.entity.RandomEvent;
import com.hong.heavenwill.mapper.RandomEventMapper;
import com.hong.heavenwill.service.RandomEventService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机事件服务实现类
 * 实现随机事件相关的业务逻辑
 */
@Service
public class RandomEventServiceImpl extends ServiceImpl<RandomEventMapper, RandomEvent> implements RandomEventService {

    // 事件类型常量
    private static final int EVENT_TYPE_NATURAL_DISASTER = 1;
    private static final int EVENT_TYPE_TREASURE_DISCOVERY = 2;
    private static final int EVENT_TYPE_IMMORTAL_DESCENT = 3;
    private static final int EVENT_TYPE_DIVINE_BEAST = 4;
    private static final int EVENT_TYPE_SPIRIT_TIDE = 5;

    // 事件状态常量
    private static final int STATUS_NOT_OCCURRED = 0;
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_ENDED = 2;

    // 随机地点列表
    private static final List<String> LOCATIONS = Arrays.asList(
            "不周山", "昆仑山", "东海", "西极", "北溟", "南荒",
            "青丘山", "玄阴山", "火焰山", "冰川谷", "迷雾森林", "雷霆山脉",
            "沧海桑田", "虚空裂缝", "星辰之海", "黄泉路", "忘川河", "三生石"
    );

    @Override
    public RandomEvent generateNaturalDisaster(Map<String, Object> params) {
        RandomEvent event = new RandomEvent();
        event.setEventType(EVENT_TYPE_NATURAL_DISASTER);
        
        // 随机天灾类型
        List<String> disasters = Arrays.asList(
                "火山喷发", "地震", "洪水", "干旱", "飓风", "暴雪",
                "陨石坠落", "沙尘暴", "海啸", "瘟疫", "雷暴"
        );
        String disaster = disasters.get(ThreadLocalRandom.current().nextInt(disasters.size()));
        
        event.setEventName(disaster);
        event.setDescription("天地异象，" + disaster + "爆发，生灵涂炭，万物凋零。");
        event.setLocation(generateRandomLocation());
        event.setInfluenceRange("大范围");
        event.setProbability(calculateProbability(EVENT_TYPE_NATURAL_DISASTER, params));
        event.setStatus(STATUS_NOT_OCCURRED);
        event.setInfluenceFactors(params != null ? params : new HashMap<>());
        
        // 设置默认的发生时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        event.setOccurTime(now);
        event.setEndTime(now.plusHours(ThreadLocalRandom.current().nextInt(1, 25)));
        
        return event;
    }

    @Override
    public RandomEvent generateTreasureDiscovery(Map<String, Object> params) {
        RandomEvent event = new RandomEvent();
        event.setEventType(EVENT_TYPE_TREASURE_DISCOVERY);
        
        // 随机宝物类型
        List<String> treasures = Arrays.asList(
                "上古神器", "修真秘籍", "珍稀药材", "天材地宝", "神秘遗迹",
                "龙蛋", "凤凰之羽", "麒麟之角", "玄武之甲", "白虎之牙"
        );
        String treasure = treasures.get(ThreadLocalRandom.current().nextInt(treasures.size()));
        
        event.setEventName(treasure + "现世");
        event.setDescription("天地感应，" + treasure + "现世，引动四方风云变幻。");
        event.setLocation(generateRandomLocation());
        event.setInfluenceRange("中范围");
        event.setProbability(calculateProbability(EVENT_TYPE_TREASURE_DISCOVERY, params));
        event.setStatus(STATUS_NOT_OCCURRED);
        event.setInfluenceFactors(params != null ? params : new HashMap<>());
        
        // 设置默认的发生时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        event.setOccurTime(now);
        event.setEndTime(now.plusDays(ThreadLocalRandom.current().nextInt(1, 8)));
        
        return event;
    }

    @Override
    public RandomEvent generateImmortalDescent(Map<String, Object> params) {
        RandomEvent event = new RandomEvent();
        event.setEventType(EVENT_TYPE_IMMORTAL_DESCENT);
        
        // 随机仙人类型
        List<String> immortals = Arrays.asList(
                "道教仙人", "佛教菩萨", "上古真神", "散仙", "地仙",
                "金仙", "大罗金仙", "混元圣人", "医仙", "剑仙"
        );
        String immortal = immortals.get(ThreadLocalRandom.current().nextInt(immortals.size()));
        
        event.setEventName(immortal + "降临");
        event.setDescription("祥云瑞气缭绕，" + immortal + "下界，欲点化有缘人。");
        event.setLocation(generateRandomLocation());
        event.setInfluenceRange("小范围");
        event.setProbability(calculateProbability(EVENT_TYPE_IMMORTAL_DESCENT, params));
        event.setStatus(STATUS_NOT_OCCURRED);
        event.setInfluenceFactors(params != null ? params : new HashMap<>());
        
        // 设置默认的发生时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        event.setOccurTime(now);
        event.setEndTime(now.plusHours(ThreadLocalRandom.current().nextInt(1, 13)));
        
        return event;
    }

    @Override
    public RandomEvent generateDivineBeastAppearance(Map<String, Object> params) {
        RandomEvent event = new RandomEvent();
        event.setEventType(EVENT_TYPE_DIVINE_BEAST);
        
        // 随机神兽类型
        List<String> beasts = Arrays.asList(
                "青龙", "白虎", "朱雀", "玄武", "麒麟",
                "凤凰", "龙", "鲲鹏", "饕餮", "梼杌", "穷奇", "混沌"
        );
        String beast = beasts.get(ThreadLocalRandom.current().nextInt(beasts.size()));
        
        event.setEventName(beast + "出没");
        event.setDescription("" + beast + "现世，天地变色，异象丛生。");
        event.setLocation(generateRandomLocation());
        event.setInfluenceRange("大范围");
        event.setProbability(calculateProbability(EVENT_TYPE_DIVINE_BEAST, params));
        event.setStatus(STATUS_NOT_OCCURRED);
        event.setInfluenceFactors(params != null ? params : new HashMap<>());
        
        // 设置默认的发生时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        event.setOccurTime(now);
        event.setEndTime(now.plusDays(ThreadLocalRandom.current().nextInt(1, 4)));
        
        return event;
    }

    @Override
    public RandomEvent generateSpiritTide(Map<String, Object> params) {
        RandomEvent event = new RandomEvent();
        event.setEventType(EVENT_TYPE_SPIRIT_TIDE);
        
        event.setEventName("灵气潮汐");
        event.setDescription("天地灵气涌动，形成潮汐，对修炼者大有裨益。");
        event.setLocation(generateRandomLocation());
        event.setInfluenceRange("全区域");
        event.setProbability(calculateProbability(EVENT_TYPE_SPIRIT_TIDE, params));
        event.setStatus(STATUS_NOT_OCCURRED);
        event.setInfluenceFactors(params != null ? params : new HashMap<>());
        
        // 设置默认的发生时间和结束时间
        LocalDateTime now = LocalDateTime.now();
        event.setOccurTime(now);
        event.setEndTime(now.plusDays(ThreadLocalRandom.current().nextInt(3, 15)));
        
        return event;
    }

    @Override
    public double calculateProbability(Integer eventType, Map<String, Object> influenceFactors) {
        // 基础概率
        double baseProbability = switch (eventType) {
            case EVENT_TYPE_NATURAL_DISASTER -> 0.2;  // 20%
            case EVENT_TYPE_TREASURE_DISCOVERY -> 0.15; // 15%
            case EVENT_TYPE_IMMORTAL_DESCENT -> 0.1;   // 10%
            case EVENT_TYPE_DIVINE_BEAST -> 0.05;      // 5%
            case EVENT_TYPE_SPIRIT_TIDE -> 0.3;        // 30%
            default -> 0.1;                           // 默认10%
        };

        // 如果有影响因子，根据影响因子调整概率
        if (influenceFactors != null && !influenceFactors.isEmpty()) {
            // 示例：季节因子影响
            if (influenceFactors.containsKey("season")) {
                String season = (String) influenceFactors.get("season");
                if ("spring".equals(season)) {
                    baseProbability += 0.05; // 春季增加5%概率
                } else if ("winter".equals(season)) {
                    baseProbability -= 0.05; // 冬季减少5%概率
                }
            }

            // 示例：特殊节日因子影响
            if (influenceFactors.containsKey("specialEvent")) {
                boolean specialEvent = (boolean) influenceFactors.get("specialEvent");
                if (specialEvent) {
                    baseProbability += 0.1; // 特殊事件期间增加10%概率
                }
            }

            // 示例：区域因子影响
            if (influenceFactors.containsKey("region")) {
                String region = (String) influenceFactors.get("region");
                if ("mountain".equals(region)) {
                    // 山地对某些事件有加成
                    if (eventType == EVENT_TYPE_IMMORTAL_DESCENT || eventType == EVENT_TYPE_TREASURE_DISCOVERY) {
                        baseProbability += 0.08;
                    }
                }
            }
        }

        // 确保概率在0-1之间
        return Math.min(1.0, Math.max(0.0, baseProbability));
    }

    @Override
    public boolean triggerEvent(RandomEvent event) {
        // 检查事件是否可以触发
        if (event.getStatus() != STATUS_NOT_OCCURRED) {
            return false;
        }

        // 根据概率决定是否触发
        double probability = event.getProbability();
        boolean shouldTrigger = ThreadLocalRandom.current().nextDouble() < probability;

        if (shouldTrigger) {
            // 触发事件
            event.setStatus(STATUS_ACTIVE);
            event.setOccurTime(LocalDateTime.now());
            this.updateById(event);
        }

        return shouldTrigger;
    }

    @Override
    public List<RandomEvent> getActiveEvents() {
        // 查询状态为活跃中的事件
        return this.lambdaQuery()
                .eq(RandomEvent::getStatus, STATUS_ACTIVE)
                .list();
    }

    @Override
    public int handleExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();
        // 查询并更新已过期的事件
        return this.lambdaUpdate()
                .eq(RandomEvent::getStatus, STATUS_ACTIVE)
                .lt(RandomEvent::getEndTime, now)
                .set(RandomEvent::getStatus, STATUS_ENDED)
                .update();
    }

    @Override
    public String generateRandomLocation() {
        // 随机选择一个地点
        return LOCATIONS.get(ThreadLocalRandom.current().nextInt(LOCATIONS.size()));
    }
}