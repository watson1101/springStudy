package com.ms.learn.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 系统管理模块启动类
 * 用于管理系统相关设置
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.ms.learn.system.mapper")
public class MsDsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDsSystemApplication.class, args);
    }
}
