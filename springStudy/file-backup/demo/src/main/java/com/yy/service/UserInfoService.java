package com.yy.service;

import com.yy.domain.UserInfo;

import java.util.List;

public interface UserInfoService {
    /**通过username查找用户信息;*/
    public UserInfo findByUsername(String username);

    public List<String> findByNoauthc();
}