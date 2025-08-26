package com.hong.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.MetaUserDTO;
import com.hong.user.entity.MetaUser;
import com.hong.user.mapper.MetaUserMapper;
import com.hong.user.service.MetaUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 真灵服务实现类
 */
@Service
public class MetaUserServiceImpl implements MetaUserService {
    
    @Resource
    private MetaUserMapper metaUserMapper;
    
    @Override
    public MetaUserDTO getMetaUserById(Long id) {
        MetaUser metaUser = metaUserMapper.selectById(id);
        return metaUser != null ? convertToDTO(metaUser) : null;
    }
    
    @Override
    public MetaUserDTO createMetaUser() {
        MetaUser metaUser = new MetaUser();
        metaUserMapper.insert(metaUser);
        return convertToDTO(metaUser);
    }
    
    @Override
    public boolean deleteMetaUser(Long id) {
        return metaUserMapper.deleteById(id) > 0;
    }
    
    @Override
    public Page<MetaUserDTO> listMetaUsers(long current, long size) {
        Page<MetaUser> page = new Page<>(current, size);
        Page<MetaUser> metaUserPage = metaUserMapper.selectPage(page, null);
        
        Page<MetaUserDTO> dtoPage = new Page<>(metaUserPage.getCurrent(), metaUserPage.getSize(), metaUserPage.getTotal());
        dtoPage.setRecords(metaUserPage.getRecords().stream().map(this::convertToDTO).toList());
        
        return dtoPage;
    }
    
    @Override
    public MetaUserDTO convertToDTO(MetaUser metaUser) {
        if (metaUser == null) {
            return null;
        }
        
        MetaUserDTO dto = new MetaUserDTO();
        BeanUtils.copyProperties(metaUser, dto);
        return dto;
    }
}