package com.hong.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.hong.domain.vo.CartVO;
import com.hong.domain.dto.ItemDTO;
import com.hong.service.CartService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    // Autowired 注入
//    @Autowired
//    private RestTemplate restTemplate;

    // Constructor 注入，对应注解AllArgsConstructor，所有参数都进行构造
//    private RestTemplate restTemplate;

    // 对应注解RequiredArgsConstructor，只有final参数进行构造 必备构造函数
    private final RestTemplate restTemplate;
    // 被注解 RequiredArgsConstructor 替代
//    public CartServiceImpl(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    private final DiscoveryClient discoveryClient;




    @Override
    public void addItemToCart(Long itemId) {
        // 1. add item to cart
        // 2. query item info
        // 3. update item info

    }

    @Override
    public void handleCartItems(List<CartVO> vos) {
        // get all item ids from cart items
        Set<Long> itemsIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());
        // 查询商品
        ResponseEntity<List<ItemDTO>> responseEntity = restTemplate.exchange(
                "http://localhost:9000/items?ids={ids}",
                HttpMethod.GET,
                null,
                // 字节码中没有泛型，泛型被擦除，所以需要使用 ParameterizedTypeReference，参数化类型的引用
                // List<ItemDTO>.class
                new ParameterizedTypeReference<List<ItemDTO>>() {
                },
                // CollUtil.join(itemsIds, ",") 连接成字符串
                Map.of("ids", CollUtil.join(itemsIds, ","))
        );
        // 解析响应
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }
        List<ItemDTO> items = responseEntity.getBody();
        if (items.isEmpty()) {
            return;
        }
        Map<Long, ItemDTO> itemMap = items.stream()
                .collect(Collectors.toMap(ItemDTO::getId, itemDTO -> itemDTO));
        log.info("itemMap: {}", itemMap);


    }
}
