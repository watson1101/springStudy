# multi-thread Java多线程技术验证模块

## 模块简介
本模块用于验证和学习 Java 多线程相关的核心技术点，包含从基础到进阶的各类多线程示例，每个示例都配有详细的中文注释说明。

## 示例总览

| 分类 | 文件名 | 说明 |
|------|--------|------|
| **线程基础** | `ThreadCreateExample.java` | 线程创建的四种方式（Thread/Runnable/Callable/Lambda） |
| | `ThreadLifecycleExample.java` | 线程六种状态（NEW/RUNNABLE/BLOCKED/WAITING/TIMED_WAITING/TERMINATED） |
| | `ThreadDaemonExample.java` | 守护线程与用户线程的区别 |
| | `ThreadInterruptExample.java` | 线程中断的协作式机制 |
| **生产者-消费者** | `ProducerConsumerWaitNotify.java` | 使用 wait()/notify() 实现（最经典方式） |
| | `ProducerConsumerBlockingQueue.java` | 使用 BlockingQueue 实现（最推荐方式） |
| | `ProducerConsumerLockCondition.java` | 使用 Lock + Condition 实现（最灵活方式） |
| **线程池** | `ThreadPoolExample.java` | ThreadPoolExecutor 自定义、Executors 工厂、定时任务 |
| | `CompletableFutureExample.java` | 异步编排、任务组合（thenCompose/thenCombine/allOf） |
| **同步机制** | `SynchronizedExample.java` | synchronized 关键字、并发问题、死锁演示 |
| | `LockExample.java` | ReentrantLock、tryLock 超时、避免死锁 |
| | `VolatileExample.java` | volatile 可见性、禁止指令重排序 |
| **并发工具** | `CountDownLatchExample.java` | 倒计时门闩（等待多个子线程完成） |
| | `CyclicBarrierExample.java` | 循环屏障（线程互相等待到达屏障点） |
| | `SemaphoreExample.java` | 信号量（控制并发访问数） |

## 生产者-消费者模型详解

本模块实现了三种生产者-消费者模型，各有优劣：

### 1. wait() / notify() 方式
```
生产者 ----[put]----▶ 共享缓冲区 ◀----[take]---- 消费者
                      (有界队列)
```
**原理**：使用 `synchronized` + `Object.wait()/notifyAll()` 实现线程间协作。

**关键点**：
- wait() 和 notify() 必须在 synchronized 块中调用
- wait() 释放锁，notify() 不会立即释放锁
- 条件判断必须使用 `while` 而非 `if`，防止虚假唤醒（spurious wakeup）
- notifyAll() 比 notify() 更安全

### 2. BlockingQueue 方式
```java
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
queue.put(value);    // 队列满时自动阻塞
queue.take();        // 队列空时自动阻塞
```
**原理**：JUC 包提供的阻塞队列，内部封装了线程同步逻辑。

**优势**：
- 代码最简洁，无需手动管理锁和条件
- 内部使用 `ReentrantLock` + `Condition` 实现，性能优异
- 生产环境推荐使用

### 3. Lock + Condition 方式
```java
Lock lock = new ReentrantLock(true);
Condition notFull = lock.newCondition();
Condition notEmpty = lock.newCondition();
```
**原理**：使用 `Lock` 替代 `synchronized`，`Condition` 替代 `wait/notify`。

**优势**：
- 支持多个条件变量（notFull / notEmpty）
- 支持公平锁
- tryLock() 支持超时
- lockInterruptibly() 可响应中断

## 运行方式

### 编译运行
```bash
# 编译整个项目（在 ms-demo 根目录）
mvn clean compile -pl multi-thread -am

# 运行主类
mvn exec:java -pl multi-thread -Dexec.mainClass="msdemo.hong.com.multithread.MultiThreadDemo"

# 或直接使用 java 命令
java -cp multi-thread/target/classes msdemo.hong.com.multithread.MultiThreadDemo
```

### 运行效果
运行后控制台会按顺序输出每个示例的执行过程和结果，例如生产者-消费者模型的日志：
```
生产者生产: 1, 缓冲区大小: 1
消费者消费: 1, 缓冲区大小: 0
生产者生产: 2, 缓冲区大小: 1
...
缓冲区已满，生产者等待...
消费者消费: ..., 缓冲区大小: 4
```

## 技术栈
- **JDK**: 21
- **依赖**: Lombok、Hutool、FastJSON
- **构建工具**: Maven

## 包结构
```
msdemo.hong.com.multithread
├── MultiThreadDemo.java                    // 主运行类
├── basics/
│   ├── ThreadCreateExample.java            // 线程创建
│   ├── ThreadLifecycleExample.java         // 线程生命周期
│   ├── ThreadDaemonExample.java            // 守护线程
│   └── ThreadInterruptExample.java         // 线程中断
├── producerconsumer/
│   ├── ProducerConsumerWaitNotify.java     // 生产-消费（wait/notify）
│   ├── ProducerConsumerBlockingQueue.java  // 生产-消费（BlockingQueue）
│   └── ProducerConsumerLockCondition.java  // 生产-消费（Lock+Condition）
├── threadpool/
│   ├── ThreadPoolExample.java              // 线程池
│   └── CompletableFutureExample.java       // 异步编排
├── synchronization/
│   ├── SynchronizedExample.java            // synchronized
│   ├── LockExample.java                    // ReentrantLock
│   └── VolatileExample.java               // volatile
└── tools/
    ├── CountDownLatchExample.java          // 倒计时门闩
    ├── CyclicBarrierExample.java           // 循环屏障
    └── SemaphoreExample.java              // 信号量
```