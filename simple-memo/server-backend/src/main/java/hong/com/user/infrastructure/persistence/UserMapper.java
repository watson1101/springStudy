package hong.com.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 基础设施层 - MyBatis-Plus 映射器，提供 CRUD 操作
 *
 * @author admin
 * @since 2026-06-22
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfoPO> {

}
