package com.ms.learn.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.product.entity.Product;
import com.ms.learn.product.mapper.ProductMapper;
import com.ms.learn.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public List<Product> listAll() {
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getId));
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(404, "商品不存在: " + id);
        }
        return product;
    }

    @Override
    public Product create(Product product) {
        product.setId(null);
        product.setCreateTime(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }
}
