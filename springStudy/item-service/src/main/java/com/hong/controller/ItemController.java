package com.hong.controller;

import com.hong.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        itemService.getItems(ids);
        return null;
    }


}
