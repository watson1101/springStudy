package com.hong.heavenwill.controller;

import com.hong.heavenwill.entity.RandomEvent;
import com.hong.heavenwill.service.RandomEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 随机事件控制器
 * 提供随机事件相关的API接口
 */
@RestController
@RequestMapping("/api/random-event")
public class RandomEventController {

    @Autowired
    private RandomEventService randomEventService;

    /**
     * 手动触发随机事件
     * @param params 事件参数
     * @return 触发结果
     */
    @PostMapping("/trigger")
    public Map<String, Object> triggerRandomEvent(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 生成随机事件
            int eventType = params.containsKey("eventType") ? (int) params.get("eventType") : (int) (Math.random() * 5) + 1;
            
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
                
                // 尝试触发事件
                boolean triggered = randomEventService.triggerEvent(event);
                
                result.put("success", true);
                result.put("data", event);
                result.put("triggered", triggered);
                result.put("message", triggered ? "事件触发成功" : "事件生成成功，但未触发");
            } else {
                result.put("success", false);
                result.put("message", "生成事件失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "触发事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取活跃中的事件列表
     * @return 活跃事件列表
     */
    @GetMapping("/active")
    public Map<String, Object> getActiveEvents() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<RandomEvent> events = randomEventService.getActiveEvents();
            result.put("success", true);
            result.put("data", events);
            result.put("count", events.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取活跃事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 处理过期事件
     * @return 处理结果
     */
    @PostMapping("/expired/handle")
    public Map<String, Object> handleExpiredEvents() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = randomEventService.handleExpiredEvents();
            result.put("success", true);
            result.put("message", "处理过期事件成功");
            result.put("count", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理过期事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成天灾事件
     * @param params 事件参数
     * @return 生成的事件
     */
    @PostMapping("/natural-disaster")
    public Map<String, Object> generateNaturalDisaster(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RandomEvent event = randomEventService.generateNaturalDisaster(params);
            randomEventService.save(event);
            result.put("success", true);
            result.put("data", event);
            result.put("message", "生成天灾事件成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成天灾事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成宝物现世事件
     * @param params 事件参数
     * @return 生成的事件
     */
    @PostMapping("/treasure-discovery")
    public Map<String, Object> generateTreasureDiscovery(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RandomEvent event = randomEventService.generateTreasureDiscovery(params);
            randomEventService.save(event);
            result.put("success", true);
            result.put("data", event);
            result.put("message", "生成宝物现世事件成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成宝物现世事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成仙人降临事件
     * @param params 事件参数
     * @return 生成的事件
     */
    @PostMapping("/immortal-descent")
    public Map<String, Object> generateImmortalDescent(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RandomEvent event = randomEventService.generateImmortalDescent(params);
            randomEventService.save(event);
            result.put("success", true);
            result.put("data", event);
            result.put("message", "生成仙人降临事件成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成仙人降临事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成神兽出没事件
     * @param params 事件参数
     * @return 生成的事件
     */
    @PostMapping("/divine-beast")
    public Map<String, Object> generateDivineBeastAppearance(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RandomEvent event = randomEventService.generateDivineBeastAppearance(params);
            randomEventService.save(event);
            result.put("success", true);
            result.put("data", event);
            result.put("message", "生成神兽出没事件成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成神兽出没事件异常：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成灵气潮汐事件
     * @param params 事件参数
     * @return 生成的事件
     */
    @PostMapping("/spirit-tide")
    public Map<String, Object> generateSpiritTide(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RandomEvent event = randomEventService.generateSpiritTide(params);
            randomEventService.save(event);
            result.put("success", true);
            result.put("data", event);
            result.put("message", "生成灵气潮汐事件成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成灵气潮汐事件异常：" + e.getMessage());
        }
        
        return result;
    }
}