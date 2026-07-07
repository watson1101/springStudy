package msdemo.hong.com.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI (Swagger) 全局配置类
 *
 * <p>SpringDoc 是 Spring Boot 3.x 推荐的接口文档生成框架，替代了已停止维护的 Springfox。</p>
 *
 * <h3>访问地址（服务启动后）</h3>
 * <ul>
 *   <li><b>API文档JSON</b> — http://localhost:{port}/{context-path}/v3/api-docs</li>
 *   <li><b>Swagger UI</b> — http://localhost:{port}/{context-path}/swagger-ui/index.html</li>
 * </ul>
 *
 * <h3>各服务访问示例</h3>
 * <ul>
 *   <li>商品服务 — http://localhost:8003/goods-service/swagger-ui/index.html</li>
 *   <li>订单服务 — http://localhost:8002/order-service/swagger-ui/index.html</li>
 *   <li>用户服务 — http://localhost:8001/user-service/swagger-ui/index.html</li>
 * </ul>
 *
 * <h3>常用注解</h3>
 * <ul>
 *   <li>{@link io.swagger.v3.oas.annotations.tags.Tag} — 标注 Controller 类，描述模块</li>
 *   <li>{@link io.swagger.v3.oas.annotations.Operation} — 标注方法，描述接口</li>
 *   <li>{@link io.swagger.v3.oas.annotations.Parameter} — 标注参数，描述请求参数</li>
 *   <li>{@link io.swagger.v3.oas.annotations.media.Schema} — 标注模型类/字段，描述数据结构</li>
 *   <li>{@link io.swagger.v3.oas.annotations.responses.ApiResponse} — 标注响应，描述返回值</li>
 * </ul>
 *
 * @author hong
 * @since 1.0.0
 */
@Configuration
public class SpringDocConfig {

    /**
     * OpenAPI 全局配置 Bean
     *
     * <p>配置 API 文档的基本信息，包括标题、版本、描述、联系方式等。
     * 各服务模块可以通过 application.yml 定制自己的 info 信息。</p>
     *
     * @return OpenAPI 配置实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API 信息
                .info(new Info()
                        .title("ms-demo 微服务 API 文档")
                        .description("基于 Spring Boot 3.3 + Spring Cloud 2023 的微服务学习项目接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("hong")
                                .email("hong@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                // 外部文档链接
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://github.com/hong/ms-demo"));
    }
}