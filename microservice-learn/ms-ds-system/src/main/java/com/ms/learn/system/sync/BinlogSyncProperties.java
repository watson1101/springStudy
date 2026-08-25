package com.ms.learn.system.sync;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * binlog 增量同步配置(读取 application.yml 的 sync.a-stock)
 * 说明: 开关 sync.a-stock.enabled 优先从 sys_config 表读取, 此处仅承载连接信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "sync.a-stock")
public class BinlogSyncProperties {

    /** Mac 源库连接信息 */
    private Source source = new Source();

    /** Ubuntu 目标库 */
    private Target target = new Target();

    /** 监听配置 */
    private Listen listen = new Listen();

    @Data
    public static class Source {
        private String host = "192.168.0.40";
        private int port = 3306;
        private String username = "root";
        private String password = "123456";
        private String database = "OPENCLAW_A_STOCK";
    }

    @Data
    public static class Target {
        private String database = "OPENCLAW_A_STOCK";
    }

    @Data
    public static class Listen {
        private long serverId = 65530;
        private int chunkSize = 1000;
    }
}
