package com.hong.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoCh522Controller {
// 获取配置文件中配置的方法1
	@Value("${book.author}")
	private String author;
	@Value("${book.name}")
	private String bookName;
	@Value("${book.price}")
	private double price;

	@RequestMapping("/")
	public String index() {
		return "Hello Springboot.";
	}

	// 处理动态 URI http://localhost:8088/book/001
	@RequestMapping("/book/{no}")
	public String findBook(@PathVariable("no") String no) {
		return "处理动态 URI: param = " + no + "---book author = " + author + " book name = " + bookName + "book price = "
				+ price;
	}

	// http://localhost:8088/book?no=002 值不为002则不会执行这个方法
	@RequestMapping(value = "/book", params = { "no=002" })
	public String findBook2(String no) {
		return "/book?no=002: findBook2.param = " + no + "---book author = " + author + " book name = " + bookName
				+ "book price = " + price;
	}

	// http://localhost:8088/book?no=003
	// 可不受限制动态接受URI中问号后的传参，但值为002时，因为findBook2在前面，将执行findBook2
	@RequestMapping("/book")
	public String findBook3(@RequestParam(value = "no") String no) {
		return "/book?no=" + no + ": findBook3.param = " + no + "---book author = " + author + " book name = "
				+ bookName + "book price = " + price;
	}

}
