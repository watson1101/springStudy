package com.hong.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.hong.user.mapper")
public class MyBatisPlusConfig {
}