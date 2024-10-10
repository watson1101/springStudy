package com.hong.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncMultiThread {
	@Async
	public void saveLog() {
		System.out.println(Thread.currentThread().getName() + " --- saveLog()");
	}
}
