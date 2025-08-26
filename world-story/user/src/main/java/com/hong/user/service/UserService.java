package com.hong.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hong.user.dto.UserDTO;
import com.hong.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户DTO对象
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * 创建新用户
     * @param userDTO 用户DTO对象
     * @return 创建成功返回true，否则返回false
     */
    boolean createUser(UserDTO userDTO);
    
    /**
     * 更新用户信息
     * @param userDTO 用户DTO对象
     * @return 更新成功返回true，否则返回false
     */
    boolean updateUser(UserDTO userDTO);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteUser(Long userId);
    
    /**
     * 分页查询用户列表
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param email 邮箱（可选）
     * @return 用户DTO分页对象
     */
    Page<UserDTO> getUserPage(Page<User> page, String username, String phone, String email);
    
    /**
     * 用户本人修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    boolean updatePasswordByUser(Long userId, String oldPassword, String newPassword);
    
    /**
     * 管理员直接修改指定用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    boolean updatePasswordByAdmin(Long userId, String newPassword);
}