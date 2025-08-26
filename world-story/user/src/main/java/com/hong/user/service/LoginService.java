package com.hong.user.service;

import com.hong.user.dto.LoginDTO;
import com.hong.user.dto.UserDTO;

/**
 * 登录服务接口
 * 处理用户登录相关业务逻辑
 */
public interface LoginService {

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 用户DTO对象，如果登录失败则返回null
     */
    UserDTO login(LoginDTO loginDTO);

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户DTO对象
     */
    UserDTO getUserInfoByUserId(String userId);

    /**
     * 校验token是否有效
     * @param token token值
     * @return 校验结果，true表示有效，false表示无效
     */
    boolean checkToken(String token);
}