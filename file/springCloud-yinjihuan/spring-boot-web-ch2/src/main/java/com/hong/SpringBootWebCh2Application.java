package com.hong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootWebCh2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebCh2Application.class, args);
	}

}
