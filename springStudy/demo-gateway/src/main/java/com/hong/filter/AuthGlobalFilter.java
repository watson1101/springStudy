package com.hong.filter;

import com.hong.util.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.hong.config.AuthProperties;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtTool jwtTool;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取 request
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断是否需要做登录拦截
        if (isInclude(request.getPath().toString())) {
        // 放行
            return chain.filter(exchange);
        }
        List<String> excludePaths = authProperties.getExcludePaths();
        // 3.获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }
        // 4.校验和解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (IllegalArgumentException e) {
            // 401 未授权
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);// 也可以直接设置401
            // 后续所有拦截器将不在执行，请求不会再转发
            return response.setComplete();
        }
        // 5.传递用户信息
        log.info("userId: {}", userId);
        String userInfo = String.valueOf(userId);
        ServerWebExchange serverWebExchange = exchange.mutate().request(builder -> builder.header("user-info", userInfo)).build();
        // 6.放行
        return chain.filter(serverWebExchange);
    }

    private boolean isInclude(String path) {
        for (String pathPatten : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPatten, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
