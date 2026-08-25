package msdemo.hong.com.flinkdemo1.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Flink Kafka 数据汇示例 — 处理数据后写入 Kafka
 *
 * <p>演示完整的端到端流处理流程：</p>
 * <ol>
 *   <li>使用自定义数据源模拟生成订单数据</li>
 *   <li>对数据进行简单的转换处理（计算含税价格）</li>
 *   <li>将处理后的数据写入 Kafka 主题</li>
 * </ol>
 *
 * <h3>前置条件</h3>
 * <p>Kafka 已启动在 127.0.0.1:9092，且已创建输出主题：</p>
 * <pre>
 * kafka-topics.bat --create --topic flink-output --bootstrap-server 127.0.0.1:9092
 * kafka-console-consumer.bat --topic flink-output --bootstrap-server 127.0.0.1:9092
 * </pre>
 *
 * @author hong
 * @since 1.0.0
 */
public class KafkaSinkDemo {

    private static final String KAFKA_BROKERS = "127.0.0.1:9092";
    private static final String OUTPUT_TOPIC = "flink-output";

    /**
     * 运行 Kafka 数据汇演示
     */
    public static void demo() throws Exception {
        System.out.println("═══════ Flink Kafka 数据汇示例 ═══════");
        System.out.println("  模拟生成订单数据 → 计算含税价格 → 写入 Kafka topic '" + OUTPUT_TOPIC + "'\n");
        System.out.println("  请在另一个终端启动 consumer 查看结果：");
        System.out.println("  kafka-console-consumer --topic " + OUTPUT_TOPIC + " --bootstrap-server " + KAFKA_BROKERS + "\n");

        // ====== 1. 创建执行环境 ======
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(5000);

        // ====== 2. 自定义数据源 ======
        // 模拟生成包含订单信息的字符串数据
        DataStreamSource<String> sourceStream = env.addSource(new MockOrderSource(), "模拟订单数据源");

        // ====== 3. 转换处理 ======
        // 解析 JSON、计算含税价格、转换为输出格式
        SingleOutputStreamOperator<String> processedStream = sourceStream
                .map((MapFunction<String, String>) jsonStr -> {
                    // 解析JSON
                    Map<String, Object> order = JSON.parseObject(jsonStr);
                    double originalPrice = (Double) order.get("amount");

                    // 计算含税价格（税率13%）
                    double taxRate = 0.13;
                    double taxAmount = originalPrice * taxRate;
                    double totalAmount = originalPrice + taxAmount;

                    // 构造输出JSON
                    Map<String, Object> output = new HashMap<>();
                    output.put("orderId", order.get("orderId"));
                    output.put("originalAmount", originalPrice);
                    output.put("taxRate", taxRate);
                    output.put("taxAmount", Math.round(taxAmount * 100.0) / 100.0);
                    output.put("totalAmount", Math.round(totalAmount * 100.0) / 100.0);
                    output.put("processTime", System.currentTimeMillis());

                    return JSON.toJSONString(output);
                });

        // ====== 4. 构建 Kafka Sink ======
        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                // 设置 Kafka Broker 地址
                .setBootstrapServers(KAFKA_BROKERS)
                // 设置消息序列化器：指定主题和序列化方式
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(OUTPUT_TOPIC)                    // 写入的目标主题
                        .setValueSerializationSchema(new SimpleStringSchema())  // value 序列化为 String
                        .build()
                )
                // 设置投递保证（至少一次，保证数据不丢失）
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        // ====== 5. 将数据流写入 Kafka ======
        processedStream.sinkTo(kafkaSink);

        // ====== 6. 为了本地验证，同时也打印到控制台 ======
        processedStream.print("写入 Kafka 的数据");

        // ====== 7. 执行作业 ======
        env.execute("Flink Kafka 数据汇演示");
    }

    /**
     * 模拟订单数据源
     *
     * <p>自定义 {@link SourceFunction}，每秒生成一条模拟订单 JSON 数据。
     * 注意：SourceFunction 在 Flink 1.18 中属于遗留 API，
     * 新项目推荐使用 {@link org.apache.flink.api.connector.source.Source} 接口。</p>
     */
    private static class MockOrderSource implements SourceFunction<String> {

        /** 是否正在运行 */
        private volatile boolean running = true;

        /** 随机数生成器 */
        private final Random random = new Random();

        /** 商品名称池 */
        private final String[] products = {"iPhone 15", "MacBook Pro", "AirPods", "iPad Air", "Apple Watch"};

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            int orderId = 1;
            while (running) {
                // 生成随机订单数据
                Map<String, Object> order = new HashMap<>();
                order.put("orderId", "ORD-" + String.format("%05d", orderId++));
                order.put("product", products[random.nextInt(products.length)]);
                order.put("amount", 50 + random.nextDouble() * 1000);  // 50~1050 元
                order.put("quantity", 1 + random.nextInt(3));          // 1~3 件

                // 发送数据
                ctx.collect(JSON.toJSONString(order));

                // 每秒生成一条
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}