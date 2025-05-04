package com.hong.demos;

public class ThreadLocalDemo {
    // 初始化，设置每个线程第一次访问 idx 时将默认值设置为0；
    static final ThreadLocal<Integer> idx = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        // 创建线程1
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 1: " + idx.get());
                idx.set(idx.get() + 1);
            }
        });

        // 创建线程2
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 2: " + idx.get());
                idx.set(idx.get() + 1);
            }
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待线程结束
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
