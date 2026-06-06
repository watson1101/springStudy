package com.hong.service;

import com.hong.domain.vo.CartVO;

import java.util.List;

public interface CartService {

    public void addItemToCart(Long itemId);

    public void handleCartItems(List<CartVO> vos);
}
