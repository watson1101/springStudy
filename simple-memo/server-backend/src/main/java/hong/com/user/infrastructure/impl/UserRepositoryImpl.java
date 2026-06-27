package hong.com.user.infrastructure.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import hong.com.user.infrastructure.converter.UserConverter;
import hong.com.user.infrastructure.persistence.UserInfoPO;
import hong.com.user.infrastructure.persistence.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现，使用 MyBatis-Plus 进行数据库操作
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public Optional<UserInfo> findById(Long id) {
        return Optional.ofNullable(userConverter.toDomain(userMapper.selectById(id)));
    }

    @Override
    public Optional<UserInfo> findByUsername(String username) {
        return Optional.ofNullable(userConverter.toDomain(
                userMapper.selectOne(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getUsername, username))));
    }

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        return Optional.ofNullable(userConverter.toDomain(
                userMapper.selectOne(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getEmail, email))));
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        UserInfoPO po = userConverter.toPO(userInfo);
        if (po.getId() == null) {
            userMapper.insert(po);
        } else {
            userMapper.updateById(po);
        }
        return userConverter.toDomain(po);
    }

    @Override
    public List<UserInfo> findAll() {
        return userMapper.selectList(null).stream().map(userConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<UserInfo> findByStatus(Integer status) {
        return userMapper.selectList(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getStatus, status))
                .stream().map(userConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(new LambdaQueryWrapper<UserInfoPO>().eq(UserInfoPO::getUsername, username)) > 0;
    }

    @Override
    public void updatePassword(Long userId, String encodedPassword) {
        UserInfoPO po = new UserInfoPO();
        po.setId(userId);
        po.setPassword(encodedPassword);
        userMapper.updateById(po);
    }
}
