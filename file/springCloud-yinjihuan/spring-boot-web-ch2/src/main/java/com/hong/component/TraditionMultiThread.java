package com.hong.component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class TraditionMultiThread {
	int i = 0;
	public void testTraditionMultiThread() {
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		executorService.execute(() -> {
			try {
				i++;
				System.out.println(Thread.currentThread().getName() + "    i = " + i);
			} catch (Exception e) {
			} finally {
			}
		});
	}
}
