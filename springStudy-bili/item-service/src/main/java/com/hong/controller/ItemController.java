package com.hong.controller;

import com.alibaba.fastjson2.JSONObject;
import com.hong.domain.ItemVO;
import com.hong.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/item")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/ping")
    public String ping() {
        return "item pong";
    }

    @PostMapping("/items")
    public String getItems(@RequestParam("ids") String ids) {
        log.info("get items by ids: {}", ids);
        List<ItemVO> items = itemService.getItemsByIdString(ids);
        return JSONObject.toJSONString(items);
    }


}
