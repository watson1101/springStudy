package com.ms.learn.common.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 异常日志自动配置 —— 业务服务引入 common 后自动生效，无需逐服务改代码。
 * <p>总开关 exception-log.enabled 默认 true，可由本地 application.yml 或 Nacos 配置覆盖。</p>
 *
 * <p><b>条件说明（重要）</b>：</p>
 * <ul>
 *   <li>{@code @ConditionalOnClass(JdbcTemplate.class)}：{@link ExceptionLogDbWriter} 与
 *       {@link ExceptionLogRecorder} 的方法签名都引用了 JdbcTemplate 相关类型，Spring 解析
 *       Bean 方法返回类型时会触发类加载。WebFlux 网关等无 spring-jdbc 的环境若不跳过，
 *       会抛 NoClassDefFoundError 导致启动失败。</li>
 *   <li>{@code @ConditionalOnSingleCandidate(JdbcTemplate.class)}：多数据源服务
 *       （如 ms-ds-system 有 source/target 两个 JdbcTemplate）会因 bean 不唯一导致
 *       注入歧义启动失败。此时自动跳过落库，仅保留文件日志能力。</li>
 * </ul>
 */
@Configuration
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnSingleCandidate(JdbcTemplate.class)
@ConditionalOnProperty(prefix = "exception-log", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ExceptionLogProperties.class)
public class ExceptionLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLogRecorder exceptionLogRecorder(ExceptionLogProperties properties,
                                                     org.springframework.beans.factory.ObjectProvider<ExceptionLogDbWriter> dbWriterProvider) {
        return new ExceptionLogRecorder(properties, dbWriterProvider);
    }

    /**
     * 落库写入器：仅在 exception-log.db.enabled=true 时创建。
     * <p>外层已保证存在唯一 JdbcTemplate。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "exception-log.db", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ExceptionLogDbWriter exceptionLogDbWriter(JdbcTemplate jdbcTemplate) {
        return new ExceptionLogDbWriter(jdbcTemplate);
    }
}
