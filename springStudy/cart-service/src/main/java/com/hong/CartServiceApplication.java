package com.hong;

import com.hong.api.config.DefaultFeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@MapperScan("com.hong.mapper")
@EnableTransactionManagement
@Slf4j
//@EnableFeignClients
@EnableFeignClients(basePackages = "com.hong.api.client" , defaultConfiguration  = DefaultFeignConfig.class) // 指定 FeignClient 所在包
// @EnableFeignClient(clients = {ItemClient.class}) 或者指定 FeignClient 字节码

public class CartServiceApplication {
    public static void main(String[] args) {
        log.info("-------------------------------------> Hello, this is cart service.");
        SpringApplication.run(CartServiceApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}