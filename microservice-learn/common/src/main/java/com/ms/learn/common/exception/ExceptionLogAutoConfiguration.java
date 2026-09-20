package com.ms.learn.common.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 异常日志自动配置 —— 业务服务引入 common 后自动生效，无需逐服务改代码。
 * <p>总开关 exception-log.enabled 默认 true，可由本地 application.yml 或 Nacos 配置覆盖。</p>
 */
@Configuration
@ConditionalOnProperty(prefix = "exception-log", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ExceptionLogProperties.class)
public class ExceptionLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLogRecorder exceptionLogRecorder(ExceptionLogProperties properties,
                                                     org.springframework.beans.factory.ObjectProvider<ExceptionLogDbWriter> dbWriterProvider) {
        return new ExceptionLogRecorder(properties, dbWriterProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "exception-log.db", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ExceptionLogDbWriter exceptionLogDbWriter(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return new ExceptionLogDbWriter(jdbcTemplate);
    }
}
