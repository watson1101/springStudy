package com.ms.learn.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.ms.learn.common.result.Result;
import com.ms.learn.product.entity.Product;
import com.ms.learn.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务 REST 接口（演示 Sentinel 限流降级）
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    @SentinelResource(value = "product-list", blockHandler = "listBlockHandler")
    public Result<List<Product>> list() {
        // 模拟耗时，方便观察 Sentinel 限流效果
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        return Result.success(productService.listAll());
    }

    /** Sentinel 限流/降级时的兜底方法 */
    public Result<List<Product>> listBlockHandler(BlockException e) {
        return Result.fail(429, "接口被限流/降级：请稍后再试");
    }

    @GetMapping("/{id}")
    public Result<Product> get(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.success(productService.create(product));
    }
}
