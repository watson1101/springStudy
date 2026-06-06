package com.hong.controller;

import com.hong.config.MyValueConfig;
import com.hong.service.HelloService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@Autowired
	private HelloService helloService;

	// 该注解等价于 : RequestMapping(RequestMethod.GET)
	@GetMapping("/hello")
	public String hello() {
		return "hello.";
	}

	/**
	 * 获取配置文件中的参数，方法1： @Autowired Environment，getProperty( propertyName)
	 */
	@Autowired
	private Environment env;

	@GetMapping("/name1")
	public String name1() {
		String name1 = env.getProperty("demo.value.name");
		return name1;
	}

	/**
	 * 获取配置文件中的参数，方法2：@Value "${propertyName}" <br/>
	 */
	@Value("${demo.value.name}")
	private String name2;

	@GetMapping("/name2")
	public String name2() {
		return name2;
	}

	/**
	 * 获取配置文件中的参数，方法3：参见 MyValueConfig.java
	 */
	// step4.Autowired 配置类，get 变量
	@Autowired
	private MyValueConfig config;

	@GetMapping("/name3")
	public String name3() {
		return config.getName();
	}
	@GetMapping("/testasync")
	public String testAsync() {
		helloService.testAsync();
		return "success";
	}
}
