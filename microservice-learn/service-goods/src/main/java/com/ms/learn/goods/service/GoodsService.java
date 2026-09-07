package com.ms.learn.goods.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.ms.learn.goods.entity.Goods;
import com.ms.learn.goods.feign.vo.DictItemVO;

import java.util.List;

/**
 * 商品服务
 */
public interface GoodsService extends IService<Goods> {

    /** 录入商品（新增） */
    Goods createGoods(Goods goods);

    /** 修改商品信息 */
    Goods updateGoods(Goods goods);

    /** 上架商品 */
    boolean onShelf(Long id);

    /** 下架商品 */
    boolean offShelf(Long id);

    /** 根据ID查询商品 */
    Goods getGoodsById(Long id);

    /** 分页查询商品（可按状态/分类筛选） */
    IPage<Goods> pageGoods(long current, long size, Integer status, Long categoryId, String name);

    /** 获取商品分类三级树（从 ms-ds-system 字典） */
    List<DictItemVO> getCategoryTree();
}
