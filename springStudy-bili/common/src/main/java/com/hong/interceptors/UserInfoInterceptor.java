package com.hong.interceptors;

import com.alibaba.nacos.common.utils.StringUtils;
import com.hong.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 执行到这里，说明用户肯定已经完成了登陆校验，或者不需要登陆，因此，这个拦截器处理完相关逻辑后直接放行，不进行拦截。<br/>
 * SpringMVC 的拦截器还需要配置才生效，需要新建配置类 MvcConfig ,将拦截器添加到拦截器链中。
 */


public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
// 1.获取用户登陆信息
        String userInfo = request.getHeader("user-info");
        //2.判断是否获取到了用户信息，如果获取到了则放入threadLocal
        if (StringUtils.isEmpty(userInfo)) {
            UserContext.setUser(Long.valueOf(userInfo));
        }
        // 3.无论是否获取到，放行
        return true;
    }

    /**
     * 完成用户清理
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }

}
