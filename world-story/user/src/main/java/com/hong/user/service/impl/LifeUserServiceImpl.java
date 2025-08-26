package com.hong.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hong.user.dto.LifeUserDTO;
import com.hong.user.entity.LifeUser;
import com.hong.user.mapper.LifeUserMapper;
import com.hong.user.service.LifeUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 生命用户服务实现类
 */
@Service
public class LifeUserServiceImpl implements LifeUserService {
    
    @Resource
    private LifeUserMapper lifeUserMapper;
    
    @Override
    public LifeUserDTO getLifeUserById(Long id) {
        LifeUser lifeUser = lifeUserMapper.selectById(id);
        return lifeUser != null ? convertToDTO(lifeUser) : null;
    }
    
    @Override
    public List<LifeUserDTO> getLifeUsersByMetaUserId(Long metaUserId) {
        LambdaQueryWrapper<LifeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LifeUser::getMetaUserId, metaUserId);
        queryWrapper.orderByAsc(LifeUser::getLifeCount);
        
        List<LifeUser> lifeUsers = lifeUserMapper.selectList(queryWrapper);
        return lifeUsers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Override
    public LifeUserDTO createLifeUser(LifeUserDTO lifeUserDTO) {
        LifeUser lifeUser = convertToEntity(lifeUserDTO);
        
        // 获取该真灵的最新一世
        LambdaQueryWrapper<LifeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LifeUser::getMetaUserId, lifeUser.getMetaUserId());
        queryWrapper.orderByDesc(LifeUser::getLifeCount);
        queryWrapper.last("LIMIT 1");
        
        LifeUser lastLife = lifeUserMapper.selectOne(queryWrapper);
        
        // 如果没有指定第几世，则自动计算
        if (lifeUser.getLifeCount() == null) {
            lifeUser.setLifeCount(lastLife != null ? lastLife.getLifeCount() + 1 : 1);
        }
        
        lifeUserMapper.insert(lifeUser);
        return convertToDTO(lifeUser);
    }
    
    @Override
    public LifeUserDTO updateLifeUser(Long id, LifeUserDTO lifeUserDTO) {
        LifeUser existingLifeUser = lifeUserMapper.selectById(id);
        if (existingLifeUser == null) {
            return null;
        }
        
        LifeUser lifeUser = convertToEntity(lifeUserDTO);
        lifeUser.setId(id);
        lifeUser.setMetaUserId(existingLifeUser.getMetaUserId()); // 不允许修改真灵ID
        lifeUser.setLifeCount(existingLifeUser.getLifeCount());  // 不允许修改第几世
        
        lifeUserMapper.updateById(lifeUser);
        return convertToDTO(lifeUser);
    }
    
    @Override
    public boolean deleteLifeUser(Long id) {
        return lifeUserMapper.deleteById(id) > 0;
    }
    
    @Override
    public Page<LifeUserDTO> listLifeUsers(long current, long size, String worldType, String species) {
        Page<LifeUser> page = new Page<>(current, size);
        
        LambdaQueryWrapper<LifeUser> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(worldType)) {
            queryWrapper.eq(LifeUser::getWorldType, worldType);
        }
        if (StringUtils.hasText(species)) {
            queryWrapper.eq(LifeUser::getSpecies, species);
        }
        
        Page<LifeUser> lifeUserPage = lifeUserMapper.selectPage(page, queryWrapper);
        
        Page<LifeUserDTO> dtoPage = new Page<>(lifeUserPage.getCurrent(), lifeUserPage.getSize(), lifeUserPage.getTotal());
        dtoPage.setRecords(lifeUserPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()));
        
        return dtoPage;
    }
    
    @Override
    public LifeUserDTO convertToDTO(LifeUser lifeUser) {
        if (lifeUser == null) {
            return null;
        }
        
        LifeUserDTO dto = new LifeUserDTO();
        BeanUtils.copyProperties(lifeUser, dto);
        return dto;
    }
    
    @Override
    public LifeUser convertToEntity(LifeUserDTO lifeUserDTO) {
        if (lifeUserDTO == null) {
            return null;
        }
        
        LifeUser entity = new LifeUser();
        BeanUtils.copyProperties(lifeUserDTO, entity);
        return entity;
    }
}