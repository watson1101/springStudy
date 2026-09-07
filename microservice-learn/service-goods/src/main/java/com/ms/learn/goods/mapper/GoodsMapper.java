package com.ms.learn.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.goods.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品 Mapper
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
