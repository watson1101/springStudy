package com.hong.service;

import com.hong.domain.ItemVO;

import java.util.List;

public interface ItemService {

    List<ItemVO> getItems(String ids);
}
