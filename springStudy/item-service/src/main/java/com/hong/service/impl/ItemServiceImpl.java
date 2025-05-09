package com.hong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hong.domain.ItemVO;
import com.hong.mapper.ItemMapper;
import com.hong.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    @Override
    public List<ItemVO> getItemsByIdString(String ids) {
        log.info("get items by ids: {}", ids);
        List<Long> idList = null;
        try {
            idList = java.util.Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("parse ids error: {}", e.getMessage());
            return null;
        }
        // 调用 Mapper 查询
        return itemMapper.selectByIds(idList);

    }

    @Override
    public List<ItemVO> getItemsByItemName(String name) {
        QueryWrapper<ItemVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        return itemMapper.selectList(queryWrapper);
    }
}
