package msdemo.hong.com.multithread.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * synchronized 关键字示例
 *
 * <p>{@code synchronized} 是 Java 内置的同步机制，基于监视器锁（Monitor Lock）实现。
 * 确保同一时刻只有一个线程访问被保护的代码块。</p>
 *
 * <h3>三种使用方式</h3>
 * <ul>
 *   <li><b>同步方法</b> — 锁住整个方法，锁是当前实例对象（this）或 Class 对象</li>
 *   <li><b>同步代码块</b> — 只锁住需要同步的代码片段，粒度更细，性能更好</li>
 *   <li><b>静态同步方法</b> — 锁是当前类的 Class 对象</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class SynchronizedExample {

    /** 演示用计数器 */
    private int count = 0;

    /** 使用 AtomicInteger 作为对比 */
    private final AtomicInteger atomicCount = new AtomicInteger(0);

    /**
     * 运行演示
     */
    public static void demo() throws Exception {
        log.info("═══════ synchronized 关键字示例 ═══════");

        // ====== 不加锁的并发问题 ======
        demonUnsafeCounter();

        // ====== 加锁的正确方式 ======
        demonSafeCounter();

        // ====== 演示死锁 ======
        demonDeadlock();

        log.info("═══════ synchronized 示例结束 ═══════\n");
    }

    /**
     * 演示不加锁的并发问题
     */
    private static void demonUnsafeCounter() throws Exception {
        log.info("  --- 无锁并发（结果不正确） ---");

        Counter counter = new Counter();
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 10个线程并发执行1000次自增
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.incrementUnsafe();
                }
                latch.countDown();
            }, "线程-" + i).start();
        }

        latch.await();
        // 预期 10000，但实际可能小于 10000（因为并发写入丢失了部分操作）
        log.info("  无锁计数结果: {}（预期10000）", counter.getValue());
    }

    /**
     * 演示加锁的正确方式
     */
    private static void demonSafeCounter() throws Exception {
        log.info("  --- synchronized 加锁（结果正确） ---");

        Counter counter = new Counter();
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.incrementSafe();
                }
                latch.countDown();
            }, "线程-" + i).start();
        }

        latch.await();
        // 结果为 10000，始终正确
        log.info("  加锁计数结果: {}（预期10000）", counter.getSafeValue());
    }

    /**
     * 演示死锁
     */
    private static void demonDeadlock() throws Exception {
        log.info("  --- 死锁演示 ---");

        Object lockA = new Object();
        Object lockB = new Object();

        // 线程1：持有锁A，尝试获取锁B
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                log.info("  线程1 持有 锁A，尝试获取 锁B...");
                try { TimeUnit.MILLISECONDS.sleep(50); } catch (InterruptedException e) {}
                synchronized (lockB) {
                    log.info("  线程1 获取了 锁B");
                }
            }
        }, "死锁线程-1");

        // 线程2：持有锁B，尝试获取锁A
        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                log.info("  线程2 持有 锁B，尝试获取 锁A...");
                try { TimeUnit.MILLISECONDS.sleep(50); } catch (InterruptedException e) {}
                synchronized (lockA) {
                    log.info("  线程2 获取了 锁A");
                }
            }
        }, "死锁线程-2");

        t1.start();
        t2.start();

        // 等2秒看看状态
        TimeUnit.SECONDS.sleep(2);

        // 检查线程状态（此时两个线程应该处于 BLOCKED 状态，互相等待）
        log.info("  线程1 状态: {}", t1.getState());
        log.info("  线程2 状态: {}", t2.getState());

        // 提示：死锁在现实中需要用 jstack 或 VisualVM 检测
        // 解决方案：按固定顺序获取锁，或使用 tryLock 超时机制
        log.info("  ⚠ 死锁发生！两个线程互相持有对方需要的锁。");

        t1.interrupt();
        t2.interrupt();
    }

    /**
     * 计数器内部类
     *
     * <p>对比无锁和加锁两种方式的计数结果。</p>
     */
    private static class Counter {
        private int count = 0;

        /**
         * 非线程安全的递增
         *
         * <p>{@code count++} 实际上包含三步操作：读取 → 修改 → 写入。
         * 多线程环境下可能丢失中间结果。</p>
         */
        public void incrementUnsafe() {
            count++;  // 非原子操作
        }

        /**
         * 线程安全的递增
         *
         * <p>使用 {@code synchronized} 方法确保原子性。
         * 同一时刻只有一个线程能执行此方法。</p>
         */
        public synchronized void incrementSafe() {
            count++;
        }

        public int getValue() { return count; }
        public int getSafeValue() { return count; }
    }
}