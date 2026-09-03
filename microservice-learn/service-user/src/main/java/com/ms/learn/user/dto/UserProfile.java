package com.ms.learn.user.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfile(
        Long id,
        String username,
        String nickname,
        String email,
        Boolean enabled,
        LocalDateTime createTime,
        List<String> roles,
        List<String> permissions) {
}
