package msdemo.hong.com.multithread.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture 异步编排示例
 *
 * <p>{@link CompletableFuture} 是 Java 8 引入的异步编程工具，
 * 提供了函数式编程风格的异步回调能力。</p>
 *
 * <h3>核心特性</h3>
 * <ul>
 *   <li><b>异步执行</b> — supplyAsync / runAsync</li>
 *   <li><b>结果转换</b> — thenApply / thenApplyAsync</li>
 *   <li><b>结果消费</b> — thenAccept / thenRun</li>
 *   <li><b>任务组合</b> — thenCompose / thenCombine / allOf / anyOf</li>
 *   <li><b>异常处理</b> — exceptionally / handle / whenComplete</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class CompletableFutureExample {

    /**
     * 运行 CompletableFuture 演示
     */
    public static void demo() throws Exception {
        log.info("═══════ CompletableFuture 异步编排演示 ═══════");

        // ====== 1. 基础用法 ======
        demoBasic();

        // ====== 2. 任务编排 ======
        demoComposition();

        // ====== 3. 异常处理 ======
        demoExceptionHandling();

        // ====== 4. 多任务组合 ======
        demoAllOf();

        log.info("═══════ CompletableFuture 演示结束 ═══════\n");
    }

    /**
     * 基础用法：创建异步任务和结果转换
     */
    private static void demoBasic() throws Exception {
        log.info("  --- 基础用法 ---");

        // runAsync：无返回值异步执行
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            log.info("  异步任务1执行（无返回值）");
        });

        // supplyAsync：有返回值异步执行
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            log.info("  异步任务2执行（有返回值）");
            try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) {}
            return "任务2结果";
        });

        // thenApply：转换结果（同步）
        CompletableFuture<String> future3 = future2.thenApply(result -> {
            log.info("  转换结果: {}", result);
            return "转换后: " + result;
        });

        // thenAccept：消费结果（无返回值）
        future3.thenAccept(result -> {
            log.info("  最终结果: {}", result);
        });

        // 等待所有任务完成
        future1.get();
        future3.get();
    }

    /**
     * 任务编排：thenCompose / thenCombine
     */
    private static void demoComposition() throws Exception {
        log.info("  --- 任务编排 ---");

        // thenCompose：任务串联（第一个任务的输出是第二个任务的输入）
        CompletableFuture<String> composed = CompletableFuture
                .supplyAsync(() -> {
                    log.info("  查询用户信息...");
                    return "用户ID: 1001";
                })
                .thenCompose(userInfo -> {
                    log.info("  根据 {} 查询订单信息...", userInfo);
                    return CompletableFuture.supplyAsync(() -> {
                        try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) {}
                        return "订单号: ORDER-20240001";
                    });
                });

        // thenCombine：两个独立任务并行执行，结果合并
        CompletableFuture<String> taskA = CompletableFuture.supplyAsync(() -> {
            log.info("  查询商品信息...");
            try { TimeUnit.MILLISECONDS.sleep(400); } catch (InterruptedException e) {}
            return "商品: iPhone 15";
        });
        CompletableFuture<String> taskB = CompletableFuture.supplyAsync(() -> {
            log.info("  查询库存信息...");
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}
            return "库存: 50台";
        });

        CompletableFuture<String> combined = taskA.thenCombine(taskB,
                (product, stock) -> product + ", " + stock);

        log.info("  thenCompose 结果: {}", composed.get());
        log.info("  thenCombine 结果: {}", combined.get());
    }

    /**
     * 异常处理
     */
    private static void demoExceptionHandling() throws Exception {
        log.info("  --- 异常处理 ---");

        // exceptionally：发生异常时的回退值
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> {
                    if (Math.random() > 0.3) {
                        throw new RuntimeException("模拟业务异常");
                    }
                    return "正常结果";
                })
                .exceptionally(ex -> {
                    log.warn("  捕获异常: {}", ex.getMessage());
                    return "默认降级结果";
                });

        log.info("  exceptionally 结果: {}", future.get());

        // handle：无论是否异常都处理（可以转换异常为正常值）
        CompletableFuture<String> handled = CompletableFuture
                .supplyAsync(() -> {
                    throw new RuntimeException("另一个异常");
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        log.warn("  handle 捕获异常: {}", ex.getMessage());
                        return "降级处理";
                    }
                    return (String) result;
                });

        log.info("  handle 结果: {}", handled.get());
    }

    /**
     * 多任务组合：allOf / anyOf
     */
    private static void demoAllOf() throws Exception {
        log.info("  --- 多任务组合 ---");

        // 创建3个独立任务（类型全部声明为 CompletableFuture<String>）
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) {}
            return "Task 1";
        });
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) {}
            return "Task 2";
        });
        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) {}
            return "Task 3";
        });

        // allOf：等待所有任务完成
        log.info("  等待所有任务完成...");
        long start = System.currentTimeMillis();
        CompletableFuture<Void> allOf = CompletableFuture.allOf(task1, task2, task3);
        allOf.get();  // 阻塞直到所有任务完成
        log.info("  所有任务完成，耗时: {}ms", System.currentTimeMillis() - start);

        // 所有任务完成后收集结果
        String result1 = task1.get();
        String result2 = task2.get();
        String result3 = task3.get();
        log.info("  任务1: {}, 任务2: {}, 任务3: {}", result1, result2, result3);

        // anyOf：任意一个任务完成即返回
        log.info("  等待任意一个任务完成...");
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(task1, task2, task3);
        Object firstResult = anyOf.get();
        log.info("  最快完成的任务结果: {}", firstResult);
    }
}