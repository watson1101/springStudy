package com.hong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hong.dao")
public class WordsMemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WordsMemoApplication.class, args);
	}

}
