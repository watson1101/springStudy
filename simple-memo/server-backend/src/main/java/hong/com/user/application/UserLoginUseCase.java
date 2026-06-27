package hong.com.user.application;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.common.infrastructure.result.ResultCode;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户登录应用服务
 */
@Service
@RequiredArgsConstructor
public class UserLoginUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserInfo login(String username, String password) {
        UserInfo userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.LOGIN_FAIL));
        if (!userInfo.isEnabled()) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED, "账户已被禁用或已删除");
        }
        if (!passwordEncoder.matches(password, userInfo.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAIL);
        }
        StpUtil.login(userInfo.getId());
        return userInfo;
    }

    public void logout() {
        StpUtil.logout();
    }

    public UserInfo getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED, "用户不存在"));
    }
}
