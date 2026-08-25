package com.ms.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ms.learn.system.entity.SysConfigLog;

/**
 * 配置变更日志 Mapper
 */
@Mapper
public interface SysConfigLogMapper extends BaseMapper<SysConfigLog> {
}
