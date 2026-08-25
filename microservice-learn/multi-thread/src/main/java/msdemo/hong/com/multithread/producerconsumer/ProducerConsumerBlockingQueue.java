package msdemo.hong.com.multithread.producerconsumer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 生产者-消费者模型 — 使用 {@link BlockingQueue} 实现
 *
 * <p>{@link java.util.concurrent.BlockingQueue} 是 JUC 包提供的阻塞队列，
 * 内部已经封装了线程同步逻辑，开发者无需手动管理 wait/notify。</p>
 *
 * <h3>常用实现类</h3>
 * <ul>
 *   <li>{@link java.util.concurrent.ArrayBlockingQueue} — 有界数组队列（本例使用）</li>
 *   <li>{@link java.util.concurrent.LinkedBlockingQueue} — 可选有界链表队列</li>
 *   <li>{@link java.util.concurrent.PriorityBlockingQueue} — 支持优先级排序</li>
 *   <li>{@link java.util.concurrent.SynchronousQueue} — 不存储元素，直接传递</li>
 *   <li>{@link java.util.concurrent.DelayQueue} — 延迟队列</li>
 * </ul>
 *
 * <h3>常用方法</h3>
 * <table border="1">
 *   <tr><th>方法</th><th>说明</th></tr>
 *   <tr><td>{@link BlockingQueue#put(Object)}</td><td>插入元素，队列满时阻塞</td></tr>
 *   <tr><td>{@link BlockingQueue#take()}</td><td>取出元素，队列空时阻塞</td></tr>
 *   <tr><td>{@link BlockingQueue#offer(Object, long, TimeUnit)}</td><td>插入元素，超时返回 false</td></tr>
 *   <tr><td>{@link BlockingQueue#poll(long, TimeUnit)}</td><td>取出元素，超时返回 null</td></tr>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ProducerConsumerBlockingQueue {

    /** 缓冲区最大容量 */
    private static final int CAPACITY = 5;

    /**
     * 阻塞队列
     *
     * <p>{@link ArrayBlockingQueue} 内部使用 {@link java.util.concurrent.locks.ReentrantLock}
     * 和 {@link java.util.concurrent.locks.Condition} 实现线程同步。
     * 当队列满时 put() 阻塞，当队列空时 take() 阻塞。</p>
     */
    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CAPACITY);

    /** 计数器 */
    private int counter = 0;

    /**
     * 运行生产-消费演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 生产者-消费者模型（BlockingQueue） ═══════");
        ProducerConsumerBlockingQueue demo = new ProducerConsumerBlockingQueue();
        demo.start();
        log.info("═══════ BlockingQueue 版演示结束 ═══════\n");
    }

    /**
     * 启动生产者和消费者线程
     */
    private void start() throws Exception {
        Thread producer = new Thread(this::produce, "生产者-BQ");
        Thread consumer = new Thread(this::consume, "消费者-BQ");

        producer.start();
        consumer.start();

        TimeUnit.SECONDS.sleep(5);

        producer.interrupt();
        consumer.interrupt();
        producer.join();
        consumer.join();
    }

    /**
     * 生产者方法
     *
     * <p>使用 BlockingQueue 后，不需要手动同步或等待，
     * put() 方法内部已处理队列满时的等待逻辑。</p>
     */
    private void produce() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int value = ++counter;
                // put() — 队列满时自动阻塞，直到有空间可用
                // 内部使用 Condition.await() 实现等待
                queue.put(value);
                log.info("  生产者生产: {}, 队列大小: {}", value, queue.size());

                TimeUnit.MILLISECONDS.sleep(300 + (long)(Math.random() * 200));
            } catch (InterruptedException e) {
                log.info("  生产者退出");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 消费者方法
     *
     * <p>使用 take() 方法，队列空时自动阻塞等待。</p>
     */
    private void consume() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // take() — 队列空时自动阻塞，直到有新元素可用
                Integer value = queue.take();
                log.info("  消费者消费: {}, 队列大小: {}", value, queue.size());

                TimeUnit.MILLISECONDS.sleep(200 + (long)(Math.random() * 150));
            } catch (InterruptedException e) {
                log.info("  消费者退出");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}