package msdemo.hong.com.multithread.synchronization;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * volatile 关键字示例
 *
 * <p>{@code volatile} 是 Java 提供的最轻量级的同步机制，保证了：</p>
 * <ul>
 *   <li><b>可见性（Visibility）</b> — 线程修改 volatile 变量后，其他线程立即可见</li>
 *   <li><b>有序性（Ordering）</b> — 禁止指令重排序（通过内存屏障实现）</li>
 * </ul>
 *
 * <p>但 volatile <b>不保证原子性</b>，复合操作（如 count++）仍需要同步。
 * 适用于：状态标志位、单例模式的双重检查等场景。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class VolatileExample {

    /**
     * 运行 volatile 演示
     */
    public static void demo() throws Exception {
        log.info("═══════ volatile 关键字示例 ═══════");

        // ====== 演示 volatile 保证可见性 ======
        demonVisibility();

        log.info("═══════ volatile 示例结束 ═══════\n");
    }

    /**
     * 演示 volatile 保证可见性
     *
     * <p>如果不加 volatile，工作线程可能永远看不到主线程对 flag 的修改，
     * 导致无限循环。</p>
     */
    private static void demonVisibility() throws Exception {
        log.info("  --- 可见性演示 ---");

        Flag flag = new Flag();

        // 子线程：等待 flag 变为 true
        Thread worker = new Thread(() -> {
            log.info("  工作线程启动，等待 flag 变为 true...");
            int count = 0;
            while (!flag.isRunning()) {
                // 如果 flag 没有 volatile 修饰，JVM 可能会优化此循环
                // 工作线程可能一直在自己的 CPU 缓存中读取 flag 的值
                count++;
            }
            log.info("  工作线程检测到 flag=true，退出循环（循环了 {} 次）", count);
        }, "工作线程");

        worker.start();

        // 主线程：1秒后将 flag 设为 true
        TimeUnit.SECONDS.sleep(1);
        log.info("  主线程设置 flag = true");
        flag.setRunning(true);  // 如果没有 volatile，子线程可能永远看不到此修改

        worker.join();
        log.info("  工作线程已安全退出");
    }

    /**
     * 标志位类
     *
     * <p>{@code running} 字段使用 volatile 修饰，确保一个线程修改后，
     * 其他线程立即看到最新值。</p>
     */
    private static class Flag {
        /**
         * 运行标志
         *
         * <p>使用 volatile 关键字保证：</p>
         * <ol>
         *   <li>对 running 的写入操作会立即刷新到主内存</li>
         *   <li>对 running 的读取操作会从主内存读取，而不是 CPU 缓存</li>
         * </ol>
         */
        private volatile boolean running = false;

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}