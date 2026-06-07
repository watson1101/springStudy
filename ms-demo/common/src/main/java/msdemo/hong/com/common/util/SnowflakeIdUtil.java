package msdemo.hong.com.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法ID生成工具类
 *
 * <p>基于 Hutool 的 {@link Snowflake} 实现，提供分布式环境下的唯一ID生成能力。</p>
 *
 * <h3>雪花算法ID结构说明（64位Long）</h3>
 * <pre>
 *  0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 *  ├─ 符号位(1bit) ┤ 时间戳相对值(41bit)           ├ 机器ID(10bit) ┤ 序列号(12bit) ┤
 * </pre>
 * <ul>
 *   <li><b>符号位</b>：始终为0，保证ID为正整数</li>
 *   <li><b>时间戳</b>：41位，可使用69年</li>
 *   <li><b>机器ID</b>：10位，支持1024台机器</li>
 *   <li><b>序列号</b>：12位，同一毫秒内最多生成4096个ID</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 生成下一个雪花算法ID
 * Long id = SnowflakeIdUtil.nextId();
 *
 * // 生成ID的字符串形式
 * String idStr = SnowflakeIdUtil.nextIdStr();
 * }</pre>
 *
 * @author hong
 * @since 1.0.0
 */
@Slf4j
public class SnowflakeIdUtil {

    /**
     * 数据中心ID（0~31）
     *
     * <p>在分布式部署中，不同数据中心应配置不同的ID，防止ID冲突。
     * 此处默认使用0，生产环境建议通过配置中心动态注入。</p>
     */
    private static final long DATACENTER_ID = 0L;

    /**
     * 机器ID（0~31）
     *
     * <p>同一数据中心内，不同机器应配置不同的ID。
     * 此处默认使用0，生产环境建议通过配置中心动态注入。</p>
     */
    private static final long WORKER_ID = 0L;

    /**
     * 雪花算法实例
     *
     * <p>使用 Hutool 提供的 {@link Snowflake} 实现，
     * 线程安全，支持多线程并发调用。</p>
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(WORKER_ID, DATACENTER_ID);

    /**
     * 私有构造器，防止实例化
     */
    private SnowflakeIdUtil() {
        // 工具类不需要实例化
    }

    /**
     * 获取下一个雪花算法ID
     *
     * <p>返回一个64位的Long类型ID，全局唯一、趋势递增。
     * 适用于数据库主键、业务流水号等场景。</p>
     *
     * @return 雪花算法生成的Long类型ID
     */
    public static Long nextId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 获取下一个雪花算法ID的字符串形式
     *
     * <p>将雪花算法ID转换为字符串，方便在URL参数、JSON传输等场景中使用。</p>
     *
     * @return 雪花算法ID的字符串形式
     */
    public static String nextIdStr() {
        return SNOWFLAKE.nextIdStr();
    }

    /**
     * 解析雪花算法ID中的时间戳
     *
     * <p>可以从ID中反向解析出生成时间，用于排查问题和性能分析。</p>
     *
     * @param id 雪花算法ID
     * @return ID生成的时间戳（毫秒）
     */
    public static long parseTimestamp(long id) {
        return SNOWFLAKE.getGenerateDateTime(id);
    }
}