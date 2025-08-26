package com.hong.user.dto;

import lombok.Data;

/**
 * 登录DTO类
 * 用于接收用户登录请求的参数
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 记住我，可选参数
     */
    private Boolean rememberMe;

    /**
     * 验证码，可选参数
     */
    private String captcha;

    /**
     * 验证码ID，可选参数
     */
    private String captchaId;
}