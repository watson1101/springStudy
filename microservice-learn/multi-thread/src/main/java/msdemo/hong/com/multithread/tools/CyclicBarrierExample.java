package msdemo.hong.com.multithread.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier 示例 — 循环屏障
 *
 * <p>{@link CyclicBarrier} 允许一组线程互相等待，直到所有线程都到达某个公共屏障点，
 * 然后继续执行。</p>
 *
 * <h3>与 CountDownLatch 的区别</h3>
 * <table border="1">
 *   <tr><th>特性</th><th>CountDownLatch</th><th>CyclicBarrier</th></tr>
 *   <tr><td>重用</td><td>不可重用</td><td>可重用（reset()）</td></tr>
 *   <tr><td>角色</td><td>计数的消费者等待生产者</td><td>线程互相等待</td></tr>
 *   <tr><td>计数方式</td><td>countDown() 减计数</td><td>await() 等待线程数到达</td></tr>
 *   <tr><td>触发动作</td><td>无</td><td>可指定 barrierAction（达到屏障后执行）</td></tr>
 * </table>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class CyclicBarrierExample {

    /** 参与等待的线程数 */
    private static final int PARTY_COUNT = 4;

    /**
     * 运行演示
     */
    public static void demo() throws Exception {
        log.info("═══════ CyclicBarrier 示例 ═══════");

        // ====== 场景：4个运动员各自准备，全部准备好后同时起跑 ======
        // 运动员就位后执行的栅栏动作（这个由最后一个到达的线程执行）
        Runnable barrierAction = () -> {
            log.info("  🔫 发令枪！所有运动员已就位！");
            log.info("  （barrierAction 由线程 {} 执行）", Thread.currentThread().getName());
        };

        CyclicBarrier barrier = new CyclicBarrier(PARTY_COUNT, barrierAction);

        log.info("  比赛开始！{} 名运动员准备...", PARTY_COUNT);

        for (int i = 1; i <= PARTY_COUNT; i++) {
            int athleteId = i;
            new Thread(() -> {
                try {
                    // 模拟不同准备时间
                    TimeUnit.MILLISECONDS.sleep(200 * athleteId);
                    log.info("  运动员{} 准备就绪 ✓（等待其他运动员）", athleteId);

                    // 等待其他运动员（到达 barrier 点）
                    barrier.await();

                    // 所有运动员都就位后，同时起跑
                    log.info("  运动员{} 起跑！", athleteId);
                } catch (InterruptedException | BrokenBarrierException e) {
                    log.error("  运动员{} 出现异常: {}", athleteId, e.getMessage());
                }
            }, "运动员-" + i).start();
        }

        // 等待比赛结束
        TimeUnit.SECONDS.sleep(3);
        log.info("  比赛结束");

        // ====== 演示 CyclicBarrier 的可重用性 ======
        log.info("  --- 第二轮比赛开始（CyclicBarrier 可重用） ---");
        // CyclicBarrier 在触发后自动重置，可以直接复用
        // 注意：这里简化演示，实际需要创建新线程或重用已有线程
        log.info("  调用 barrier.reset() 重置...");
        // 注意：如果还有线程在 await() 等待，reset() 会导致 BrokenBarrierException

        log.info("═══════ CyclicBarrier 示例结束 ═══════\n");
    }
}