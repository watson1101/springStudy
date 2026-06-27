package hong.com.user;

import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.common.infrastructure.result.ResultCode;
import hong.com.user.application.UserRegisterUseCase;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户注册用例单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserRegisterUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserRegisterUseCase userRegisterUseCase;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInfo result = userRegisterUseCase.register("newuser", "123456", "新用户", "new@test.com");
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("新用户", result.getNickname());
        verify(userRepository).save(any(UserInfo.class));
    }

    @Test
    void testRegister_UsernameExists() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> userRegisterUseCase.register("existing", "123456", null, null));
        verify(userRepository, never()).save(any());
    }
}
