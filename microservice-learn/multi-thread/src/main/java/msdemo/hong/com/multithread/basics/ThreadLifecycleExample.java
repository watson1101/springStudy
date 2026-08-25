package msdemo.hong.com.multithread.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 线程生命周期示例
 *
 * <p>Java 线程的生命周期包含以下 6 种状态（定义在 {@link Thread.State} 枚举中）：</p>
 * <ol>
 *   <li><b>{@link Thread.State#NEW}</b> — 新建：线程刚被创建，尚未启动</li>
 *   <li><b>{@link Thread.State#RUNNABLE}</b> — 就绪/运行：线程已启动，可能在运行或等待CPU时间片</li>
 *   <li><b>{@link Thread.State#BLOCKED}</b> — 阻塞：等待获取监视器锁（synchronized）</li>
 *   <li><b>{@link Thread.State#WAITING}</b> — 等待：等待其他线程显式唤醒（wait/join/park）</li>
 *   <li><b>{@link Thread.State#TIMED_WAITING}</b> — 超时等待：等待一段时间（sleep/wait(time)/join(time)/parkNanos）</li>
 *   <li><b>{@link Thread.State#TERMINATED}</b> — 终止：线程执行完毕</li>
 * </ol>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ThreadLifecycleExample {

    /**
     * 运行线程生命周期演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 线程生命周期演示 ═══════");

        // ====== 1. NEW 状态 ======
        // 刚创建但未调用 start()，状态为 NEW
        Thread thread = new Thread(() -> {
            // 线程执行的业务逻辑
            log.info("  线程开始执行具体业务...");
            try {
                // TIMED_WAITING：调用 sleep() 进入超时等待
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("  线程业务执行完毕");
        }, "生命周期演示线程");

        // 此时线程刚创建，还未启动
        log.info("  1. 刚创建未启动: state={}", thread.getState());  // NEW

        // ====== 2. RUNNABLE 状态 ======
        thread.start();
        log.info("  2. start()后: state={}", thread.getState());    // RUNNABLE

        // ====== 3. TIMED_WAITING 状态 ======
        // 主线程等待一小段时间，让子线程进入 sleep()
        TimeUnit.MILLISECONDS.sleep(100);
        log.info("  3. 子线程sleep中: state={}", thread.getState()); // TIMED_WAITING

        // ====== 4. 等待线程结束 ======
        thread.join();  // 主线程等待子线程执行完毕
        log.info("  4. 线程执行完毕: state={}", thread.getState());  // TERMINATED

        log.info("═══════ 线程生命周期演示结束 ═══════\n");
    }
}