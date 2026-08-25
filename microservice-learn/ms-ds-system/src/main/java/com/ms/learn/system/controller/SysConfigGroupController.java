package com.ms.learn.system.controller;

import com.ms.learn.common.result.Result;
import com.ms.learn.system.entity.SysConfigGroup;
import com.ms.learn.system.service.SysConfigGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理服务 - 配置分组 REST 接口
 */
@RestController
@RequestMapping("/api/system/group")
@RequiredArgsConstructor
public class SysConfigGroupController {

    private final SysConfigGroupService groupService;

    @GetMapping("/list")
    public Result<List<SysConfigGroup>> list() {
        return Result.success(groupService.listAll());
    }

    @GetMapping("/{id}")
    public Result<SysConfigGroup> get(@PathVariable Long id) {
        return Result.success(groupService.getById(id));
    }

    @PostMapping
    public Result<SysConfigGroup> create(@RequestBody SysConfigGroup group) {
        return Result.success(groupService.create(group));
    }
}
