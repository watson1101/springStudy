package com.ms.learn.product.service;

import com.ms.learn.product.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> listAll();

    Product getById(Long id);

    Product create(Product product);
}
