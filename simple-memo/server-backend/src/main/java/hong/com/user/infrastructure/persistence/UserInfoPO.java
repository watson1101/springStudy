package hong.com.user.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import hong.com.common.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息持久化对象
 * 基础设施层 - MyBatis-Plus 实体，与数据库表 user_service_user_info 映射
 *
 * @author admin
 * @since 2026-06-22
 */
@Getter
@Setter
@TableName("user_service_user_info")
public class UserInfoPO extends BaseEntity {

    /** 主键ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 角色：admin/user */
    private String role;

    /** 状态：1-启用 0-禁用 */
    private Integer status;

    /** 多端登录：1-允许 0-不允许 */
    private Integer multiDeviceLogin;

    /** 逻辑删除：0-未删除 1-已删除 */
    private Integer deleted;
}
