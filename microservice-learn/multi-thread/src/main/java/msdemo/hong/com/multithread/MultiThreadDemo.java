package msdemo.hong.com.multithread;

import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.multithread.basics.*;
import msdemo.hong.com.multithread.producerconsumer.*;
import msdemo.hong.com.multithread.threadpool.*;
import msdemo.hong.com.multithread.synchronization.*;
import msdemo.hong.com.multithread.tools.*;

/**
 * Java 多线程技术演示入口
 *
 * <p>作为 multi-thread 模块的主运行类，依次运行各个示例的 {@code demo()} 方法。
 * 每个示例都包含详细的注释说明运行原理和关键知识点。</p>
 *
 * <h3>示例覆盖内容</h3>
 * <ol>
 *   <li><b>线程基础</b> — 线程创建、生命周期、守护线程、线程中断</li>
 *   <li><b>生产-消费模型</b> — wait/notify、BlockingQueue、Lock+Condition 三种实现方式</li>
 *   <li><b>线程池</b> — ThreadPoolExecutor 自定义、Executors 工厂、定时任务</li>
 *   <li><b>CompletableFuture</b> — 异步编排、任务组合、异常处理</li>
 *   <li><b>synchronized</b> — 并发问题演示、死锁演示</li>
 *   <li><b>Lock</b> — ReentrantLock、tryLock 超时、避免死锁</li>
 *   <li><b>volatile</b> — 可见性演示</li>
 *   <li><b>CountDownLatch</b> — 主线程等待子线程完成</li>
 *   <li><b>CyclicBarrier</b> — 线程互相等待到达屏障点</li>
 *   <li><b>Semaphore</b> — 信号量控制并发数</li>
 * </ol>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class MultiThreadDemo {

    public static void main(String[] args) throws Exception {
        log.info("");
        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║      Java 多线程技术演示         ║");
        log.info("╚══════════════════════════════════════════════════╝");
        log.info("");

        // ====== 第一部分：线程基础 ======
        log.info("█████ 第一部分：线程基础 █████");
        ThreadCreateExample.demo();            // 线程创建方式
        ThreadLifecycleExample.demo();         // 线程生命周期
        ThreadDaemonExample.demo();            // 守护线程
        ThreadInterruptExample.demo();         // 线程中断

        // ====== 第二部分：生产者-消费者模型 ======
        log.info("█████ 第二部分：生产者-消费者模型 █████");
        ProducerConsumerWaitNotify.demo();     // wait/notify 实现
        ProducerConsumerBlockingQueue.demo();  // BlockingQueue 实现
        ProducerConsumerLockCondition.demo();  // Lock+Condition 实现

        // ====== 第三部分：线程池 ======
        log.info("█████ 第三部分：线程池 █████");
        ThreadPoolExample.demo();              // 线程池
        CompletableFutureExample.demo();       // 异步编排

        // ====== 第四部分：同步 ======
        log.info("█████ 第四部分：同步 █████");
        SynchronizedExample.demo();            // synchronized
        LockExample.demo();                    // ReentrantLock
        VolatileExample.demo();                // volatile

        // ====== 第五部分：并发工具类 ======
        log.info("█████ 第五部分：并发工具类 █████");
        CountDownLatchExample.demo();          // 倒计时门闩
        CyclicBarrierExample.demo();           // 循环屏障
        SemaphoreExample.demo();               // 信号量

        log.info("");
        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║      所有演示已完成！         ║");
        log.info("╚══════════════════════════════════════════════════╝");
    }
}