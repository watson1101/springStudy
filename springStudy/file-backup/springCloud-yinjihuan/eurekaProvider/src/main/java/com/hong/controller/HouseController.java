package com.hong.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/house")
public class HouseController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, this is from fsh-house, server port is 8081";
    }
}
