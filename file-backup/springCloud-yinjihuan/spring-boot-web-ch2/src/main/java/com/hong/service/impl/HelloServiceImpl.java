package com.hong.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hong.component.AsyncMultiThread;
import com.hong.component.TraditionMultiThread;
import com.hong.service.HelloService;

@Service
public class HelloServiceImpl implements HelloService {
    @Autowired
    private TraditionMultiThread traditionMultiThread;
    @Autowired
    private AsyncMultiThread asyncMultiThread;

	@Override
	public void testAsync() {
		// 传统多线程
		int i=0;
		while(true) {
		traditionMultiThread.testTraditionMultiThread();
		i++;
		if(i==50) {
			break;
		}
		}
		// @Async 多线程
		asyncMultiThread.saveLog();
	}

}
