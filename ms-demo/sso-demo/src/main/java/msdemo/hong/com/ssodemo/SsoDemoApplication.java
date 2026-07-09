package msdemo.hong.com.ssodemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SSO单点登录演示模块启动类
 * 
 * <p>该模块模拟了单点登录（Single Sign-On）的完整流程，包含两个核心角色：
 * <ul>
 *   <li><strong>SSO Server (身份提供者/IdP)</strong>：负责用户认证和令牌发放</li>
 *   <li><strong>SSO Client (服务提供者/SP)</strong>：负责令牌验证和资源保护</li>
 * </ul>
 * </p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>用户在一个系统登录后，其他关联系统无需再次登录</li>
 *   <li>使用JWT作为令牌格式，Redis作为令牌存储</li>
 *   <li>模拟其他模块（如user-service、order-service）通过SSO登录当前系统</li>
 * </ul>
 * </p>
 * 
 * <p>启动端口：8006</p>
 */
@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
public class SsoDemoApplication {

    /**
     * 主方法，启动SSO演示模块
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SsoDemoApplication.class, args);
    }

}