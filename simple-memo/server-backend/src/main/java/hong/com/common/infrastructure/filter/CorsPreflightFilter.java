package hong.com.common.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CORS 预检请求过滤器
 * 在 Servlet 容器层面拦截 OPTIONS 预检请求，
 * 在 Sa-Token 拦截器之前返回 CORS 响应头
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsPreflightFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 设置 CORS 响应头（所有请求都设置）
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");

        // 如果是 OPTIONS 预检请求，直接返回 200
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 非 OPTIONS 请求，继续执行过滤器链
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
