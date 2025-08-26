package com.hong.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import com.hong.user.dto.LoginDTO;
import com.hong.user.dto.UserDTO;
import com.hong.user.entity.User;
import com.hong.user.service.LoginService;
import com.hong.user.service.UserService;
import com.hong.user.utils.PasswordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 登录服务实现类
 * 实现用户登录相关业务逻辑
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 用户DTO对象，如果登录失败则返回null
     */
    @Override
    public UserDTO login(LoginDTO loginDTO) {
        if (loginDTO == null || !StringUtils.hasText(loginDTO.getUsername()) 
                || !StringUtils.hasText(loginDTO.getPassword())) {
            return null;
        }

        // 根据用户名获取用户信息
        UserDTO userDTO = userService.getUserByUsername(loginDTO.getUsername());
        if (userDTO == null) {
            return null;
        }

        // 验证密码（使用加盐MD5验证密码）
        if (!PasswordUtils.validatePassword(loginDTO.getPassword(), userDTO.getPassword())) {
            // 如果加盐MD5验证失败，再尝试直接比较（兼容旧密码）
            if (!loginDTO.getPassword().equals(userDTO.getPassword())) {
                return null;
            }
        }

        // 验证账号状态
        if (userDTO.getStatus() != null && userDTO.getStatus() == 0) {
            return null;
        }

        // 使用SA-Token进行登录，将用户ID作为登录标识
        StpUtil.login(userDTO.getId());

        // 可以将用户信息存入Redis，便于快速获取
        // 注意：这里只存储必要的用户信息，不要存储敏感信息
        // 深拷贝用户对象，避免修改原始对象
        UserDTO sessionUserDTO = new UserDTO();
        BeanUtils.copyProperties(userDTO, sessionUserDTO);
        // 移除敏感信息
        sessionUserDTO.setPassword(null);
        sessionUserDTO.setEmail(null);
        sessionUserDTO.setPhone(null);
        
        StpUtil.getSession().set("userInfo", sessionUserDTO);

        return userDTO;
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户DTO对象
     */
    @Override
    public UserDTO getUserInfoByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }

        try {
            Long id = Long.parseLong(userId);
            // 首先尝试从SA-Token的session中获取用户信息
            Object userInfo = StpUtil.getSession().get("userInfo");
            if (userInfo instanceof UserDTO) {
                UserDTO sessionUser = (UserDTO) userInfo;
                if (sessionUser.getId().equals(id)) {
                    return sessionUser;
                }
            }
            
            // 如果session中没有或者用户不匹配，则从数据库获取
            return userService.getUserByUsername(userService.getById(id).getUsername());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 校验token是否有效
     * @param token token值
     * @return 校验结果，true表示有效，false表示无效
     */
    @Override
    public boolean checkToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            // 使用SA-Token的checkToken方法验证token是否有效
            return StpUtil.checkToken(token);
        } catch (Exception e) {
            return false;
        }
    }
}