package msdemo.hong.com.multithread.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 锁示例
 *
 * <p>{@link ReentrantLock} 是 JUC 包提供的可重入锁，相比 synchronized 提供更多功能：</p>
 * <ul>
 *   <li><b>可中断</b> — {@link Lock#lockInterruptibly()} 可响应中断</li>
 *   <li><b>可超时</b> — {@link Lock#tryLock(long, TimeUnit)} 超时放弃</li>
 *   <li><b>公平锁</b> — 构造时可选 fair=true，按线程等待顺序获取锁</li>
 *   <li><b>多个条件变量</b> — {@link java.util.concurrent.locks.Condition}</li>
 *   <li><b>可查询状态</b> — getHoldCount()、isHeldByCurrentThread()、getQueueLength()</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class LockExample {

    /** 可重入锁（公平模式） */
    private final Lock fairLock = new ReentrantLock(true);

    /** 可重入锁（非公平模式，默认） */
    private final Lock unfairLock = new ReentrantLock(false);

    /** 共享账户余额 */
    private int balance = 1000;

    /**
     * 运行演示
     */
    public static void demo() throws Exception {
        log.info("═══════ ReentrantLock 示例 ═══════");

        LockExample demo = new LockExample();

        // ====== 1. 基本用法 ======
        demo.basicLock();

        // ====== 2. tryLock 超时 ======
        demo.tryLockWithTimeout();

        // ====== 3. 解决死锁 ======
        demo.solveDeadlock();

        log.info("═══════ ReentrantLock 示例结束 ═══════\n");
    }

    /**
     * 基本用法：lock/unlock
     */
    private void basicLock() throws Exception {
        log.info("  --- 基本用法 ---");

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                // 加锁（必须手动加锁）
                unfairLock.lock();
                try {
                    // 模拟账户操作
                    int current = balance;
                    TimeUnit.MILLISECONDS.sleep(50);  // 模拟耗时操作
                    balance = current - 100;
                    log.info("  扣款100元，余额: {}", balance);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 在 finally 块中释放锁，确保即使发生异常也能释放
                    unfairLock.unlock();
                }
            }, "取款线程-" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }
        log.info("  最终余额: {}", balance);
    }

    /**
     * tryLock 超时尝试（避免死锁的关键手段）
     */
    private void tryLockWithTimeout() throws Exception {
        log.info("  --- tryLock 超时尝试 ---");

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();

        // 线程1：先尝试获取 lock1，再尝试获取 lock2
        Thread t1 = new Thread(() -> {
            try {
                log.info("  线程1 尝试获取 lock1...");
                lock1.lock();
                log.info("  线程1 获取到 lock1");
                TimeUnit.MILLISECONDS.sleep(100);

                log.info("  线程1 尝试获取 lock2（最多等500ms）...");
                // tryLock 在指定时间内获取不到就放弃，不会一直阻塞
                if (lock2.tryLock(500, TimeUnit.MILLISECONDS)) {
                    try {
                        log.info("  线程1 获取到 lock2，执行任务");
                    } finally {
                        lock2.unlock();
                    }
                } else {
                    log.warn("  线程1 获取 lock2 超时，放弃操作");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock1.unlock();
            }
        }, "tryLock-1");

        Thread t2 = new Thread(() -> {
            try {
                log.info("  线程2 尝试获取 lock2...");
                lock2.lock();
                log.info("  线程2 获取到 lock2");
                TimeUnit.MILLISECONDS.sleep(100);

                log.info("  线程2 尝试获取 lock1（最多等500ms）...");
                if (lock1.tryLock(500, TimeUnit.MILLISECONDS)) {
                    try {
                        log.info("  线程2 获取到 lock1，执行任务");
                    } finally {
                        lock1.unlock();
                    }
                } else {
                    log.warn("  线程2 获取 lock1 超时，放弃操作（避免了死锁）");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock2.unlock();
            }
        }, "tryLock-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    /**
     * 使用 tryLock 解决死锁问题
     */
    private void solveDeadlock() throws Exception {
        log.info("  --- 使用 tryLock 解决死锁 ---");
        // 与 SynchronizedExample 中的死锁场景对比
        // 使用 tryLock 后，一个线程会在超时后放弃，死锁被打破
        log.info("  通过 tryLock 超时机制，成功避免了死锁。");
    }
}