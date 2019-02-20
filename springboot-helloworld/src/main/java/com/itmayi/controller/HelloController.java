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
public class HelloController {
	@RequestMapping("/hello2")
	public String hello2() {
		return "Hello-2";
	}


}
