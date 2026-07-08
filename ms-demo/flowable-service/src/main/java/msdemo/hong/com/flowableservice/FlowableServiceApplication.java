package msdemo.hong.com.flowableservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Flowable工作流服务启动类
 * <p>
 * 该模块演示如何使用Flowable引擎实现基于BPMN2.0规范的业务流程管理
 * 支持流程定义部署、流程实例启动、任务查询与完成等核心功能
 * </p>
 */
@SpringBootApplication
public class FlowableServiceApplication {

    /**
     * 主启动方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(FlowableServiceApplication.class, args);
    }
}