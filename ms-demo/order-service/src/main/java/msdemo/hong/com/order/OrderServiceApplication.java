package msdemo.hong.com.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 订单服务启动类
 *
 * <p>这是整个订单服务的入口，负责启动 Spring Boot 应用。</p>
 *
 * <h3>订单服务职责</h3>
 * <ul>
 *   <li><b>订单创建</b> - 接收前端提交的订单请求，校验商品库存和价格，创建新订单</li>
 *   <li><b>订单查询</b> - 支持按订单ID、用户ID、时间范围等多维度查询订单列表和详情</li>
 *   <li><b>订单状态管理</b> - 管理订单的生命周期状态流转（待支付 → 已支付 → 已发货 → 已完成 → 已取消）</li>
 *   <li><b>支付对接</b> - 对接支付网关处理支付回调，更新订单支付状态</li>
 *   <li><b>消息通知</b> - 订单状态变更时通过 Kafka 发送消息通知其他服务</li>
 * </ul>
 *
 * <h3>启动说明</h3>
 * 启动前需要确保以下中间件已启动：
 * <ol>
 *   <li>Nacos（服务注册与配置中心）- localhost:8848</li>
 *   <li>PostgreSQL（数据库）- localhost:5432</li>
 *   <li>Redis（缓存）- localhost:6379</li>
 *   <li>Kafka（消息队列）- localhost:9092</li>
 * </ol>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "msdemo.hong.com")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "msdemo.hong.com")
@MapperScan("msdemo.hong.com.order.mapper")
public class OrderServiceApplication {

    /**
     * 主函数 - 启动订单服务
     *
     * <p>这是 Java 应用的入口方法，Spring Boot 会通过 {@link SpringApplication#run(Class, String[])}
     * 自动配置并启动内嵌的 Tomcat 服务器。</p>
     *
     * @param args 命令行参数（通常为空）
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("========== 订单服务启动成功！ ==========");
    }
}