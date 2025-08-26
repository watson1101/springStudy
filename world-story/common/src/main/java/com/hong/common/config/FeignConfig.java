package com.hong.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置类
 */
@Configuration
@EnableFeignClients(basePackages = "com.hong.common.feign")
public class FeignConfig {

}