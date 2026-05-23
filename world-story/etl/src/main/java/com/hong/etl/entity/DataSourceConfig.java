package com.hong.etl.entity;

import lombok.Data;

/**
 * 数据库连接配置
 */
@Data
public class DataSourceConfig {
    /**
     * 数据库类型: MYSQL, POSTGRESQL, MONGODB
     */
    private String type;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接参数(可选)
     */
    private String parameters;

    /**
     * 构建JDBC URL
     */
    public String buildJdbcUrl() {
        StringBuilder url = new StringBuilder();
        switch (type.toUpperCase()) {
            case "MYSQL":
                url.append("jdbc:mysql://").append(host).append(":").append(port)
                   .append("/").append(database);
                if (parameters != null && !parameters.isEmpty()) {
                    url.append("?").append(parameters);
                }
                break;
            case "POSTGRESQL":
                url.append("jdbc:postgresql://").append(host).append(":").append(port)
                   .append("/").append(database);
                if (parameters != null && !parameters.isEmpty()) {
                    url.append("?").append(parameters);
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + type);
        }
        return url.toString();
    }

    /**
     * 获取Flink CDC连接器名称
     */
    public String getCdcConnectorType() {
        switch (type.toUpperCase()) {
            case "MYSQL":
                return "mysql";
            case "POSTGRESQL":
                return "postgres";
            case "MONGODB":
                return "mongodb";
            default:
                throw new IllegalArgumentException("不支持的数据库类型: " + type);
        }
    }
}
