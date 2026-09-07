package com.ms.learn.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.common.result.Result;
import com.ms.learn.goods.constant.GoodsStatus;
import com.ms.learn.goods.entity.Goods;
import com.ms.learn.goods.feign.DictClient;
import com.ms.learn.goods.feign.vo.DictItemVO;
import com.ms.learn.goods.mapper.GoodsMapper;
import com.ms.learn.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    /** 商品分类字典类型 */
    private static final String GOODS_CATEGORY = "GOODS_CATEGORY";

    private final DictClient dictClient;

    @Override
    public Goods createGoods(Goods goods) {
        // 校验三级分类是否存在且为 level=3
        validateCategory(goods.getCategoryId());

        if (goods.getStatus() == null) {
            goods.setStatus(GoodsStatus.DRAFT);
        }
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        LocalDateTime now = LocalDateTime.now();
        goods.setCreateTime(now);
        goods.setUpdateTime(now);
        save(goods);
        return goods;
    }

    @Override
    public Goods updateGoods(Goods goods) {
        if (goods.getId() == null) {
            throw new BizException("商品ID不能为空");
        }
        Goods existing = getById(goods.getId());
        if (existing == null) {
            throw new BizException("商品不存在");
        }
        // 若修改了分类，校验分类合法性
        if (goods.getCategoryId() != null) {
            validateCategory(goods.getCategoryId());
        }
        goods.setUpdateTime(LocalDateTime.now());
        updateById(goods);
        return getById(goods.getId());
    }

    @Override
    public boolean onShelf(Long id) {
        Goods goods = getById(id);
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        goods.setStatus(GoodsStatus.ON_SHELF);
        goods.setUpdateTime(LocalDateTime.now());
        return updateById(goods);
    }

    @Override
    public boolean offShelf(Long id) {
        Goods goods = getById(id);
        if (goods == null) {
            throw new BizException("商品不存在");
        }
        goods.setStatus(GoodsStatus.OFF_SHELF);
        goods.setUpdateTime(LocalDateTime.now());
        return updateById(goods);
    }

    @Override
    public Goods getGoodsById(Long id) {
        return getById(id);
    }

    @Override
    public IPage<Goods> pageGoods(long current, long size, Integer status, Long categoryId, String name) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Goods::getStatus, status);
        }
        if (categoryId != null) {
            wrapper.eq(Goods::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Goods::getName, name);
        }
        wrapper.orderByDesc(Goods::getId);

        // 手动分页：先查总数，再用 LIMIT/OFFSET 查当前页（无需 PaginationInnerInterceptor）
        long total = baseMapper.selectCount(wrapper);
        long offset = (current - 1) * size;
        List<Goods> records = baseMapper.selectList(wrapper.last("LIMIT " + size + " OFFSET " + offset));

        Page<Goods> page = new Page<>(current, size, total);
        page.setRecords(records);
        return page;
    }

    @Override
    public List<DictItemVO> getCategoryTree() {
        Result<List<DictItemVO>> result = dictClient.listItemTree(GOODS_CATEGORY);
        if (result == null || result.getCode() == null || result.getCode() != 200) {
            log.warn("[GoodsService] 获取商品分类字典失败: {}", result == null ? "null" : result.getMessage());
            return List.of();
        }
        return result.getData();
    }

    /**
     * 校验分类ID是否存在且为三级分类（level=3）
     */
    private void validateCategory(Long categoryId) {
        if (categoryId == null) {
            throw new BizException("商品分类不能为空");
        }
        Result<DictItemVO> result = dictClient.getItem(categoryId);
        if (result == null || result.getCode() == null || result.getCode() != 200 || result.getData() == null) {
            throw new BizException("分类不存在或字典服务不可用");
        }
        DictItemVO item = result.getData();
        if (item.getLevel() == null || item.getLevel() != 3) {
            throw new BizException("商品只能挂在三级分类下");
        }
        if (item.getStatus() == null || item.getStatus() != 1) {
            throw new BizException("该分类已停用");
        }
    }
}
