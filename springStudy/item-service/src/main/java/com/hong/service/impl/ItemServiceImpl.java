package com.hong.service.impl;

import com.hong.domain.ItemVO;
import com.hong.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Override
    public List<ItemVO> getItems(String ids) {
        log.info("get items by ids: {}", ids);
        // 1. get items by ids
        // 2. return items
        return null;
    }
}
