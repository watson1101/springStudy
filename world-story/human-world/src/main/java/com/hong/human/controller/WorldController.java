package com.hong.human.controller;

import com.hong.common.feign.HavenFeignClient;
import com.hong.common.feign.UnderFeignClient;
import com.hong.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 世界控制器，演示服务间调用
 */
@Slf4j
@RestController
@RequestMapping("/api/world")
public class WorldController {

    @Autowired
    private HavenFeignClient havenFeignClient;

    @Autowired
    private UnderFeignClient underFeignClient;

    /**
     * 获取三界信息
     */
    @GetMapping("/info")
    public Result<Map<String, String>> getWorldInfo() {
        log.info("获取三界信息");
        
        // 调用天界服务
        Result<String> havenResult = havenFeignClient.getHavenInfo();
        
        // 调用地府服务
        Result<String> underResult = underFeignClient.getUnderInfo();
        
        // 组装结果
        Map<String, String> worldInfo = new HashMap<>();
        worldInfo.put("human", "这里是人间，凡人居住的地方");
        
        if (havenResult.isSuccess()) {
            worldInfo.put("haven", havenResult.getData());
        } else {
            worldInfo.put("haven", "天界服务暂时不可用");
        }
        
        if (underResult.isSuccess()) {
            worldInfo.put("under", underResult.getData());
        } else {
            worldInfo.put("under", "地府服务暂时不可用");
        }
        
        return Result.success(worldInfo);
    }
}