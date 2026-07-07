package msdemo.hong.com.ssodemo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SSO用户数据传输对象
 * 
 * <p>用于在SSO Server和SSO Client之间传递用户信息，包含用户的基本身份信息。</p>
 * 
 * <p>该对象会被序列化到JWT令牌中，因此需要实现Serializable接口。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，系统唯一标识
     */
    private Long userId;

    /**
     * 用户名，登录账号
     */
    private String username;

    /**
     * 用户昵称，显示名称
     */
    private String nickname;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户角色列表，逗号分隔
     */
    private String roles;

    /**
     * 用户所属系统/模块标识
     */
    private String systemId;

    /**
     * 创建演示用户实例
     * 
     * @param username 用户名
     * @return SsoUserDTO实例
     */
    public static SsoUserDTO createDemoUser(String username) {
        return SsoUserDTO.builder()
                .userId(SsoConstants.DEMO_USER_ID)
                .username(username)
                .nickname(SsoConstants.DEMO_USER_NICKNAME)
                .email(username + "@demo.com")
                .phone("13800138000")
                .roles("ADMIN,USER")
                .systemId("sso-demo")
                .build();
    }

}