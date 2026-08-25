package com.ms.learn.system.service.impl;

import com.ms.learn.common.exception.BizException;
import com.ms.learn.system.entity.SysConfigGroup;
import com.ms.learn.system.mapper.SysConfigGroupMapper;
import com.ms.learn.system.service.SysConfigGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配置分组 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysConfigGroupServiceImpl implements SysConfigGroupService {

    private final SysConfigGroupMapper groupMapper;

    @Override
    public List<SysConfigGroup> listAll() {
        return groupMapper.selectList(null);
    }

    @Override
    public SysConfigGroup getById(Long id) {
        SysConfigGroup group = groupMapper.selectById(id);
        if (group == null) {
            throw new BizException(404, "配置分组不存在: " + id);
        }
        return group;
    }

    @Override
    public SysConfigGroup create(SysConfigGroup group) {
        group.setId(null);
        group.setCreateTime(LocalDateTime.now());
        group.setUpdateTime(LocalDateTime.now());
        groupMapper.insert(group);
        return group;
    }
}
