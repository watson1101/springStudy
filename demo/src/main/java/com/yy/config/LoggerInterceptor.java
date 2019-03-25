package com.yy.config;

public class LoggerInterceptor {

    //请求开始时间标记
    private static final String LOGGER_SEND_TIME = "_send_time";
    //请求日志实体标记
    private static final String LOGGER_ENTITY = "_logger_entity";

    //调用请求的时候执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //创建请求实体
        LoggerEntity loggerEntity = new LoggerEntity();
        //获取请求的sessionId
        String sessionId = request.getRequestedSessionId();
        //请求的路径
        String path = request.getRequestURI();
        //获取请求参数信息
        String paramData = JSON.toJSONString(request.getParameterMap(), SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue);
        //设置客户端ip
        loggerEntity.setIp(LoggerUtils.getCliectIp(request));
        //设置请求方法
        loggerEntity.setMethod(request.getMethod());
        //设置请求类型
        loggerEntity.setType(LoggerUtils.getRequestType(request));
        //设置请求参数的json字符串
        loggerEntity.setParamData(paramData);
        //设置请求地址
        loggerEntity.setUri(path);
        //设置sessionId
        loggerEntity.setSessionId(sessionId);
        //设置请求开始时间
        request.setAttribute(LOGGER_SEND_TIME, System.currentTimeMillis());
        //设置请求实体到request中，方便after调用
        request.setAttribute(LOGGER_ENTITY, loggerEntity);
        return true;
    }

    //controller调用之后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    //viewResolve返回view到前台之前执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //获取请求错误码
        int status = response.getStatus();
        //当前时间
        long currentTime = System.currentTimeMillis();
        //请求开始时间
        long time = Long.parseLong(request.getAttribute(LOGGER_SEND_TIME).toString());
        //获取本次请求日志实体
        LoggerEntity loggerEntity = (LoggerEntity) request.getAttribute(LOGGER_ENTITY);
        //设置请求时间差
        loggerEntity.setTimeConsuming(Integer.valueOf(String.valueOf((currentTime - time))));
        //设置返回时间
        loggerEntity.setReturnTime(String.valueOf(currentTime));
        //设置返回错误码
        loggerEntity.setHttpStatusCode(String.valueOf(status));
        //设置返回值
        loggerEntity.setReturnData(JSON.toJSONString(LoggerUtils.LOGGER_RETURN, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue));
        //将日志写入到数据库
        LoggerJpa loggerJpa = getDAO(LoggerJpa.class, request);
        loggerJpa.save(loggerEntity);
        System.out.println("写入成功");
    }

    //我们在拦截器内无法通过SpringBean的方式注入LoggerJPA，我只能通过另外一种形式。
    //WebApplicationContextUtils
    //这个工具类可以通过HttpServletRequest请求对象的上下文（ServetCotext）获取Spring管理的Bean
    /*
    创建了一个getDAO的方法，方法需要传入一个实体的类型，以及一个HttpServetRequest请求对象，
    通过WebApplicationContextUtils内部的getRequiredWebApplicationContext方法获取到BeanFactory（实体工厂类）
    ，从而通过工厂实体的getBean方法就可以拿到SpringDataJPA为我们管理的LoggerJPA持久化数据接口实例。
     */
    private <T> T getDAO(Class<T> clazz, HttpServletRequest request) {
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return factory.getBean(clazz);

    }
}