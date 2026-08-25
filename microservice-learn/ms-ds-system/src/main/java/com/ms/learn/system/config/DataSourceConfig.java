package com.ms.learn.system.config;

import com.ms.learn.system.sync.BinlogSyncProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源配置
 * 提供"源库(Mac)"和"目标库(Ubuntu OPENCLAW_A_STOCK)"两路 JdbcTemplate
 * 注意: 同步目标库与系统管理库(ms_ds_sys_config)不同, 需独立数据源
 */
@Configuration
public class DataSourceConfig {

    /**
     * 源库 JdbcTemplate(Mac 192.168.0.40) - 用于读取表结构
     */
    @Bean
    public JdbcTemplate sourceJdbcTemplate(BinlogSyncProperties props) {
        DataSource ds = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://" + props.getSource().getHost() + ":" + props.getSource().getPort()
                        + "/" + props.getSource().getDatabase()
                        + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true")
                .username(props.getSource().getUsername())
                .password(props.getSource().getPassword())
                .build();
        return new JdbcTemplate(ds);
    }

    /**
     * 目标库 JdbcTemplate(Ubuntu 192.168.0.27, 库 OPENCLAW_A_STOCK) - 写入同步数据
     * 独立数据源, 不依赖系统管理库(ms_ds_sys_config)的默认数据源
     */
    @Bean
    public JdbcTemplate targetJdbcTemplate(BinlogSyncProperties props) {
        DataSource ds = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://192.168.0.27:3306/" + props.getTarget().getDatabase()
                        + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true")
                .username("root")
                .password("123456")
                .build();
        return new JdbcTemplate(ds);
    }
}
