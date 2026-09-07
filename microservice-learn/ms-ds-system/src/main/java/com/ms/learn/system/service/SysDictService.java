package com.ms.learn.system.service;

import com.ms.learn.system.entity.SysDict;
import com.ms.learn.system.entity.SysDictItem;

import java.util.List;

/**
 * 数据字典服务
 */
public interface SysDictService {

    /** 查询所有启用的字典类型 */
    List<SysDict> listDictTypes();

    /** 根据 dict_type 查询字典类型 */
    SysDict getDictByType(String dictType);

    /** 根据 dict_type 查询该字典下所有启用的字典项（扁平列表，按 sort 升序） */
    List<SysDictItem> listItemsByType(String dictType);

    /** 根据 dict_type 查询字典项并构建三级树（parent_id 关联） */
    List<SysDictItem> listItemTreeByType(String dictType);

    /** 根据字典项 ID 查询字典项 */
    SysDictItem getItemById(Long itemId);

    /** 新增字典类型 */
    SysDict createDict(SysDict dict);

    /** 修改字典类型 */
    SysDict updateDict(SysDict dict);

    /** 删除字典类型（同时删除其下所有字典项） */
    boolean deleteDict(Long dictId);

    /** 新增字典项 */
    SysDictItem createItem(SysDictItem item);

    /** 修改字典项 */
    SysDictItem updateItem(SysDictItem item);

    /** 删除字典项（若有子级则一并删除） */
    boolean deleteItem(Long itemId);
}
