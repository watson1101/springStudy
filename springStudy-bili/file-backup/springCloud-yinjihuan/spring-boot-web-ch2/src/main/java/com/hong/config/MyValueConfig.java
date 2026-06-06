package com.hong.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 获取配置文件中的参数，方法3：
 */
// step1.声明该类是配置文件类，声明变量的前缀
@ConfigurationProperties(prefix = "demo.value")
@Component
public class MyValueConfig {

    // step2.前缀已经配置，这里变量名直接是配置文件中的变量名即可
    private String name;

    // step3.编写 get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
