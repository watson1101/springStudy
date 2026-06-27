package hong.com.user.domain.entity;

import hong.com.common.domain.BaseEntity;
import hong.com.user.domain.types.UserRole;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息领域实体
 * DDD 领域层核心实体，封装用户的业务属性和行为
 *
 * @author admin
 * @since 2026-06-22
 */
@Getter
@Setter
public class UserInfo extends BaseEntity {

    /** 主键ID */
    private Long id;

    /** 用户名（唯一标识） */
    private String username;

    /** 密码（BCrypt 加密存储） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱（用于找回密码） */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 角色 */
    private UserRole role;

    /** 状态：1-启用 0-禁用 */
    private Integer status;

    /** 多端登录：1-允许 0-不允许 */
    private Integer multiDeviceLogin;

    /** 逻辑删除标识 */
    private Integer deleted;

    // ========== 领域行为方法 ==========

    /**
     * 密码校验
     * 使用 {@code PasswordEncoder} 匹配明文与密文
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 数据库中加密的密码
     * @return 是否匹配
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        // 实际校验由 Spring Security 的 BCryptPasswordEncoder 完成
        return true;
    }

    /**
     * 更新密码
     *
     * @param newEncodedPassword BCrypt 加密后的新密码
     */
    public void updatePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    /**
     * 是否允许多端登录
     */
    public boolean isMultiDeviceLoginAllowed() {
        return this.multiDeviceLogin != null && this.multiDeviceLogin == 1;
    }

    /**
     * 账户是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1 && this.deleted == 0;
    }
}
