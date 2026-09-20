package com.ms.learn.common.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常日志配置项 —— 支持本地 application.yml 与 Nacos 配置中心双来源。
 * <p>开关默认全部打开；告警仅保留扩展接口，暂不实现具体处理。</p>
 */
@Data
@ConfigurationProperties(prefix = "exception-log")
public class ExceptionLogProperties {

    /** 总开关，默认开启 */
    private boolean enabled = true;

    /** 文件输出配置 */
    private File file = new File();

    /** 数据库落库配置 */
    private Db db = new Db();

    /** 告警配置（预留扩展，暂不处理） */
    private Alert alert = new Alert();

    @Data
    public static class File {
        /** 文件输出开关，默认开启 */
        private boolean enabled = true;
        /** 日志目录（部署时挂载宿主机目录，勿只存容器内） */
        private String path = "./logs/exception";
        /** 单个日志文件名 */
        private String fileName = "exception.log";
        /** 保留天数 */
        private int maxHistory = 30;
        /** 单个文件大小上限（MB），超过即滚动生成新文件，默认 100 */
        private long maxFileSizeMb = 100;
    }

    @Data
    public static class Db {
        /** 数据库落库开关，默认开启 */
        private boolean enabled = true;
    }

    @Data
    public static class Alert {
        /** 告警开关，默认关闭（预留扩展） */
        private boolean enabled = false;
    }
}
