package com.ms.learn.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 商品服务启动类（示例：连接 PostgreSQL + Sentinel 熔断限流）
 */
@SpringBootApplication
@MapperScan("com.ms.learn.product.mapper")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
