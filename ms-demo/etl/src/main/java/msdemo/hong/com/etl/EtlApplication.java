package msdemo.hong.com.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ETL数据处理服务启动类
 *
 * <p>ETL服务负责：</p>
 * <ul>
 *   <li>数据抽取 - 从各种数据源抽取数据</li>
 *   <li>数据转换 - 数据清洗、格式转换</li>
 *   <li>数据加载 - 将处理后的数据加载到目标系统</li>
 *   <li>流式处理 - 使用Flink进行实时数据处理</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "msdemo.hong.com")
@EnableDiscoveryClient
public class EtlApplication {

    /**
     * 主函数 - 启动ETL数据处理服务
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(EtlApplication.class, args);
        System.out.println("ETL数据处理服务启动成功！");
    }
}
