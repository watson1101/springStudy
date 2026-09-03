package com.ms.learn.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 交易服务启动类
 * <p>接收支付请求 -> 模拟2s处理 -> 返回交易成功 -> Feign 调用 service-points 发放积分</p>
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.ms.learn.transaction.mapper")
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
