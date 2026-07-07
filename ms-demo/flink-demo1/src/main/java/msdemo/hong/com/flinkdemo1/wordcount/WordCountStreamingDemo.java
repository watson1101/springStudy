package msdemo.hong.com.flinkdemo1.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * Flink 流式 WordCount 示例 — 从 Socket 读取数据流
 *
 * <p>这是 Flink 最经典的入门示例。从 Socket 端口读取文本流，
 * 对每行数据进行分词，统计每个单词出现的次数，并将结果打印到控制台。</p>
 *
 * <h3>运行前提</h3>
 * <p>启动一个 Socket 数据源（在终端执行）：</p>
 * <pre>
 * nc -lk 9999    # Linux/Mac
 * 或使用 Windows 版本的 netcat
 * </pre>
 * <p>然后在 nc 终端输入文本，即可看到 Flink 实时统计结果。</p>
 *
 * <h3>Flink 执行模式</h3>
 * <ul>
 *   <li><b>本地模式</b> — 在 IDE 中直接运行，使用本地环境（默认）</li>
 *   <li><b>集群模式</b> — 打包成 JAR 提交到 Flink 集群：
 *       {@code flink run -c msdemo.hong.com.flinkdemo1.FlinkDemoRunner flink-demo1-1.0-SNAPSHOT.jar wordcount-stream}</li>
 * </ul>
 *
 * <h3>Flink 本地地址</h3>
 * <ul>
 *   <li>Web UI：http://127.0.0.1:8081</li>
 *   <li>JobManager：127.0.0.1:6123</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
public class WordCountStreamingDemo {

    /** Socket 数据源主机地址（本地 Flunk 环境） */
    private static final String HOST = "127.0.0.1";

    /** Socket 数据源端口 */
    private static final int PORT = 9999;

    /**
     * 运行流式 WordCount
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>获取执行环境（StreamExecutionEnvironment）</li>
     *   <li>连接 Socket 数据源（netcat 发送文本行）</li>
     *   <li>对每行数据执行分词和映射转换</li>
     *   <li>按单词分组并统计出现次数</li>
     *   <li>打印结果到控制台</li>
     *   <li>触发作业执行</li>
     * </ol>
     */
    public static void demo() throws Exception {
        System.out.println("═══════ Flink 流式 WordCount（Socket 数据源） ═══════");
        System.out.println("  请确保已运行: nc -lk " + PORT);
        System.out.println("  在 nc 终端输入文本即可看到统计结果...\n");

        // ====== 1. 创建流处理执行环境 ======
        // StreamExecutionEnvironment 是所有 Flink 流处理程序的入口
        // getExecutionEnvironment() 会根据运行环境自动决定：
        //   - IDE中运行 → 本地模式（LocalEnvironment）
        //   - 提交到集群 → 集群模式（ClusterEnvironment）
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度（默认为 CPU 核数）
        // 并行度决定了 TaskManager 中并行执行任务的子任务数量
        env.setParallelism(2);

        // ====== 2. 添加数据源（Source） ======
        // 从 Socket 端口读取文本流，每行数据作为一条记录
        DataStreamSource<String> textStream = env.socketTextStream(HOST, PORT);

        // ====== 3. 数据转换（Transformation） ======
        // 3.1 分词与映射：将每行文本拆分为 (单词, 1) 的二元组
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = textStream
                // flatMap：将一条输入转换为零条或多条输出
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (line, collector) -> {
                    // 按空格切分行文本，得到单词数组
                    String[] words = line.toLowerCase().split("\\s+");
                    for (String word : words) {
                        if (word.length() > 0) {
                            // 输出 (单词, 1) 二元组，代表该单词出现一次
                            collector.collect(Tuple2.of(word, 1));
                        }
                    }
                })
                // 显式声明返回类型（Java 类型擦除需要）
                .returns(Types.TUPLE(Types.STRING, Types.INT))

                // 3.2 按单词分组
                // 使用 Tuple2 的第一个字段（f0）即单词作为分组key
                .keyBy(value -> value.f0)

                // 3.3 求和统计
                // 对 Tuple2 的第二个字段（f1）即计数进行累加
                .sum(1);

        // ====== 4. 输出结果（Sink） ======
        // 将计算结果打印到控制台
        // 参数 true 表示先打印标准输出的标识信息（如子任务序号等）
        wordCounts.print("WordCount 结果");

        // ====== 5. 触发作业执行 ======
        // execute() 触发 Flink 作业的真正执行，这是一个阻塞调用
        // 作业名称可以用于在 Flink Web UI（http://127.0.0.1:8081）上识别
        env.execute("Flink 流式 WordCount（Socket）");
    }
}