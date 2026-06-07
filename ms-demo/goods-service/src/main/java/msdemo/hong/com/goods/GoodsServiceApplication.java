package msdemo.hong.com.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品服务启动类
 *
 * <p>商品服务负责：</p>
 * <ul>
 *   <li>商品管理 - 商品的增删改查</li>
 *   <li>库存管理 - 库存查询和扣减</li>
 *   <li>价格管理 - 商品价格设置和调整</li>
 *   <li>商品分类 - 分类管理</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "msdemo.hong.com")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "msdemo.hong.com")
@MapperScan("msdemo.hong.com.goods.mapper")
public class GoodsServiceApplication {

    /**
     * 主函数 - 启动商品服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceApplication.class, args);
        System.out.println("商品服务启动成功！");
    }
}
