package com.hong.routers;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RequiredArgsConstructor
public class DynamicRouteLoader {

    private final NacosConfigManager nacosConfigManager;

    private final RouteDefinitionWriter writer;

    private final String dataId = "hm-gateway-routes";
    private final String group = "DEFAULT_GROUP";

    private final Set<String> routeIds = new HashSet<>();

    // 在项目初始化时执行（在这个bean初始化的时候执行）
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        // 1. 项目启动时，先拉取一次配置，并添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        // 监听到配置变更
                        updateConfigInfo(configInfo);
                    }
                });
        // 初次读取到配置信息时，执行更新逻辑
        updateConfigInfo(configInfo);

    }

    public void updateConfigInfo(String configInfo) {
        log.info("监听到配置文件更新：{}", configInfo);
        // 解析配置文件，转为 RouterDefinition
        List<RouteDefinition> routeDefinitionList = JSONUtil.toList(configInfo, RouteDefinition.class);
        // 2. 删除旧的路由表
        routeIds.forEach(routeId -> {
            writer.delete(Mono.just(routeId)).subscribe();
        });

        // 3.更新路由表
        for (RouteDefinition routeDefinition : routeDefinitionList){
            writer.save(Mono.just(routeDefinition)).subscribe();
            // 记录路由id，用于下次删除
            routeIds.add(routeDefinition.getId());
        }
    }

}
