package msdemo.hong.com.role;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 角色权限服务启动类
 *
 * <p>角色权限服务负责：</p>
 * <ul>
 *   <li>用户角色管理 - 用户与角色的关联</li>
 *   <li>角色管理 - 角色的增删改查</li>
 *   <li>权限管理 - 权限的定义和分配</li>
 *   <li>权限验证 - RBAC权限控制</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "msdemo.hong.com")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "msdemo.hong.com")
@MapperScan("msdemo.hong.com.role.mapper")
public class RoleApplication {

    /**
     * 主函数 - 启动角色权限服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RoleApplication.class, args);
        System.out.println("角色权限服务启动成功！");
    }
}
