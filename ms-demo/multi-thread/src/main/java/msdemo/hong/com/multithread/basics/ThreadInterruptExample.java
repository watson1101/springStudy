package msdemo.hong.com.multithread.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 线程中断示例
 *
 * <p>Java 不提供强制停止线程的机制（{@link Thread#stop()} 已废弃），
 * 而是使用<b>协作式中断</b>的方式：</p>
 * <ul>
 *   <li>调用 {@link Thread#interrupt()} — 设置线程的中断标志位</li>
 *   <li>线程内部通过 {@link Thread#isInterrupted()} 检查中断标志</li>
 *   <li>当线程处于 sleep/wait/join 等阻塞状态时收到中断，会抛出 {@link InterruptedException}</li>
 *   <li>捕获 InterruptedException 后，中断标志位会被清除，通常需要<b>恢复中断状态</b></li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ThreadInterruptExample {

    /**
     * 运行线程中断演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 线程中断演示 ═══════");

        // 创建线程：模拟耗时计算任务
        Thread worker = new Thread(() -> {
            log.info("  工作线程开始执行耗时任务...");

            // 循环中检查中断标志，优雅地响应中断
            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                // 模拟一些计算工作
                if (count % 1000000 == 0) {
                    log.info("  处理了 {} 条数据...", count);
                }

                // 模拟遇到需要等待的操作（如 IO 等待）
                if (count == 3000000) {
                    try {
                        // 当线程在此处 sleep 时被中断，会抛出 InterruptedException
                        log.info("  工作线程进入 sleep 等待...");
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        // 捕获异常后，中断标志位被清除
                        // 正确做法：恢复中断状态
                        Thread.currentThread().interrupt();
                        log.info("  工作线程在 sleep 中被中断，已恢复中断标志");
                    }
                }
            }
            log.info("  工作线程响应中断退出，共处理 {} 条数据", count);
        }, "工作线程");

        worker.start();

        // 主线程等待一段时间后发送中断信号
        TimeUnit.MILLISECONDS.sleep(100);
        log.info("  主线程发送中断信号...");
        worker.interrupt();  // 设置中断标志位

        // 等待工作线程结束
        worker.join();
        log.info("  工作线程已安全退出");

        log.info("═══════ 线程中断演示结束 ═══════\n");
    }
}