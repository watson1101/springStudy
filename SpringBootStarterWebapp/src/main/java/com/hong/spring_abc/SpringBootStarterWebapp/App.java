package com.hong.spring_abc.SpringBootStarterWebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//此注解指定这是一个SpringBoot的应用程序，
//不加就会报异常 Unable to start ServletWebServerApplicationContext due to missing ServletWebServerFactory bean
@SpringBootApplication
public class App {

	public static void main(String[] args) {
	    //SpringApplication用于从main方法中启动Spring应用的类
		SpringApplication.run(App.class, args);
	}

}
