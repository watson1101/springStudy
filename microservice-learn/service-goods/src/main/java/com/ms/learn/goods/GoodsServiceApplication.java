package com.ms.learn.goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品管理服务启动类
 * <p>功能：录入商品、上架/下架、修改商品信息（价格、图片、描述、规格、三级分类）。</p>
 * <p>依赖：通过 Feign 调用 ms-ds-system 获取商品分类字典。</p>
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.ms.learn.goods.mapper")
public class GoodsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceApplication.class, args);
    }
}
