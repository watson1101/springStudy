package msdemo.hong.com.multithread.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池示例
 *
 * <p>线程池的核心实现类是 {@link ThreadPoolExecutor}，构造参数决定了线程池的行为：</p>
 * <pre>
 * ThreadPoolExecutor(
 *     int corePoolSize,       // 核心线程数
 *     int maximumPoolSize,    // 最大线程数
 *     long keepAliveTime,     // 非核心线程空闲存活时间
 *     TimeUnit unit,          // 存活时间单位
 *     BlockingQueue&lt;Runnable&gt; workQueue,  // 任务队列
 *     ThreadFactory threadFactory,        // 线程工厂
 *     RejectedExecutionHandler handler    // 拒绝策略
 * )
 * </pre>
 *
 * <h3>线程池工作流程</h3>
 * <ol>
 *   <li>提交任务时，如果核心线程未满 → 创建核心线程执行</li>
 *   <li>核心线程已满，任务加入工作队列等待</li>
 *   <li>工作队列也满 → 创建非核心线程（直到 maximumPoolSize）</li>
 *   <li>达到最大线程数 + 队列满 → 执行拒绝策略</li>
 * </ol>
 *
 * <h3>拒绝策略（4种）</h3>
 * <ul>
 *   <li>{@link ThreadPoolExecutor.AbortPolicy} — 抛异常（默认）</li>
 *   <li>{@link ThreadPoolExecutor.CallerRunsPolicy} — 提交任务的线程自己执行</li>
 *   <li>{@link ThreadPoolExecutor.DiscardPolicy} — 直接丢弃</li>
 *   <li>{@link ThreadPoolExecutor.DiscardOldestPolicy} — 丢弃队列中最旧的任务</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ThreadPoolExample {

    /**
     * 运行线程池演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 线程池演示 ═══════");

        // ====== 1. 自定义线程池 ======
        demoCustomThreadPool();

        // ====== 2. Executors 工厂方法 ======
        demoExecutors();

        // ====== 3. 定时任务线程池 ======
        demonScheduledThreadPool();

        log.info("═══════ 线程池演示结束 ═══════\n");
    }

    /**
     * 演示自定义线程池（推荐方式）
     *
     * <p>使用 {@link ThreadPoolExecutor} 直接构造，明确参数含义。</p>
     */
    private static void demoCustomThreadPool() throws Exception {
        log.info("  --- 自定义线程池 ---");

        // 创建自定义线程工厂（给线程命名、设为守护线程等）
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("业务线程-" + counter.getAndIncrement());
                thread.setDaemon(false);          // 设为用户线程
                thread.setPriority(Thread.NORM_PRIORITY);
                return thread;
            }
        };

        // 创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                                // corePoolSize: 核心线程数
                4,                                // maximumPoolSize: 最大线程数
                60, TimeUnit.SECONDS,             // keepAliveTime: 空闲存活时间
                new ArrayBlockingQueue<>(10),      // workQueue: 有界队列
                threadFactory,                     // threadFactory: 线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy()  // handler: 拒绝策略
        );

        // 预启动所有核心线程（可选，默认懒加载）
        executor.prestartAllCoreThreads();

        log.info("  线程池已创建: core={}, max={}, queue={}",
                2, 4, 10);

        // 提交15个任务（演示线程池的工作流程）
        for (int i = 1; i <= 15; i++) {
            int taskId = i;
            executor.execute(() -> {
                log.info("  执行任务 #{}: 线程={}, 池中线程数={}, 活跃数={}, 队列大小={}",
                        taskId,
                        Thread.currentThread().getName(),
                        executor.getPoolSize(),
                        executor.getActiveCount(),
                        executor.getQueue().size());
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 优雅关闭线程池
        executor.shutdown();                       // 不再接收新任务，等待已有任务完成
        boolean terminated = executor.awaitTermination(10, TimeUnit.SECONDS);
        log.info("  线程池关闭: terminated={}", terminated);
    }

    /**
     * 演示 {@link Executors} 工厂方法（了解即可，不推荐正式使用）
     *
     * <p>阿里巴巴 Java 开发规范明确禁止使用 Executors 创建线程池：
     * <ul>
     *   <li>newFixedThreadPool / newSingleThreadExecutor → 使用无界队列 {@link LinkedBlockingQueue}，可能 OOM</li>
     *   <li>newCachedThreadPool → 最大线程数 Integer.MAX_VALUE，可能创建过多线程</li>
     *   <li>newScheduledThreadPool → 同上</li>
     * </ul>
     */
    private static void demoExecutors() throws Exception {
        log.info("  --- Executors 工厂方法 ---");

        // 1. 固定大小线程池（FixedThreadPool）
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        log.info("  FixedThreadPool: 固定3个线程");

        // 2. 单线程线程池（SingleThreadExecutor）
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        log.info("  SingleThreadExecutor: 单线程，保证任务顺序执行");

        // 3. 缓存线程池（CachedThreadPool）
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        log.info("  CachedThreadPool: 按需创建，空闲60秒回收");

        // 使用 fixedPool 执行几个任务
        for (int i = 1; i <= 3; i++) {
            int taskId = i;
            fixedPool.execute(() -> {
                log.info("  FixedPool 任务 #{} 由 {} 执行", taskId, Thread.currentThread().getName());
            });
        }

        // 关闭所有线程池
        List<ExecutorService> pools = Arrays.asList(fixedPool, singlePool, cachedPool);
        for (ExecutorService pool : pools) {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * 演示定时任务线程池
     *
     * <p>{@link ScheduledThreadPoolExecutor} 支持延迟执行和周期执行。</p>
     */
    private static void demonScheduledThreadPool() throws Exception {
        log.info("  --- 定时任务线程池 ---");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // 1. 延迟执行：3秒后执行一次
        log.info("  调度延迟任务：3秒后执行");
        scheduler.schedule(() -> {
            log.info("  ⏰ 延迟任务执行: {}", System.currentTimeMillis());
        }, 3, TimeUnit.SECONDS);

        // 2. 固定频率执行：初始延迟1秒，每隔2秒执行一次
        log.info("  调度周期任务：延迟1秒，每隔2秒执行");
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            log.info("  🔄 周期任务执行: {}", System.currentTimeMillis());
        }, 1, 2, TimeUnit.SECONDS);

        // 运行8秒后取消周期任务
        TimeUnit.MILLISECONDS.sleep(8500);
        future.cancel(false);
        log.info("  周期任务已取消");

        scheduler.shutdown();
        scheduler.awaitTermination(5, TimeUnit.SECONDS);
        log.info("  定时任务线程池已关闭");
    }
}