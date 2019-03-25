package com.yy.mapper;

import com.yy.domain.UserInfo;

import java.util.List;

public interface UserInfoMapper {
    public UserInfo findByUsername(String username);
    public List<String> findByNoauthc();
}
