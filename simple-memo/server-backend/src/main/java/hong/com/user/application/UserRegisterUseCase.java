package hong.com.user.application;

import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.common.infrastructure.result.ResultCode;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import hong.com.user.domain.types.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户注册应用服务
 */
@Service
@RequiredArgsConstructor
public class UserRegisterUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public UserInfo register(String username, String password, String nickname, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ResultCode.USER_EXIST, "用户名 '" + username + "' 已被注册");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(passwordEncoder.encode(password));
        userInfo.setNickname(nickname != null ? nickname : username);
        userInfo.setEmail(email);
        userInfo.setRole(UserRole.USER);
        userInfo.setStatus(1);
        userInfo.setMultiDeviceLogin(1);
        userInfo.setDeleted(0);
        return userRepository.save(userInfo);
    }
}
