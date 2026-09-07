package com.ms.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.system.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典类型 Mapper
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {
}
