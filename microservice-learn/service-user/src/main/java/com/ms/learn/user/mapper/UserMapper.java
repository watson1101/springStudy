package com.ms.learn.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ms.learn.user.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
            SELECT DISTINCT r.code
            FROM t_role r
            JOIN t_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            ORDER BY r.code
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT p.code
            FROM t_permission p
            JOIN t_role_permission rp ON rp.permission_id = p.id
            JOIN t_user_role ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = #{userId}
            ORDER BY p.code
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT id FROM t_role WHERE code = #{code}")
    Long selectRoleIdByCode(@Param("code") String code);

    @Insert("""
            INSERT INTO t_user_role (user_id, role_id)
            VALUES (#{userId}, #{roleId})
            ON CONFLICT DO NOTHING
            """)
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
