package com.ms.learn.goods.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ms.learn.common.result.Result;
import com.ms.learn.goods.entity.Goods;
import com.ms.learn.goods.feign.vo.DictItemVO;
import com.ms.learn.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理 REST 接口
 * <p>功能：录入商品、上架/下架、修改商品信息（价格、图片、描述、规格、三级分类）。</p>
 * <p>所有接口需登录（Sa-Token）。</p>
 */
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@SaCheckLogin
public class GoodsController {

    private final GoodsService goodsService;

    /** 录入商品 */
    @PostMapping
    public Result<Goods> create(@RequestBody Goods goods) {
        return Result.success(goodsService.createGoods(goods));
    }

    /** 修改商品信息 */
    @PutMapping
    public Result<Goods> update(@RequestBody Goods goods) {
        return Result.success(goodsService.updateGoods(goods));
    }

    /** 上架商品 */
    @PutMapping("/{id}/on-shelf")
    public Result<Boolean> onShelf(@PathVariable Long id) {
        return Result.success(goodsService.onShelf(id));
    }

    /** 下架商品 */
    @PutMapping("/{id}/off-shelf")
    public Result<Boolean> offShelf(@PathVariable Long id) {
        return Result.success(goodsService.offShelf(id));
    }

    /** 根据ID查询商品 */
    @GetMapping("/{id}")
    public Result<Goods> get(@PathVariable Long id) {
        return Result.success(goodsService.getGoodsById(id));
    }

    /** 分页查询商品（演示 Sentinel 限流） */
    @GetMapping("/page")
    @SentinelResource(value = "goods-page", blockHandler = "pageBlockHandler")
    public Result<IPage<Goods>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name) {
        return Result.success(goodsService.pageGoods(current, size, status, categoryId, name));
    }

    /** Sentinel 限流/降级兜底 */
    public Result<IPage<Goods>> pageBlockHandler(long current, long size, Integer status,
                                                 Long categoryId, String name,
                                                 com.alibaba.csp.sentinel.slots.block.BlockException e) {
        return Result.fail(429, "商品列表接口被限流/降级：请稍后再试");
    }

    /** 获取商品分类三级树（从 ms-ds-system 字典） */
    @GetMapping("/categories")
    public Result<List<DictItemVO>> categories() {
        return Result.success(goodsService.getCategoryTree());
    }
}
