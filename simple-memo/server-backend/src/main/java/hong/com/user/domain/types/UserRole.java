package hong.com.user.domain.types;

/**
 * 用户角色枚举
 * 值对象 - 标识用户在系统中的角色权限
 *
 * @author admin
 * @since 2026-06-22
 */
public enum UserRole {

    /** 超级管理员 */
    ADMIN("admin", "超级管理员"),

    /** 普通用户 */
    USER("user", "普通用户");

    /** 角色标识 */
    private final String code;

    /** 角色描述 */
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的用户角色: " + code);
    }
}
