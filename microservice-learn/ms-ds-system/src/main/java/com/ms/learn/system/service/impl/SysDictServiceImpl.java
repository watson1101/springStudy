package com.ms.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ms.learn.common.exception.BizException;
import com.ms.learn.system.entity.SysDict;
import com.ms.learn.system.entity.SysDictItem;
import com.ms.learn.system.mapper.SysDictItemMapper;
import com.ms.learn.system.mapper.SysDictMapper;
import com.ms.learn.system.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictMapper dictMapper;
    private final SysDictItemMapper itemMapper;

    @Override
    public List<SysDict> listDictTypes() {
        return dictMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getStatus, 1)
                .orderByAsc(SysDict::getId));
    }

    @Override
    public SysDict getDictByType(String dictType) {
        return dictMapper.selectOne(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictType, dictType)
                .last("LIMIT 1"));
    }

    @Override
    public List<SysDictItem> listItemsByType(String dictType) {
        SysDict dict = getDictByType(dictType);
        if (dict == null) {
            return new ArrayList<>();
        }
        return itemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dict.getId())
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort, SysDictItem::getId));
    }

    @Override
    public List<SysDictItem> listItemTreeByType(String dictType) {
        List<SysDictItem> all = listItemsByType(dictType);
        if (all.isEmpty()) {
            return new ArrayList<>();
        }
        // 按 parent_id 分组
        Map<Long, List<SysDictItem>> byParent = all.stream()
                .collect(Collectors.groupingBy(SysDictItem::getParentId));
        // 为每个节点设置 children
        for (SysDictItem item : all) {
            item.setChildren(byParent.getOrDefault(item.getId(), new ArrayList<>()));
        }
        // 返回根节点（parent_id = 0）
        List<SysDictItem> roots = byParent.getOrDefault(0L, new ArrayList<>());
        roots.sort(Comparator.comparing(SysDictItem::getSort).thenComparing(SysDictItem::getId));
        return roots;
    }

    @Override
    public SysDictItem getItemById(Long itemId) {
        return itemMapper.selectById(itemId);
    }

    @Override
    public SysDict createDict(SysDict dict) {
        if (dict.getStatus() == null) {
            dict.setStatus(1);
        }
        if (dict.getDeleted() == null) {
            dict.setDeleted(0);
        }
        dictMapper.insert(dict);
        return dict;
    }

    @Override
    public SysDict updateDict(SysDict dict) {
        if (dict.getId() == null) {
            throw new BizException("字典ID不能为空");
        }
        dictMapper.updateById(dict);
        return dictMapper.selectById(dict.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDict(Long dictId) {
        // 先删除该字典下所有字典项
        itemMapper.delete(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dictId));
        return dictMapper.deleteById(dictId) > 0;
    }

    @Override
    public SysDictItem createItem(SysDictItem item) {
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        if (item.getDeleted() == null) {
            item.setDeleted(0);
        }
        if (item.getSort() == null) {
            item.setSort(0);
        }
        itemMapper.insert(item);
        return item;
    }

    @Override
    public SysDictItem updateItem(SysDictItem item) {
        if (item.getId() == null) {
            throw new BizException("字典项ID不能为空");
        }
        itemMapper.updateById(item);
        return itemMapper.selectById(item.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteItem(Long itemId) {
        // 递归删除子级
        List<SysDictItem> children = itemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getParentId, itemId));
        for (SysDictItem child : children) {
            deleteItem(child.getId());
        }
        return itemMapper.deleteById(itemId) > 0;
    }
}
