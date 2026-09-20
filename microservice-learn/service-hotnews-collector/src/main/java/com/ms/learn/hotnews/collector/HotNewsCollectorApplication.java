package com.ms.learn.hotnews.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 热点资讯采集服务启动类
 *
 * <p>职责：定时（cron 从 Nacos 动态读取）抓取今日头条热榜，投递到 RocketMQ。</p>
 */
@SpringBootApplication
@EnableScheduling
public class HotNewsCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotNewsCollectorApplication.class, args);
    }
}
