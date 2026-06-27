package hong.com.user.domain.repository;

import hong.com.user.domain.entity.UserInfo;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 * DDD 领域层 - 定义用户聚合根的持久化契约
 * 由基础设施层实现具体的持久化逻辑
 *
 * @author admin
 * @since 2026-06-22
 */
public interface UserRepository {

    /**
     * 根据 ID 查询用户
     */
    Optional<UserInfo> findById(Long id);

    /**
     * 根据用户名查询用户
     */
    Optional<UserInfo> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    Optional<UserInfo> findByEmail(String email);

    /**
     * 保存用户（新增/更新）
     *
     * @param userInfo 用户领域实体
     * @return 保存后的用户
     */
    UserInfo save(UserInfo userInfo);

    /**
     * 查询所有用户
     */
    List<UserInfo> findAll();

    /**
     * 根据状态查询用户列表
     */
    List<UserInfo> findByStatus(Integer status);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 更新密码
     *
     * @param userId          用户ID
     * @param encodedPassword BCrypt 加密后的新密码
     */
    void updatePassword(Long userId, String encodedPassword);
}
