package com.ms.learn.hotnews.collector.config;

import com.ms.learn.hotnews.collector.service.HotNewsCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * 动态定时任务配置
 *
 * <p>cron 表达式从 Nacos 配置读取（hotnews.collect.cron），不写死在注解中。</p>
 * <p>使用 {@link SchedulingConfigurer} + {@link CronTrigger}，配合 {@link RefreshScope}，
 * 当 Nacos 配置变更时（Spring Cloud 自动刷新），定时任务会以新的 cron 重新注册，无需重启。</p>
 *
 * <p>说明：SchedulingConfigurer.configureTasks 在刷新时会被重新调用，
 * 通过持有上一次的 Trigger 判断 cron 是否变化，实现动态更新。</p>
 */
@Slf4j
@Configuration
@RefreshScope
@RequiredArgsConstructor
public class DynamicScheduleConfig implements SchedulingConfigurer {

    private final HotNewsCollectService collectService;

    /** cron 直接以字符串注入，便于变化检测 */
    @Value("${hotnews.collect.cron:0 0/30 * * * ?}")
    private String cron;

    /** 是否启用定时采集 */
    @Value("${hotnews.collect.enabled:true}")
    private boolean enabled;

    /** 记录当前已注册的 cron，用于检测变化 */
    private volatile String currentCron;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (!enabled) {
            log.warn("热榜定时采集已禁用（hotnews.collect.enabled=false）");
            return;
        }
        if (cron == null || cron.isBlank()) {
            log.warn("热榜定时采集 cron 为空，跳过注册");
            return;
        }

        // 避免重复注册同一个 cron（刷新时可能重复调用）
        if (cron.equals(currentCron)) {
            return;
        }
        currentCron = cron;
        log.info("注册热榜定时采集任务，cron={}", cron);

        String cronToUse = cron;
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        collectService.collectAndSend();
                    } catch (Exception e) {
                        // 定时任务内部异常不应中断后续调度
                        log.error("定时采集执行异常", e);
                    }
                },
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(cronToUse);
                    java.util.Date next = trigger.nextExecutionTime(triggerContext);
                    return next == null ? null : next.toInstant();
                }
        );
    }
}
