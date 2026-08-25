package msdemo.hong.com.multithread.basics;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 线程创建方式示例
 *
 * <p>Java 中创建线程的四种常见方式：</p>
 * <ol>
 *   <li><b>继承 {@link Thread}</b> — 重写 run() 方法，简单直接，但受单继承限制</li>
 *   <li><b>实现 {@link Runnable}</b> — 函数式接口，无返回值，可配合线程池</li>
 *   <li><b>实现 {@link Callable}</b> — 有返回值，可抛出异常，配合 {@link FutureTask} 获取结果</li>
 *   <li><b>使用 {@link java.util.concurrent.Executors}</b> — 工具类创建线程（不推荐直接使用）</li>
 * </ol>
 *
 * <p>推荐方式：优先使用 {@link Runnable} 或 {@link Callable} + 线程池，
 * 将任务定义与线程管理解耦。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class ThreadCreateExample {

    /**
     * 运行所有线程创建方式的演示
     */
    public static void demo() throws Exception {
        log.info("═══════ 线程创建方式演示 ═══════");

        // ====== 方式一：继承 Thread 类 ======
        log.info("【方式一】继承 Thread 类创建线程:");
        MyThread myThread = new MyThread("Thread-继承");
        myThread.start();                          // 启动线程
        myThread.join();                           // 等待线程执行完毕

        // ====== 方式二：实现 Runnable 接口 ======
        log.info("【方式二】实现 Runnable 接口创建线程:");
        Thread runnableThread = new Thread(new MyRunnable(), "Thread-Runnable");
        runnableThread.start();                    // 启动线程
        runnableThread.join();                     // 等待线程执行完毕

        // ====== 方式三：Lambda 表达式（Runnable 的简化写法） ======
        log.info("【方式三】Lambda 表达式创建线程:");
        Thread lambdaThread = new Thread(() -> {
            // Lambda 表达式实现 Runnable，代码更简洁
            log.info("  Lambda线程执行中... 当前线程: {}", Thread.currentThread().getName());
        }, "Thread-Lambda");
        lambdaThread.start();
        lambdaThread.join();

        // ====== 方式四：实现 Callable 接口（有返回值） ======
        log.info("【方式四】实现 Callable 接口创建线程（有返回值）:");
        MyCallable myCallable = new MyCallable();
        FutureTask<String> futureTask = new FutureTask<>(myCallable);
        Thread callableThread = new Thread(futureTask, "Thread-Callable");
        callableThread.start();                    // 启动线程

        // 获取 Callable 的执行结果（阻塞等待直到线程执行完毕）
        String result = futureTask.get();
        log.info("  Callable线程执行结果: {}", result);

        log.info("═══════ 线程创建方式演示结束 ═══════\n");
    }

    /**
     * 方式一：继承 Thread 类
     *
     * <p>通过继承 {@link Thread} 并重写 {@code run()} 方法来定义线程执行逻辑。
     * 优点：可以直接使用 Thread 类的方法（如 getName、setPriority 等）。
     * 缺点：Java 不支持多继承，如果已经继承了其他类就不能再继承 Thread。</p>
     */
    private static class MyThread extends Thread {

        /**
         * 构造器，指定线程名称
         *
         * @param name 线程名称
         */
        public MyThread(String name) {
            super(name);  // 调用父类构造器设置线程名
        }

        /**
         * 线程执行体
         *
         * <p>重写 {@code run()} 方法，定义线程要执行的任务逻辑。
         * 调用 {@code start()} 方法后，JVM 会自动调用此方法。</p>
         */
        @Override
        public void run() {
            log.info("  继承Thread方式启动: 线程名={}, 优先级={}",
                    getName(), getPriority());
        }
    }

    /**
     * 方式二：实现 Runnable 接口
     *
     * <p>通过实现 {@link Runnable} 接口，将任务逻辑与线程本身解耦。
     * 优点：不影响继承其他类，可以共享同一个 Runnable 实例给多个线程。</p>
     */
    private static class MyRunnable implements Runnable {

        /**
         * 线程执行体
         *
         * <p>实现 {@code run()} 方法，定义线程要执行的任务逻辑。</p>
         */
        @Override
        public void run() {
            log.info("  实现Runnable方式启动: 线程名={}",
                    Thread.currentThread().getName());
        }
    }

    /**
     * 方式四：实现 Callable 接口（有返回值）
     *
     * <p>{@link Callable} 与 {@link Runnable} 的区别：</p>
     * <ul>
     *   <li>Callable 有返回值，Runnable 没有</li>
     *   <li>Callable 可以抛出受检异常，Runnable 不能</li>
     *   <li>Callable 配合 {@link FutureTask} 可以获取异步执行结果</li>
     * </ul>
     */
    private static class MyCallable implements Callable<String> {

        /**
         * 线程执行体，有返回值
         *
         * @return 线程执行结果字符串
         */
        @Override
        public String call() {
            log.info("  Callable线程执行中... 线程名: {}",
                    Thread.currentThread().getName());
            // 模拟一些计算
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Callable执行完毕，返回结果";
        }
    }
}