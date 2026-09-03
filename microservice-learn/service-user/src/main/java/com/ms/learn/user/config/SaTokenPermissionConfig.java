package com.ms.learn.user.config;

import cn.dev33.satoken.stp.StpInterface;
import com.ms.learn.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaTokenPermissionConfig implements StpInterface {

    private final UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return userMapper.selectPermissionCodesByUserId(toUserId(loginId));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userMapper.selectRoleCodesByUserId(toUserId(loginId));
    }

    private Long toUserId(Object loginId) {
        return Long.valueOf(loginId.toString());
    }
}
