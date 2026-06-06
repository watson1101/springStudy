package com.hong.service;

import com.hong.domain.ItemVO;

import java.util.List;

public interface ItemService {

    List<ItemVO> getItemsByIdString(String ids);
    List<ItemVO> getItemsByItemName(String name);
}
