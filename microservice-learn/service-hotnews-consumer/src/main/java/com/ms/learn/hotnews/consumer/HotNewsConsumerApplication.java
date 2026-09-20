package com.ms.learn.hotnews.consumer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 热点资讯消费服务启动类
 *
 * <p>职责：监听 RocketMQ 热榜消息，写入 MySQL ms_ds_hotnews。</p>
 */
@SpringBootApplication
@MapperScan("com.ms.learn.hotnews.consumer.mapper")
public class HotNewsConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotNewsConsumerApplication.class, args);
    }
}
