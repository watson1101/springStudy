package com.hong;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.hong.mapper")
@EnableTransactionManagement
@Slf4j
public class ItemServiceApplication {
    public static void main(String[] args) {

        log.info("Hello world, this is item service.");
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}