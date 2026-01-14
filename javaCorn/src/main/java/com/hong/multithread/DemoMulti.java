package com.hong.multithread;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 三个生产者，5个消费者，队列为空则消费者等待，队列满则生产者等待
 */
public class DemoMulti {
    private static final int MAX_BREAD = 5;
    private static final Queue<String> breadQueue = new LinkedList<>();

    public static void main(String[] args) {
        Thread producer1 = new Thread(new Producer(), "Producer-1");
        Thread producer2 = new Thread(new Producer(), "Producer-2");
        Thread producer3 = new Thread(new Producer(), "Producer-3");
        Thread consumerA = new Thread(new Consumer(), "Consumer-A");
        Thread consumerB = new Thread(new Consumer(), "Consumer-B");
        Thread consumerC = new Thread(new Consumer(), "Consumer-C");
        Thread consumerD = new Thread(new Consumer(), "Consumer-D");
        Thread consumerE = new Thread(new Consumer(), "Consumer-E");

        producer1.start();
        producer2.start();
        producer3.start();
        consumerA.start();
        consumerB.start();
        consumerC.start();
        consumerD.start();
        consumerE.start();

    }

    static class Producer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (breadQueue) {
                    while (breadQueue.size() == MAX_BREAD) {
                        try {
                            breadQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String bread = "Bread-" + System.currentTimeMillis();
                    breadQueue.add(bread);
                    System.out.println(Thread.currentThread().getName() + " produced " + bread + " 当前面包库存：" + breadQueue.size());
                    breadQueue.notifyAll();
                }
                try {
                    Thread.sleep(5000); // Simulate time taken to produce bread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (breadQueue) {
                    while (breadQueue.isEmpty()) {
                        try {
                            breadQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String bread = breadQueue.poll();
                    System.out.println(Thread.currentThread().getName() + " 消费 " + bread + " 当前面包库存：" + breadQueue.size());
                    breadQueue.notifyAll();
                }
                try {
                    Thread.sleep(3000); // Simulate time taken to consume bread
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}