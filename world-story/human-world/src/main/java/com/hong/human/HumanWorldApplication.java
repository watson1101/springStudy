package com.hong.human;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 人间服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hong.human.mapper")
public class HumanWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HumanWorldApplication.class, args);
    }
}