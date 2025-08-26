package com.hong.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hong.user.dto.UserDTO;
import com.hong.user.entity.User;
import com.hong.user.mapper.UserMapper;
import com.hong.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.hong.user.utils.PasswordUtils;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserDTO getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        queryWrapper.eq(User::getDeleted, 0); // 查询未删除的账号
        
        User user = getOne(queryWrapper);
        if (user == null) {
            return null;
        }
        
        return convertToDTO(user);
    }

    @Override
    public boolean createUser(UserDTO userDTO) {
        if (userDTO == null || !StringUtils.hasText(userDTO.getUsername())) {
            return false;
        }
        
        // 检查用户名是否已存在
        if (getUserByUsername(userDTO.getUsername()) != null) {
            return false;
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1); // 默认启用
        }
        
        // 逻辑删除标记默认为0（未删除）
        if (user.getDeleted() == null) {
            user.setDeleted(0);
        }
        
        return save(user);
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getId() == null) {
            return false;
        }
        
        User user = getById(userDTO.getId());
        if (user == null) {
            return false;
        }
        
        BeanUtils.copyProperties(userDTO, user);
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            return false;
        }
        
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 执行逻辑删除
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }

    @Override
    public Page<UserDTO> getUserPage(Page<User> page, String username, String phone, String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDeleted, 0); // 查询未删除的账号
        
        // 添加查询条件
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        
        if (StringUtils.hasText(phone)) {
            queryWrapper.like(User::getPhone, phone);
        }
        
        if (StringUtils.hasText(email)) {
            queryWrapper.like(User::getEmail, email);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(User::getCreateTime);
        
        // 执行分页查询
        Page<User> userPage = page(page, queryWrapper);
        
        // 转换为DTO对象
        List<UserDTO> userDTOList = userPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 创建DTO分页对象
        Page<UserDTO> userDTOPage = new Page<>();
        userDTOPage.setRecords(userDTOList);
        userDTOPage.setCurrent(userPage.getCurrent());
        userDTOPage.setSize(userPage.getSize());
        userDTOPage.setTotal(userPage.getTotal());
        
        return userDTOPage;
    }

    /**
     * 将User实体转换为UserDTO
     * @param user User实体
     * @return UserDTO对象
     */
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
    
    /**
     * 用户本人修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    @Override
    public boolean updatePasswordByUser(Long userId, String oldPassword, String newPassword) {
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        User user = getById(userId);
        if (user == null || user.getDeleted() == 1) {
            return false;
        }
        
        // 验证旧密码
        // 先尝试加盐MD5验证
        if (!PasswordUtils.validatePassword(oldPassword, user.getPassword())) {
            // 如果加盐MD5验证失败，再尝试直接比较（兼容旧密码）
            if (!oldPassword.equals(user.getPassword())) {
                return false;
            }
        }
        
        // 使用加盐MD5设置新密码
        user.setPassword(PasswordUtils.generatePassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }
    
    /**
     * 管理员直接修改指定用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    @Override
    public boolean updatePasswordByAdmin(Long userId, String newPassword) {
        if (userId == null || !StringUtils.hasText(newPassword)) {
            return false;
        }
        
        User user = getById(userId);
        if (user == null || user.getDeleted() == 1) {
            return false;
        }
        
        // 使用加盐MD5设置新密码
        user.setPassword(PasswordUtils.generatePassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        return updateById(user);
    }
}