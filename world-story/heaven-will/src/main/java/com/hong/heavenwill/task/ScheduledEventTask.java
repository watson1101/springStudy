package com.hong.heavenwill.task;

import com.hong.heavenwill.entity.RandomEvent;
import com.hong.heavenwill.service.RandomEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 定时任务类
 * 负责定时生成和处理随机事件
 */
@Component
public class ScheduledEventTask {

    @Autowired
    private RandomEventService randomEventService;

    // 随机数生成器
    private static final Random RANDOM = new Random();

    /**
     * 每小时生成随机事件
     * cron表达式：0 0 * * * ? 表示每小时的第0分第0秒执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void generateRandomEvents() {
        System.out.println("开始生成随机事件：" + LocalDateTime.now());
        
        // 生成随机参数
        Map<String, Object> params = generateRandomParams();
        
        // 随机选择事件类型
        int eventType = RANDOM.nextInt(5) + 1;
        
        RandomEvent event = null;
        switch (eventType) {
            case 1:
                event = randomEventService.generateNaturalDisaster(params);
                break;
            case 2:
                event = randomEventService.generateTreasureDiscovery(params);
                break;
            case 3:
                event = randomEventService.generateImmortalDescent(params);
                break;
            case 4:
                event = randomEventService.generateDivineBeastAppearance(params);
                break;
            case 5:
                event = randomEventService.generateSpiritTide(params);
                break;
        }
        
        if (event != null) {
            // 保存事件
            randomEventService.save(event);
            System.out.println("生成随机事件成功：" + event.getEventName() + "，地点：" + event.getLocation());
            
            // 尝试触发事件
            boolean triggered = randomEventService.triggerEvent(event);
            if (triggered) {
                System.out.println("事件触发成功：" + event.getEventName());
            }
        }
    }

    /**
     * 每天处理过期事件
     * cron表达式：0 0 1 * * ? 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void handleExpiredEvents() {
        System.out.println("开始处理过期事件：" + LocalDateTime.now());
        int count = randomEventService.handleExpiredEvents();
        System.out.println("处理过期事件完成，共处理 " + count + " 个事件");
    }

    /**
     * 每3小时生成一次高概率事件
     * cron表达式：0 0 0/3 * * ? 表示每3小时执行一次
     */
    @Scheduled(cron = "0 0 0/3 * * ?")
    public void generateHighProbabilityEvent() {
        System.out.println("开始生成高概率事件：" + LocalDateTime.now());
        
        // 生成特殊参数，提高事件概率
        Map<String, Object> params = generateRandomParams();
        params.put("specialEvent", true);
        
        // 随机选择事件类型
        int eventType = RANDOM.nextInt(5) + 1;
        
        RandomEvent event = null;
        switch (eventType) {
            case 1:
                event = randomEventService.generateNaturalDisaster(params);
                break;
            case 2:
                event = randomEventService.generateTreasureDiscovery(params);
                break;
            case 3:
                event = randomEventService.generateImmortalDescent(params);
                break;
            case 4:
                event = randomEventService.generateDivineBeastAppearance(params);
                break;
            case 5:
                event = randomEventService.generateSpiritTide(params);
                break;
        }
        
        if (event != null) {
            // 强制提高概率
            event.setProbability(Math.min(1.0, event.getProbability() + 0.3));
            
            // 保存事件
            randomEventService.save(event);
            System.out.println("生成高概率事件成功：" + event.getEventName() + "，概率：" + event.getProbability());
            
            // 尝试触发事件
            boolean triggered = randomEventService.triggerEvent(event);
            if (triggered) {
                System.out.println("高概率事件触发成功：" + event.getEventName());
            }
        }
    }

    /**
     * 生成随机参数
     * @return 随机参数
     */
    private Map<String, Object> generateRandomParams() {
        Map<String, Object> params = new HashMap<>();
        
        // 随机季节
        String[] seasons = {"spring", "summer", "autumn", "winter"};
        params.put("season", seasons[RANDOM.nextInt(seasons.length)]);
        
        // 随机区域
        String[] regions = {"mountain", "ocean", "plain", "forest", "desert", "swamp"};
        params.put("region", regions[RANDOM.nextInt(regions.length)]);
        
        // 随机天气
        String[] weathers = {"sunny", "rainy", "cloudy", "windy", "stormy", "foggy"};
        params.put("weather", weathers[RANDOM.nextInt(weathers.length)]);
        
        // 随机时间因子
        params.put("timeFactor", RANDOM.nextDouble());
        
        return params;
    }

    /**
     * 生成随机地点
     * @return 随机地点
     */
    private String generateRandomLocation() {
        return randomEventService.generateRandomLocation();
    }
}