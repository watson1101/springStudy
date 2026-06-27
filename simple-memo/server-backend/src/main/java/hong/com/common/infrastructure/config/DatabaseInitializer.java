package hong.com.common.infrastructure.config;

import hong.com.user.domain.entity.UserInfo;
import hong.com.user.domain.repository.UserRepository;
import hong.com.user.domain.types.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据库初始化器，项目启动时自动插入初始用户数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) {
            log.info("数据库已初始化，跳过初始数据插入");
            return;
        }

        log.info("开始初始化数据库默认用户...");

        UserInfo admin = new UserInfo();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setNickname("超级管理员");
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(1);
        admin.setMultiDeviceLogin(1);
        admin.setDeleted(0);
        userRepository.save(admin);

        UserInfo hong = new UserInfo();
        hong.setUsername("hong");
        hong.setPassword(passwordEncoder.encode("123456"));
        hong.setNickname("测试用户 hong");
        hong.setRole(UserRole.USER);
        hong.setStatus(1);
        hong.setMultiDeviceLogin(1);
        hong.setDeleted(0);
        userRepository.save(hong);

        log.info("数据库初始化完成！已创建用户: admin(管理员), hong(普通用户)");
    }
}
