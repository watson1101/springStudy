package com.ms.learn.points;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 积分服务启动类
 * <p>提供：用户积分账户、发放（交易模块 Feign 调用消费返积分：1 分 = 1 积分）、消耗、流水查询</p>
 */
@SpringBootApplication
@MapperScan("com.ms.learn.points.mapper")
public class PointsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointsServiceApplication.class, args);
    }
}
