package com.ms.learn.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
