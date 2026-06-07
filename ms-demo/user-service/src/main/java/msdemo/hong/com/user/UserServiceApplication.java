package msdemo.hong.com.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 用户服务启动类
 *
 * <p>用户服务负责：</p>
 * <ul>
 *   <li>用户注册 - 新用户账号创建</li>
 *   <li>用户登录 - 身份验证和令牌发放</li>
 *   <li>用户信息管理 - 个人资料的增删改查</li>
 *   <li>密码管理 - 密码修改和重置</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "msdemo.hong.com")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "msdemo.hong.com")
@MapperScan("msdemo.hong.com.user.mapper")
public class UserServiceApplication {

    /**
     * 主函数 - 启动用户服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("用户服务启动成功！");
    }
}
