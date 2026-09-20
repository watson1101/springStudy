package com.ms.learn.hotnews.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 采集相关配置（来自 Nacos，支持动态刷新）
 *
 * <p>对应 Nacos DataId: service-hotnews-collector.yaml 中的 hotnews.collect.*</p>
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "hotnews.collect")
public class CollectProperties {

    /** 定时任务 cron 表达式（默认每 30 分钟）—— 从 Nacos 动态读取，不写死 */
    private String cron = "0 0/30 * * * ?";

    /** 是否启用定时采集 */
    private boolean enabled = true;

    /** 抓取目标地址 */
    private String url = "https://www.toutiao.com/hot-event/hot-board/?origin=toutiao_pc";

    /** 抓取超时（毫秒） */
    private int timeout = 10000;

    /** 抓取时携带的 User-Agent */
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";
}
