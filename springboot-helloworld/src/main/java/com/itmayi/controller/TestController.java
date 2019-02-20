package com.itmayi.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Test
 * 
 * @author mc
 *
 */
@RestController
//@EnableAutoConfiguration
public class TestController {
	@RequestMapping("/hello")
	public String hello() {
		return "Hello";
	}

//	public static void main(String[] args) {
//		SpringApplication.run(TestController.class, args);
//	}
}
