package hong.com.user;

import cn.dev33.satoken.stp.StpUtil;
import hong.com.common.infrastructure.exception.BusinessException;
import hong.com.user.application.UserLoginUseCase;
import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import hong.com.user.domain.types.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserLoginUseCase userLoginUseCase;

    private UserInfo testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserInfo();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encoded_password");
        testUser.setRole(UserRole.USER);
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setMultiDeviceLogin(1);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);

        try (MockedStatic<StpUtil> stpUtil = mockStatic(StpUtil.class)) {
            UserInfo result = userLoginUseCase.login("testuser", "password");
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            stpUtil.verify(() -> StpUtil.login(1L));
        }
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername("nonexist")).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userLoginUseCase.login("nonexist", "password"));
    }
}
