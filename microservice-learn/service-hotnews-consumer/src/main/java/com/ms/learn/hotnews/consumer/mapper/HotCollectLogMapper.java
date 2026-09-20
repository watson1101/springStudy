package com.ms.learn.hotnews.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.hotnews.consumer.entity.HotCollectLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采集批次日志 Mapper
 */
@Mapper
public interface HotCollectLogMapper extends BaseMapper<HotCollectLog> {
}
