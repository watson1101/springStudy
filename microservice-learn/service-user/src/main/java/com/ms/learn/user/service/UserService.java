package com.ms.learn.user.service;

import com.ms.learn.user.entity.User;

import java.util.List;

public interface UserService {

    List<User> listAll();

    User getById(Long id);

    User create(User user);
}
