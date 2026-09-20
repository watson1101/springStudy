package com.ms.learn.hotnews.consumer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.hotnews.consumer.entity.HotNews;
import org.apache.ibatis.annotations.Mapper;

/**
 * 热榜数据 Mapper
 */
@Mapper
public interface HotNewsMapper extends BaseMapper<HotNews> {
}
