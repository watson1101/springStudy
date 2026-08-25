package com.ms.learn.system.service;

/**
 * 系统配置读取服务
 * 用于从 sys_config 表读取配置项(开关等)
 */
public interface SysConfigService {

    /**
     * 读取配置值(未找到返回 null)
     */
    String getValue(String configKey);

    /**
     * 读取布尔配置(未找到或非法返回默认值)
     */
    boolean getBoolean(String configKey, boolean defaultValue);

    /**
     * 更新配置值(upsert)
     */
    boolean setValue(String configKey, String value, String remark);
}
