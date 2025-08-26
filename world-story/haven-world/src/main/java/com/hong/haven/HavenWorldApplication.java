package com.hong.haven;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 天界服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hong.haven.mapper")
public class HavenWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HavenWorldApplication.class, args);
    }
}