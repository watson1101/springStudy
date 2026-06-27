package hong.com.user.application;

import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.common.infrastructure.result.ResultCode;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户密码管理应用服务
 */
@Service
@RequiredArgsConstructor
public class UserPasswordUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserInfo userInfo = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));
        if (!passwordEncoder.matches(oldPassword, userInfo.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAIL, "旧密码错误");
        }
        userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String username, String email, String newPassword) {
        UserInfo userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));
        if (userInfo.getEmail() == null || !userInfo.getEmail().equals(email)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱与注册信息不匹配");
        }
        userRepository.updatePassword(userInfo.getId(), passwordEncoder.encode(newPassword));
    }

    public void sendResetCode(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "该邮箱未注册"));
    }
}
