package com.hong.heavenwill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hong.heavenwill.entity.RandomEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 随机事件Mapper接口
 * 提供对随机事件表的CRUD操作
 */
@Mapper
public interface RandomEventMapper extends BaseMapper<RandomEvent> {
    // 可以添加自定义的查询方法
}