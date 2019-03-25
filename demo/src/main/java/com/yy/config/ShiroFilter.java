package com.yy.config;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 白名单过滤器
 */
public class ShiroFilter  extends AccessControlFilter {
    private static final Logger log= LoggerFactory.getLogger(ShiroFilter.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object object) throws Exception{
       Subject subject = getSubject(servletRequest,servletResponse);
        String url = getPathWithinApplication(servletRequest);
        log.info("当前用户正在访问的 url => " + url);
//        log.info("subject.isPermitted(url);"+subject.isPermitted(url));
        return stringRedisTemplate.hasKey("url");
    }
    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception{
        return true;
    }

}
