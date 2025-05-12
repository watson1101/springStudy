package com.hong.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.hong.domain.vo.CartVO;
//import com.hong.domain.dto.ItemDTO;
import com.hong.api.dto.ItemDTO;
import com.hong.service.CartService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//import com.hong.client.ItemClient;
import com.hong.api.client.ItemClient;

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

// OpenFeign, 有这个则不在需要 RestTemplate 方式调用
    private final ItemClient itemClient;


    @Override
    public void addItemToCart(Long itemId) {
        // 1. add item to cart
        // 2. query item info
        // 3. update item info

    }

    // URL 写死的远程调用
//    @Override
    public void handleCartItemsV1(List<CartVO> vos) {
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

    /**
     * 使用服务发现的随机实例的远程调用
     * 服务只需要引入 nacos的 discovery 依赖，然后配置nacos地址，即可完成服务注册
     * @param vos
     */

    @Override
    public void handleCartItems(List<CartVO> vos) {
        // get all item ids from cart items
        Set<Long> itemsIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());
        List<ServiceInstance> instances = discoveryClient.getInstances("item-service");
        if (instances.isEmpty()) {
            return;
        }
        // 随机选择一个实例
        ServiceInstance serviceInstance = instances.get(RandomUtil.randomInt(instances.size()));
        // 查询商品
        ResponseEntity<List<ItemDTO>> responseEntity = restTemplate.exchange(
                serviceInstance.getUri()+"/items?ids={ids}",
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

    /**
     * 使用 openFeign 的远程调用
     */
    public void handleCartItemsOpenFeign(List<CartVO> vos){
        Set<Long> itemsIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());
        // 查询商品
        // 如果引入和 demo-api 模块，那么，文件最上面引入的 ItemClient 就不是当前模块的 ItemClient（实际上，当前模块的ItemClient就可以直接去掉了）然后引入 demo-api模块中的 ItemClient
        // 此时，项目实际无法运行，因为 ItemClient 所在包的包名发生变化（com.hong.api.client）无法构造，需要在启动类增加注解：
        // 指定 FeignClient 所在包 @EnableFeignClient(basePackages = "com.hong.api.client")
        // 或者指定 FeignClient 字节码 @EnableFeignClient(clients = {ItemClient.class})
        List<ItemDTO> items = itemClient.queryItemByIds(itemsIds);
        log.info("handleCartItemsOpenFeign-->items: {}", items);
        // ……

    }


}
