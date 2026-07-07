package msdemo.hong.com.flinkdemo1;

import msdemo.hong.com.flinkdemo1.kafka.KafkaSinkDemo;
import msdemo.hong.com.flinkdemo1.kafka.KafkaSourceDemo;
import msdemo.hong.com.flinkdemo1.window.WindowWordCountDemo;
import msdemo.hong.com.flinkdemo1.wordcount.WordCountBatchDemo;
import msdemo.hong.com.flinkdemo1.wordcount.WordCountStreamingDemo;

/**
 * Flink 学习示例运行入口
 *
 * <p>作为 flink-demo1 模块的主运行类，接收命令行参数选择要运行的示例。</p>
 *
 * <h3>使用方式</h3>
 * <pre>
 * # 查看帮助
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner help
 *
 * # 运行批处理 WordCount
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner batch
 *
 * # 运行流式 WordCount（需要先启动 nc -lk 9999）
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner stream
 *
 * # 运行窗口 WordCount（需要先启动 nc -lk 9998）
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner window
 *
 * # 运行 Kafka 数据源演示（需要 Kafka 已启动）
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner kafka-source
 *
 * # 运行 Kafka 数据汇演示（需要 Kafka 已启动）
 * java -cp flink-demo1/target/classes msdemo.hong.com.flinkdemo1.FlinkDemoRunner kafka-sink
 * </pre>
 *
 * <h3>提交到 Flink 集群</h3>
 * <pre>
 * # 先打包
 * mvn clean package -pl flink-demo1 -am -DskipTests
 *
 * # 提交到本地 Flink 集群（http://127.0.0.1:8081）
 * flink run -c msdemo.hong.com.flinkdemo1.FlinkDemoRunner flink-demo1/target/flink-demo1-1.0-SNAPSHOT.jar stream
 * </pre>
 *
 * @author hong
 * @since 1.0.0
 */
public class FlinkDemoRunner {

    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║        Flink 学习示例                       ║");
        System.out.println("║    Flink Web UI: http://127.0.0.1:8081     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        // 批处理 WordCount（从内存读取数据）
//        WordCountBatchDemo.demo();

//        // 流式 WordCount（从 Socket 读取数据）
//        WordCountStreamingDemo.demo();
//
//        // 窗口 WordCount（从 Socket 读取数据，按窗口统计）
//        WindowWordCountDemo.demo();
//
//        // Kafka 数据源（从 Kafka 读取消息）
//        KafkaSourceDemo.demo();
//
//        // Kafka 数据汇（处理数据后写入 Kafka）
//        KafkaSinkDemo.demo();

        //------
        // 默认显示帮助信息
        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "batch":
                // 批处理 WordCount（从内存读取数据）
                WordCountBatchDemo.demo();
                break;

            case "stream":
                // 流式 WordCount（从 Socket 读取数据）
                WordCountStreamingDemo.demo();
                break;

            case "window":
                // 窗口 WordCount（从 Socket 读取数据，按窗口统计）
                WindowWordCountDemo.demo();
                break;

            case "kafka-source":
                // Kafka 数据源（从 Kafka 读取消息）
                KafkaSourceDemo.demo();
                break;

            case "kafka-sink":
                // Kafka 数据汇（处理数据后写入 Kafka）
                KafkaSinkDemo.demo();
                break;

            default:
                System.out.println("未知命令: " + command);
                printHelp();
                break;
        }
    }

    private static void printHelp() {
        System.out.println("使用方法: FlinkDemoRunner <command>");
        System.out.println();
        System.out.println("命令列表:");
        System.out.println("  batch          运行批处理 WordCount（内存数据源）");
        System.out.println("  stream         运行流式 WordCount（需要 nc -lk 9999）");
        System.out.println("  window         运行窗口 WordCount（需要 nc -lk 9998）");
        System.out.println("  kafka-source   运行 Kafka 数据源演示（需要 Kafka）");
        System.out.println("  kafka-sink     运行 Kafka 数据汇演示（需要 Kafka）");
        System.out.println("  help           显示此帮助信息");
        System.out.println();
    }
}