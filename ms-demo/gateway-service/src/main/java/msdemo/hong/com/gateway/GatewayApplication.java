package msdemo.hong.com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 *
 * <p>这是微服务架构的统一入口，负责：</p>
 * <ul>
 *   <li>路由转发 - 将请求转发到后端服务</li>
 *   <li>负载均衡 - 在多个服务实例间分配请求</li>
 *   <li>限流熔断 - 保护后端服务不被过载</li>
 *   <li>统一鉴权 - 验证用户身份和权限</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    /**
     * 主函数 - 启动网关服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("网关服务启动成功！");
    }
}
