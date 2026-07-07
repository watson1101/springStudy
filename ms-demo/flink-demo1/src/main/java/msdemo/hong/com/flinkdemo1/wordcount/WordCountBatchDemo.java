package msdemo.hong.com.flinkdemo1.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

/**
 * Flink 批处理 WordCount 示例 — 使用 DataStream API 处理有界数据
 *
 * <p>在 Flink 1.18 中，推荐使用 DataStream API 进行批流一体处理。
 * 通过将输入数据作为有界流处理，实现批处理语义。</p>
 *
 * <p>本例演示了 Flink 批处理的基本流程，数据直接通过 {@code fromCollection()} 传入，
 * 不需要外部数据源。</p>
 *
 * @author hong
 * @since 1.0.0
 */
public class WordCountBatchDemo {

    /**
     * 运行批处理 WordCount
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>创建流处理执行环境</li>
     *   <li>从内存集合加载数据（作为有界流）</li>
     *   <li>分词、分组、统计</li>
     *   <li>打印结果</li>
     * </ol>
     */
    public static void demo() throws Exception {
        System.out.println("═══════ Flink 批处理 WordCount（内存数据源） ═══════\n");

        // ====== 1. 创建流处理执行环境 ======
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // ====== 2. 准备数据（Source） ======
        // 从内存集合创建数据集，适合小规模演示
        List<String> dataList = Arrays.asList(
                "Apache Flink is a framework",
                "Flink is also a distributed processing engine",
                "Apache Hadoop and Apache Flink are both big data tools"
        );

        // 将集合转换为 Flink DataStream（有界流）
        DataStreamSource<String> textStream = env.fromCollection(dataList);

        // ====== 3. 数据转换（Transformation） ======
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCounts = textStream
                // flatMap：逐行分词
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (line, collector) -> {
                    // 将行文本转为小写并按空格分割
                    String[] words = line.toLowerCase().split("\\s+");
                    for (String word : words) {
                        if (word.length() > 0) {
                            // 每个单词计数为1
                            collector.collect(Tuple2.of(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                // 按单词分组（使用 f0 字段）
                .keyBy(value -> value.f0)
                // 对第二个字段求和
                .sum(1);

        // ====== 4. 输出结果（Sink） ======
        // 将计算结果打印到控制台
        wordCounts.print("WordCount 结果");

        // ====== 5. 触发作业执行 ======
        env.execute("Flink 批处理 WordCount");
    }
}