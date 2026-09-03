package com.ms.learn.user.service;

import com.ms.learn.user.dto.LoginRequest;
import com.ms.learn.user.dto.LoginResponse;
import com.ms.learn.user.dto.RegisterRequest;
import com.ms.learn.user.dto.UserProfile;

public interface AuthService {

    UserProfile register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void logout();

    UserProfile currentUser();
}
