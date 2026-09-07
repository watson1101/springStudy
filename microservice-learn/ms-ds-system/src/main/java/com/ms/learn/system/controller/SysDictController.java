package com.ms.learn.system.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.system.entity.SysDict;
import com.ms.learn.system.entity.SysDictItem;
import com.ms.learn.system.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典 REST 接口（供 service-goods 等业务模块通过 Feign 调用）
 * <p>
 * 主要接口：
 * <ul>
 *   <li>GET  /api/system/dict/types           查询所有字典类型</li>
 *   <li>GET  /api/system/dict/type/{dictType} 按类型查询字典项（扁平）</li>
 *   <li>GET  /api/system/dict/tree/{dictType} 按类型查询字典项树（三级）</li>
 *   <li>GET  /api/system/dict/item/{itemId}   按ID查询字典项</li>
 *   <li>POST /api/system/dict/type            新增字典类型</li>
 *   <li>PUT  /api/system/dict/type            修改字典类型</li>
 *   <li>DELETE /api/system/dict/type/{id}     删除字典类型</li>
 *   <li>POST /api/system/dict/item            新增字典项</li>
 *   <li>PUT  /api/system/dict/item            修改字典项</li>
 *   <li>DELETE /api/system/dict/item/{id}     删除字典项</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    // ==================== 字典类型 ====================

    @GetMapping("/types")
    public Result<List<SysDict>> listTypes() {
        return Result.success(dictService.listDictTypes());
    }

    @PostMapping("/type")
    public Result<SysDict> createType(@RequestBody SysDict dict) {
        return Result.success(dictService.createDict(dict));
    }

    @PutMapping("/type")
    public Result<SysDict> updateType(@RequestBody SysDict dict) {
        return Result.success(dictService.updateDict(dict));
    }

    @DeleteMapping("/type/{id}")
    public Result<Boolean> deleteType(@PathVariable Long id) {
        return Result.success(dictService.deleteDict(id));
    }

    // ==================== 字典项 ====================

    @GetMapping("/type/{dictType}")
    public Result<List<SysDictItem>> listItems(@PathVariable String dictType) {
        return Result.success(dictService.listItemsByType(dictType));
    }

    @GetMapping("/tree/{dictType}")
    public Result<List<SysDictItem>> listTree(@PathVariable String dictType) {
        return Result.success(dictService.listItemTreeByType(dictType));
    }

    @GetMapping("/item/{itemId}")
    public Result<SysDictItem> getItem(@PathVariable Long itemId) {
        return Result.success(dictService.getItemById(itemId));
    }

    @PostMapping("/item")
    public Result<SysDictItem> createItem(@RequestBody SysDictItem item) {
        return Result.success(dictService.createItem(item));
    }

    @PutMapping("/item")
    public Result<SysDictItem> updateItem(@RequestBody SysDictItem item) {
        return Result.success(dictService.updateItem(item));
    }

    @DeleteMapping("/item/{id}")
    public Result<Boolean> deleteItem(@PathVariable Long id) {
        return Result.success(dictService.deleteItem(id));
    }
}
