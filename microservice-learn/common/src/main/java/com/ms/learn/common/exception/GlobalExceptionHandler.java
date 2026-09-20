package com.ms.learn.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.ms.learn.common.result.Result;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 —— 供各业务服务复用（位于 common，随自动配置生效）。
 * <p>统一捕获异常 → 记录异常日志（文件 + 数据库）→ 返回统一 Result 结构。</p>
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionLogRecorder recorder;
    private final Environment environment;

    public GlobalExceptionHandler(ExceptionLogRecorder recorder, Environment environment) {
        this.recorder = recorder;
        this.environment = environment;
    }

    /** 业务异常：可预期，按异常自带 code 返回（不落异常日志，避免噪声） */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        return ResponseEntity.status(e.getCode())
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    /** 参数不正确 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException e,
                                                              HttpServletRequest request) {
        record(e, request);
        return ResponseEntity.badRequest().body(Result.fail(400, e.getMessage()));
    }

    /** 兜底：未预期异常 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        record(e, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(500, "系统繁忙，请稍后重试"));
    }

    private void record(Exception e, HttpServletRequest request) {
        try {
            ExceptionLog entry = new ExceptionLog();
            entry.setId(System.currentTimeMillis() * 1000 + (long) (Math.random() * 1000));
            entry.setServiceName(environment.getProperty("spring.application.name", "unknown"));
            entry.setExceptionType(e.getClass().getName());
            entry.setMessage(e.getMessage());
            entry.setStackTrace(toStackTrace(e));
            if (request != null) {
                entry.setRequestUri(request.getRequestURI());
                entry.setRequestMethod(request.getMethod());
                entry.setRequestParams(buildParams(request));
                entry.setIp(resolveIp(request));
            }
            entry.setOccurTime(LocalDateTime.now());
            entry.setDeleted(0);
            recorder.record(entry);
        } catch (Exception ex) {
            log.warn("[exception-log] 记录异常日志时发生二次异常: {}", ex.getMessage());
        }
    }

    private String toStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String s = sw.toString();
        return s.length() > 4000 ? s.substring(0, 4000) : s;
    }

    private String buildParams(HttpServletRequest request) {
        try {
            String query = request.getQueryString();
            if (query != null && !query.isEmpty()) {
                return query;
            }
            Map<String, String[]> params = request.getParameterMap();
            if (params.isEmpty()) {
                return null;
            }
            return params.entrySet().stream()
                    .limit(50)
                    .map(en -> en.getKey() + "=" + String.join(",", en.getValue()))
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String h : headers) {
            String v = request.getHeader(h);
            if (v != null && !v.isBlank() && !"unknown".equalsIgnoreCase(v)) {
                return v.contains(",") ? v.split(",")[0].trim() : v.trim();
            }
        }
        return request.getRemoteAddr();
    }
}
