package com.yy.service.impl;


import com.yy.domain.UserInfo;
import com.yy.mapper.UserInfoMapper;
import com.yy.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoMapper.findByUsername(username);
    }

    @Override
    public List<String> findByNoauthc() {
        System.out.println("UserInfoServiceImpl.findByNoauthc()");
        return userInfoMapper.findByNoauthc();
    }
}