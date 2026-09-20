package com.ms.learn.common.exception;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 异常日志实体 —— 用于异常信息落库。
 * <p>学习点：全局异常捕获后，把异常的关键上下文持久化，便于事后排查。</p>
 */
@Data
public class ExceptionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（雪花算法，应用层生成） */
    private Long id;

    /** 链路追踪 ID */
    private String traceId;

    /** 服务名 */
    private String serviceName;

    /** 异常全类名 */
    private String exceptionType;

    /** 异常消息 */
    private String message;

    /** 异常堆栈 */
    private String stackTrace;

    /** 请求 URI */
    private String requestUri;

    /** 请求方法 */
    private String requestMethod;

    /** 请求参数 */
    private String requestParams;

    /** 用户 ID */
    private String userId;

    /** 客户端 IP */
    private String ip;

    /** 异常发生时间 */
    private LocalDateTime occurTime;

    /** 软删除标记：0 未删除，1 已删除 */
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
