package com.ms.learn.system.service;

import com.ms.learn.system.entity.SysConfigGroup;

import java.util.List;

/**
 * 配置分组 Service
 */
public interface SysConfigGroupService {

    /**
     * 查询全部分组
     */
    List<SysConfigGroup> listAll();

    /**
     * 按ID查询
     */
    SysConfigGroup getById(Long id);

    /**
     * 新增分组(占位)
     */
    SysConfigGroup create(SysConfigGroup group);
}
