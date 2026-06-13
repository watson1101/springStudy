package msdemo.hong.com.multithread.producerconsumer;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者-消费者模型 — 使用 {@link Lock} + {@link Condition} 实现
 *
 * <p>{@link java.util.concurrent.locks.ReentrantLock} 配合 {@link Condition}
 * 是 {@code wait() / notify()} 的增强替代方案：</p>
 *
 * <h3>优势对比</h3>
 * <table border="1">
 *   <tr><th>对比项</th><th>synchronized + wait/notify</th><th>Lock + Condition</th></tr>
 *   <tr><td>等待/唤醒</td><td>Object.wait() / notify()</td><td>Condition.await() / signal()</td></tr>
 *   <tr><td>条件变量</td><td>一个锁只有一个等待集</td><td>一个锁可以创建多个 Condition</td></tr>
 *   <tr><td>超时等待</td><td>wait(timeout)</td><td>await(timeout, unit)</td></tr>
 *   <tr><td>公平性</td><td>非公平</td><td>可设置公平/非公平</td></tr>
 *   <tr><td>可中断性</td><td>不可中断</td><td>lockInterruptibly() 可响应中断</td></tr>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ProducerConsumerLockCondition {

    /** 缓冲区最大容量 */
    private static final int CAPACITY = 5;

    /** 共享缓冲区 */
    private final Queue<Integer> buffer = new LinkedList<>();

    /** 可重入锁 */
    private final Lock lock = new ReentrantLock(true);  // true = 公平锁

    /**
     * 不满条件（生产者等待的条件）
     *
     * <p>当缓冲区已满时，生产者在此条件上等待。</p>
     */
    private final Condition notFull = lock.newCondition();

    /**
     * 不空条件（消费者等待的条件）
     *
     * <p>当缓冲区为空时，消费者在此条件上等待。</p>
     */
    private final Condition notEmpty = lock.newCondition();

    /** 计数器 */
    private int counter = 0;

    /**
     * 运行生产-消费演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 生产者-消费者模型（Lock + Condition） ═══════");
        ProducerConsumerLockCondition demo = new ProducerConsumerLockCondition();
        demo.start();
        log.info("═══════ Lock+Condition 版演示结束 ═══════\n");
    }

    /**
     * 启动生产者和消费者线程
     */
    private void start() throws Exception {
        Thread producer = new Thread(this::produce, "生产者-LC");
        Thread consumer = new Thread(this::consume, "消费者-LC");

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
     * <p>使用 Lock 和 Condition 实现：
     * 缓冲区满时在 {@code notFull} 条件上等待。</p>
     */
    private void produce() {
        while (!Thread.currentThread().isInterrupted()) {
            // 加锁（必须手动加锁和释放）
            lock.lock();
            try {
                // 缓冲区满，在 notFull 条件上等待
                // wait/notify 时我们用 while，这里同样需要 while 防止虚假唤醒
                while (buffer.size() >= CAPACITY) {
                    log.info("  缓冲区已满，生产者等待... (size={})", buffer.size());
                    // await() 释放锁并等待，与 wait() 类似
                    notFull.await();
                }

                int value = ++counter;
                buffer.offer(value);
                log.info("  生产者生产: {}, 缓冲区大小: {}", value, buffer.size());

                // 唤醒在 notEmpty 条件上等待的消费者
                // signal() 与 notify() 类似，signalAll() 与 notifyAll() 类似
                notEmpty.signalAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                // 释放锁（必须在 finally 块中释放，确保异常时也能释放）
                lock.unlock();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(300 + (long)(Math.random() * 200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 消费者方法
     */
    private void consume() {
        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                // 缓冲区空，在 notEmpty 条件上等待
                while (buffer.isEmpty()) {
                    log.info("  缓冲区已空，消费者等待... (size={})", buffer.size());
                    notEmpty.await();
                }

                Integer value = buffer.poll();
                log.info("  消费者消费: {}, 缓冲区大小: {}", value, buffer.size());

                // 唤醒在 notFull 条件上等待的生产者
                notFull.signalAll();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(200 + (long)(Math.random() * 150));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}