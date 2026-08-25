package com.ms.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.system.entity.SysConfig;
import com.ms.learn.system.mapper.SysConfigMapper;
import com.ms.learn.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统配置读取服务实现
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public String getValue(String configKey) {
        SysConfig c = getByKey(configKey);
        return c == null ? null : c.getConfigValue();
    }

    @Override
    public boolean getBoolean(String configKey, boolean defaultValue) {
        String v = getValue(configKey);
        if (v == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(v.trim())
                || "1".equals(v.trim())
                || "on".equalsIgnoreCase(v.trim());
    }

    @Override
    public boolean setValue(String configKey, String value, String remark) {
        SysConfig existing = getByKey(configKey);
        if (existing == null) {
            SysConfig c = new SysConfig();
            c.setGroupId(2L); // 系统设置分组
            c.setConfigKey(configKey);
            c.setConfigName(configKey);
            c.setConfigValue(value);
            c.setValueType(detectType(value));
            c.setStatus(1);
            c.setDeleted(0);
            c.setRemark(remark);
            c.setCreateTime(LocalDateTime.now());
            c.setUpdateTime(LocalDateTime.now());
            configMapper.insert(c);
            return true;
        }
        existing.setConfigValue(value);
        existing.setUpdateTime(LocalDateTime.now());
        if (remark != null) {
            existing.setRemark(remark);
        }
        configMapper.updateById(existing);
        return true;
    }

    private SysConfig getByKey(String configKey) {
        return configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getDeleted, 0)
                .last("LIMIT 1"));
    }

    private String detectType(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return "bool";
        }
        return "string";
    }
}
