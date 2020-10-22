package com.hong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hong.ch623Entity.Ch623Author;

@RestController
@RequestMapping("/ch623")
public class DemoCh623Controller {
	// 另一种获取配置文件的方式，参见 Ch623Author.java
	@Autowired
	private Ch623Author author;

	@RequestMapping("/")
	public String index() {
		return "Hello Springboot.";
	}

	@RequestMapping("/author")
	public String findAuthor() {
		return "findAuthor: author.name=" + author.getName() + " author.age=" + author.getAge();
	}
}
