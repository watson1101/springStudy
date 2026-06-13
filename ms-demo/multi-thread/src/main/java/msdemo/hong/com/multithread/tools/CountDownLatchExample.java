package msdemo.hong.com.multithread.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch 示例 — 倒计时门闩
 *
 * <p>{@link CountDownLatch} 是一个同步辅助工具，允许一个或多个线程等待，
 * 直到其他线程完成一系列操作。</p>
 *
 * <h3>核心方法</h3>
 * <ul>
 *   <li>{@link CountDownLatch#CountDownLatch(int)} — 构造函数，指定计数器初始值</li>
 *   <li>{@link CountDownLatch#countDown()} — 计数器减1（由完成任务的线程调用）</li>
 *   <li>{@link CountDownLatch#await()} — 等待计数器变为0</li>
 *   <li>{@link CountDownLatch#await(long, TimeUnit)} — 超时等待</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 *   <li>主线程等待多个子线程完成（如批量数据导入完成后汇总结果）</li>
 *   <li>多个线程同时开始（结合 CyclicBarrier 更好）</li>
 * </ul>
 *
 * <p>注意：CountDownLatch 的计数器<b>只能使用一次</b>，不能重置。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class CountDownLatchExample {

    /** 参与的任务总数 */
    private static final int TASK_COUNT = 5;

    /**
     * 运行演示
     */
    public static void demo() throws Exception {
        log.info("═══════ CountDownLatch 示例 ═══════");

        // ====== 场景：主线程等待所有子线程加载数据完成 ======
        CountDownLatch latch = new CountDownLatch(TASK_COUNT);

        log.info("  主线程：开始并行加载 {} 个数据模块...", TASK_COUNT);

        for (int i = 1; i <= TASK_COUNT; i++) {
            int moduleId = i;
            new Thread(() -> {
                try {
                    log.info("  模块{} 开始加载数据...", moduleId);
                    // 模拟不同模块加载时间不同
                    TimeUnit.MILLISECONDS.sleep(300 + (long)(Math.random() * 500));
                    log.info("  模块{} 数据加载完成 ✓", moduleId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 每个模块完成后计数器减1
                    latch.countDown();
                }
            }, "加载线程-" + i).start();
        }

        // 主线程等待所有模块加载完毕
        log.info("  主线程等待所有模块加载完成...");
        latch.await();  // 阻塞直到计数器为0
        log.info("  所有模块加载完成！主线程继续执行");

        log.info("═══════ CountDownLatch 示例结束 ═══════\n");
    }
}