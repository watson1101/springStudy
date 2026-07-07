package msdemo.hong.com.flinkdemo1.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Flink Kafka 数据源示例 — 从 Kafka 读取消息
 *
 * <p>演示如何使用 Flink Kafka Connector 从 Kafka 主题消费消息，
 * 并对消息进行解析和处理。</p>
 *
 * <h3>前置条件</h3>
 * <p>Kafka 已启动在 127.0.0.1:9092，且已创建输入主题：</p>
 * <pre>
 * kafka-topics.bat --create --topic flink-input --bootstrap-server 127.0.0.1:9092
 * kafka-console-producer.bat --topic flink-input --bootstrap-server 127.0.0.1:9092
 * {"id":"1001","name":"商品1","price":99.5,"timestamp":1700000000000}
 * </pre>
 *
 * <p>Flink Web UI 地址（如启用日志推送）：http://127.0.0.1:8081</p>
 *
 * @author hong
 * @since 1.0.0
 */
public class KafkaSourceDemo {

    /** Kafka 服务器地址（本地安装） */
    private static final String KAFKA_BROKERS = "127.0.0.1:9092";

    /** 消费的主题名称 */
    private static final String INPUT_TOPIC = "flink-input";

    /** 消费者组 ID */
    private static final String GROUP_ID = "flink-demo-group";

    /**
     * 运行 Kafka 数据源演示
     *
     * <p>从 Kafka 读取 JSON 格式的订单消息，解析后提取关键字段输出。</p>
     */
    public static void demo() throws Exception {
        System.out.println("═══════ Flink Kafka 数据源示例 ═══════");
        System.out.println("  从 Kafka topic '" + INPUT_TOPIC + "' 消费消息...");
        System.out.println("  请在另一个终端启动 producer 发送消息：");
        System.out.println("  kafka-console-producer --topic " + INPUT_TOPIC + " --bootstrap-server " + KAFKA_BROKERS);
        System.out.println("  发送示例：{\"id\":\"1001\",\"name\":\"测试商品\",\"price\":99.5}\n");

        // ====== 1. 创建执行环境 ======
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 启用 Checkpoint（Kafka 消费需要 checkpoint 才能保证 exactly-once 语义）
        env.enableCheckpointing(5000);  // 每5秒做一次 checkpoint

        // ====== 2. 构建 Kafka Source ======
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                // 设置 Kafka Broker 地址
                .setBootstrapServers(KAFKA_BROKERS)
                // 设置消费的主题（可以订阅多个主题）
                .setTopics(INPUT_TOPIC)
                // 设置消费者组 ID
                .setGroupId(GROUP_ID)
                // 设置偏移量初始位置：从最早的消息开始消费
                // 如果已经消费过，会从上次提交的 offset 继续消费
                .setStartingOffsets(OffsetsInitializer.earliest())
                // 设置反序列化器：将 Kafka 中的 byte[] 反序列化为 String
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        // ====== 3. 将 Kafka Source 添加到执行环境 ======
        DataStreamSource<String> kafkaStream = env.fromSource(
                kafkaSource,
                WatermarkStrategy.noWatermarks(),  // 不使用 watermark（简单场景）
                "Kafka Source"
        );

        // 设置 Source 算子的并行度（建议与 Kafka 分区数一致）
        kafkaStream.setParallelism(1);

        // ====== 4. 处理数据 ======
        SingleOutputStreamOperator<String> processedStream = kafkaStream
                .map((MapFunction<String, String>) value -> {
                    try {
                        // 解析 JSON 格式的消息
                        JSONObject json = JSON.parseObject(value);
                        String id = json.getString("id");
                        String name = json.getString("name");
                        Double price = json.getDouble("price");

                        // 转换为可读的格式
                        return String.format("商品{id=%s, name=%s, price=%.2f}", id, name, price);
                    } catch (Exception e) {
                        // 解析失败的消息记录到错误输出
                        return "【解析失败】" + value;
                    }
                });

        // ====== 5. 输出结果 ======
        processedStream.print("Kafka 消息");

        // ====== 6. 执行作业 ======
        env.execute("Flink Kafka 数据源演示");
    }
}