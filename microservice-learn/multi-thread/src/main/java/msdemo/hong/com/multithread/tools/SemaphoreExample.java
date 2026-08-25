package msdemo.hong.com.multithread.tools;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore 示例 — 信号量
 *
 * <p>{@link Semaphore} 用于控制同时访问特定资源的线程数量，
 * 通过许可证（Permit）的获取和释放来管理并发访问。</p>
 *
 * <h3>核心方法</h3>
 * <ul>
 *   <li>{@link Semaphore#acquire()} — 获取一个许可证（如果没有则阻塞）</li>
 *   <li>{@link Semaphore#release()} — 释放一个许可证</li>
 *   <li>{@link Semaphore#tryAcquire()} — 尝试获取，获取不到立即返回 false</li>
 *   <li>{@link Semaphore#availablePermits()} — 当前可用许可证数</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 *   <li>数据库连接池限流</li>
 *   <li>接口限流（如秒杀系统）</li>
 *   <li>有限资源的访问控制</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class SemaphoreExample {

    /** 最大并发数（模拟停车场有3个车位） */
    private static final int MAX_PERMITS = 3;

    /**
     * 运行演示
     */
    public static void demo() throws Exception {
        log.info("═══════ Semaphore 示例 ═══════");

        // ====== 场景：模拟停车场车位管理 ======
        // 创建信号量，初始有3个许可证（3个空车位）
        Semaphore parkingLot = new Semaphore(MAX_PERMITS, true);  // fair=true 公平模式

        log.info("  停车场共有 {} 个车位", MAX_PERMITS);

        // 10辆车尝试进入停车场
        for (int i = 1; i <= 10; i++) {
            int carId = i;
            new Thread(() -> {
                try {
                    log.info("  车辆{} 到达停车场，等待车位...（当前可用车位: {}）",
                            carId, parkingLot.availablePermits());

                    // 获取许可证（进入停车场）
                    parkingLot.acquire();

                    log.info("  车辆{} 进入停车场 ✅ 剩余车位: {}",
                            carId, parkingLot.availablePermits());

                    // 停车时间（2~5秒）
                    int parkTime = 2000 + (int)(Math.random() * 3000);
                    TimeUnit.MILLISECONDS.sleep(parkTime);

                    log.info("  车辆{} 离开停车场（停了{:.1f}秒）", carId, parkTime/1000.0);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 释放许可证（离开停车场）
                    // 必须在 finally 中释放，确保即使异常也能归还
                    parkingLot.release();
                }
            }, "车辆-" + i).start();

            // 车辆到达间隔
            TimeUnit.MILLISECONDS.sleep(300);
        }

        // 等待所有车辆操作完成
        TimeUnit.SECONDS.sleep(8);

        log.info("  最终可用车位: {}", parkingLot.availablePermits());
        log.info("═══════ Semaphore 示例结束 ═══════\n");
    }
}