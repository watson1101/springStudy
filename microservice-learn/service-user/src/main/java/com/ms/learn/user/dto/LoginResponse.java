package com.ms.learn.user.dto;

public record LoginResponse(
        String tokenName,
        String tokenValue,
        long tokenTimeout,
        UserProfile user) {
}
