package com.hong;

import com.hong.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.hong.mapper")
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.hong.api.client", defaultConfiguration = DefaultFeignConfig.class)
// 指定 FeignClient 所在包
@Slf4j
public class ItemServiceApplication {
    public static void main(String[] args) {

        log.info("-------------------------------------> Hello, this is item service.");
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}