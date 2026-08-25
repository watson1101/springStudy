package msdemo.hong.com.multithread.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 守护线程示例
 *
 * <p>Java 中的线程分为两类：</p>
 * <ul>
 *   <li><b>用户线程（User Thread）</b> — 普通线程，JVM 会等待所有用户线程结束后才退出</li>
 *   <li><b>守护线程（Daemon Thread）</b> — 后台服务线程，当所有用户线程退出时，
 *       JVM 会强制终止守护线程（即使其任务未完成）</li>
 * </ul>
 *
 * <p>常见守护线程：GC 垃圾回收线程、Finalizer 线程、JMX 监控线程等。</p>
 * <p>使用 {@link Thread#setDaemon(boolean)} 在线程启动前设置守护线程。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ThreadDaemonExample {

    /**
     * 运行守护线程演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 守护线程演示 ═══════");

        // 创建用户线程（默认）
        Thread userThread = new Thread(() -> {
            log.info("  用户线程开始执行...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("  用户线程执行完毕");
        }, "用户线程");

        // 创建守护线程
        Thread daemonThread = new Thread(() -> {
            log.info("  守护线程开始执行...");
            try {
                // 守护线程会尝试运行3秒，但用户线程2秒后结束
                // JVM 在用户线程全部结束后立即退出，不等守护线程
                for (int i = 0; i < 6; i++) {
                    TimeUnit.MILLISECONDS.sleep(500);
                    log.info("  守护线程还在运行... i={}", i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("  守护线程执行完毕（此句可能不会被执行）");
        }, "守护线程");

        // 设置为守护线程（必须在线程 start() 之前设置）
        daemonThread.setDaemon(true);

        // 启动线程
        userThread.start();
        daemonThread.start();

        log.info("  是否守护线程: userThread={}, daemonThread={}",
                userThread.isDaemon(), daemonThread.isDaemon());

        // 等待用户线程结束（JVM 在此时退出，守护线程被强制终止）
        userThread.join();
        log.info("  用户线程已结束，JVM即将退出...");

        log.info("═══════ 守护线程演示结束（注意守护线程可能未完成） ═══════\n");
    }
}