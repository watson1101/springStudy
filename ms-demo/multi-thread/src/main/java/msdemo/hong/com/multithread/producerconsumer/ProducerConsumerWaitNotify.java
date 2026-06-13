package msdemo.hong.com.multithread.producerconsumer;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * 生产者-消费者模型 — 使用 {@code wait() / notify()} 实现
 *
 * <p>这是最经典的生产者-消费者实现方式，基于 Java 内置的等待通知机制：</p>
 * <ul>
 *   <li>{@link Object#wait()} — 使当前线程进入等待状态，并释放对象的锁</li>
 *   <li>{@link Object#notify()} / {@link Object#notifyAll()} — 唤醒在该对象上等待的线程</li>
 * </ul>
 *
 * <h3>核心要点</h3>
 * <ul>
 *   <li>wait() 和 notify() 必须在 {@code synchronized} 代码块或方法中调用</li>
 *   <li>wait() 会释放锁，notify() 不会立即释放锁（要到 synchronized 块结束才释放）</li>
 *   <li>判断条件必须使用 {@code while} 循环而非 {@code if}，防止<b>虚假唤醒（spurious wakeup）</b></li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ProducerConsumerWaitNotify {

    /** 缓冲区最大容量 */
    private static final int CAPACITY = 5;

    /** 共享缓冲区，使用 LinkedList 作为 FIFO 队列 */
    private final Queue<Integer> buffer = new LinkedList<>();

    /** 对象锁，用于同步 wait/notify */
    private final Object lock = new Object();

    /** 计数器，用于生成生产的数据 */
    private int counter = 0;

    /**
     * 运行生产-消费演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 生产者-消费者模型（wait/notify） ═══════");
        ProducerConsumerWaitNotify demo = new ProducerConsumerWaitNotify();
        demo.start();
    }

    /**
     * 启动生产者和消费者线程
     */
    private void start() throws Exception {
        // 创建生产者和消费者线程
        Thread producer = new Thread(this::produce, "生产者");
        Thread consumer = new Thread(this::consume, "消费者");

        producer.start();
        consumer.start();

        // 运行5秒后停止
        TimeUnit.SECONDS.sleep(5);

        // 中断线程（通过 interrupt 发出停止信号）
        producer.interrupt();
        consumer.interrupt();

        producer.join();
        consumer.join();
        log.info("  生产-消费演示结束");
        log.info("═══════ 生产-消费演示结束 ═══════\n");
    }

    /**
     * 生产者方法
     *
     * <p>持续生产数据放入缓冲区，当缓冲区满时等待消费者消费。</p>
     */
    private void produce() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (lock) {
                    // 必须用 while 而不是 if，防止虚假唤醒
                    while (buffer.size() >= CAPACITY) {
                        log.info("  缓冲区已满，生产者等待... (size={})", buffer.size());
                        // 缓冲区满，生产者进入等待状态（同时释放锁）
                        lock.wait();
                    }

                    // 生产一个数据
                    int value = ++counter;
                    buffer.offer(value);  // offer() 向队列尾部添加元素
                    log.info("  生产者生产: {}, 缓冲区大小: {}", value, buffer.size());

                    // 唤醒消费者（通知它可以消费了）
                    // notifyAll() 唤醒所有等待线程，更安全
                    lock.notifyAll();
                }

                // 模拟生产耗时
                TimeUnit.MILLISECONDS.sleep(300 + (long)(Math.random() * 200));
            } catch (InterruptedException e) {
                log.info("  生产者收到中断信号，退出");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 消费者方法
     *
     * <p>持续从缓冲区消费数据，当缓冲区空时等待生产者生产。</p>
     */
    private void consume() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (lock) {
                    // 必须用 while 而不是 if
                    while (buffer.isEmpty()) {
                        log.info("  缓冲区已空，消费者等待... (size={})", buffer.size());
                        // 缓冲区空，消费者进入等待状态（同时释放锁）
                        lock.wait();
                    }

                    // 消费一个数据
                    Integer value = buffer.poll();  // poll() 从队列头部取出元素
                    log.info("  消费者消费: {}, 缓冲区大小: {}", value, buffer.size());

                    // 唤醒生产者（通知它可以生产了）
                    lock.notifyAll();
                }

                // 模拟消费耗时（消费者比生产者稍快，体现等待效果）
                TimeUnit.MILLISECONDS.sleep(200 + (long)(Math.random() * 150));
            } catch (InterruptedException e) {
                log.info("  消费者收到中断信号，退出");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}