package com.hong.spring_abc.SpringBootStarterWebapp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//RestController相当于SpringMVC中的 @Controller + @ResponseBody
@RestController
public class HelloController {
@RequestMapping("/hello")
	public String hello(){
		System.out.println("hello method run.");
		return "Hello Spring Boot.";
	}
}
