package com.hong.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTool {

    private final JWTSigner jwtSigner;

    public JwtTool(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("RS256", keyPair);
    }

    public String createToken(Long userId, Duration ttl) {
        return JWT.create()
                .setPayload("userId", userId)
//                .withPayload("userId", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .setSigner(jwtSigner)
                .sign();
    }

    public Long parseToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token is null，未登录");
        }
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new IllegalArgumentException("token is invalid，token不合法");
        }
        // 校验 jwt 是否有效
        if (!jwt.verify()) {
            throw new IllegalArgumentException("token is invalid，token不合法");
        }
        // 校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (Exception e) {
            throw new IllegalArgumentException("token is expired，token过期");
        }
        // 数据格式校验
        Object userPayload = jwt.getPayload("user");
        if (userPayload == null) {
            // 数据为空
            throw new IllegalArgumentException("token is invalid，token不合法");
        }
        // 数据解析
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // 数据格式不合法
            throw new IllegalArgumentException("token is invalid，token不合法");
        }
    }
}
