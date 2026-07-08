package msdemo.hong.com.flowableservice.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flowable配置类
 * <p>
 * 用于配置Flowable引擎的自定义行为，包括流程引擎配置、数据源等
 * </p>
 */
@Configuration
public class FlowableConfig {

    /**
     * 配置SpringProcessEngineConfiguration
     * <p>
     * 可以在此方法中自定义Flowable引擎的各种配置参数
     * 例如：异步执行器、历史级别、自定义监听器等
     * </p>
     *
     * @return EngineConfigurationConfigurer 用于配置流程引擎
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> engineConfigurationConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setActivityFontName("宋体");
            engineConfiguration.setLabelFontName("宋体");
            engineConfiguration.setAnnotationFontName("宋体");
        };
    }
}