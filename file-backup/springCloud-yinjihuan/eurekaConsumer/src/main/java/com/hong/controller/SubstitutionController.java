package com.hong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 为 eureka 注册中心配置 security 认证后，服务提供方可以正常注册，但服务消费者无法正常启动项目，原因待查。
 */
@RestController
@RequestMapping("/substitution")
public class SubstitutionController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("callHello")
    public String callHello() {
        return restTemplate.getForObject("http://fsh-house/house/hello", String.class);
    }
}
