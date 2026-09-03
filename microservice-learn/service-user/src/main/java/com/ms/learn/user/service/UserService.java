package com.ms.learn.user.service;

import com.ms.learn.user.dto.UserProfile;

import java.util.List;

public interface UserService {

    List<UserProfile> listAll();

    UserProfile getById(Long id);
}
