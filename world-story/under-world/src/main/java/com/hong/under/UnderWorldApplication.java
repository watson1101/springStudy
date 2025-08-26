package com.hong.under;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 地府服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hong.under.mapper")
public class UnderWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnderWorldApplication.class, args);
    }
}