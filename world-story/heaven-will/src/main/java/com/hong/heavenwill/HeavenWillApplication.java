package com.hong.heavenwill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 天道模块主启动类
 * 负责随机事件生成、概率计算、定时任务等功能
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.hong.heavenwill.mapper")
@EnableScheduling
public class HeavenWillApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeavenWillApplication.class, args);
    }

}