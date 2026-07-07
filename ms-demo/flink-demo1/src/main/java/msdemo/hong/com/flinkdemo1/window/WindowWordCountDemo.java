package msdemo.hong.com.flinkdemo1.window;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Flink 窗口 WordCount 示例 — 基于处理时间的滚动窗口词频统计
 *
 * <p>与基础的 WordCount 不同，窗口 WordCount 按时间窗口分组统计，
 * 可以统计「最近 5 秒内」每个单词出现的次数。</p>
 *
 * <h3>窗口类型</h3>
 * <ul>
 *   <li><b>滚动窗口（Tumbling Window）</b> — 固定大小，不重叠（本例使用）</li>
 *   <li><b>滑动窗口（Sliding Window）</b> — 固定大小，可重叠</li>
 *   <li><b>会话窗口（Session Window）</b> — 按活动间隙分组</li>
 *   <li><b>全局窗口（Global Window）</b> — 所有数据一个窗口</li>
 * </ul>
 *
 * <h3>运行前提</h3>
 * <p>启动 Socket 数据源（在终端执行）：</p>
 * <pre>
 * nc -lk 9998
 * </pre>
 *
 * @author hong
 * @since 1.0.0
 */
public class WindowWordCountDemo {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9998;

    /**
     * 运行窗口 WordCount
     */
    public static void demo() throws Exception {
        System.out.println("═══════ Flink 窗口 WordCount（每5秒滚动窗口） ═══════");
        System.out.println("  请确保已运行: nc -lk " + PORT);
        System.out.println("  每5秒输出一次窗口内的词频统计结果\n");

        // ====== 1. 创建执行环境 ======
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // ====== 2. 数据源 ======
        DataStreamSource<String> textStream = env.socketTextStream(HOST, PORT);

        // ====== 3. 转换 + 窗口计算 ======
        SingleOutputStreamOperator<Tuple2<String, Integer>> windowCounts = textStream
                // 3.1 分词映射
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (line, collector) -> {
                    String[] words = line.toLowerCase().split("\\s+");
                    for (String word : words) {
                        if (word.length() > 0) {
                            collector.collect(Tuple2.of(word, 1));
                        }
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))

                // 3.2 按单词分组
                .keyBy(value -> value.f0)

                // 3.3 设置滚动窗口（Tumbling Window）
                // 按处理时间（ProcessingTime）每5秒划分一个窗口
                // 每个窗口内的数据独立统计
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))

                // 3.4 在窗口内对计数求和
                .sum(1);

        // ====== 4. 输出 ======
        windowCounts.print("窗口统计（5秒）");

        // ====== 5. 执行 ======
        env.execute("Flink 窗口 WordCount");
    }
}