package com.ms.learn.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 异常日志落库写入器 —— 使用 JdbcTemplate 直写，避免依赖各服务的 Mapper 扫描配置。
 * <p>表名固定为 exception_log，各服务在自己业务库中建同名表。</p>
 */
@Slf4j
@Component
public class ExceptionLogDbWriter {

    /** 异常日志表名（固定） */
    private static final String TABLE_NAME = "exception_log";

    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " "
                    + "(id, trace_id, service_name, exception_type, message, stack_trace, "
                    + " request_uri, request_method, request_params, user_id, ip, occur_time, deleted, create_time, update_time) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public ExceptionLogDbWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(ExceptionLog e) {
        LocalDateTime occur = e.getOccurTime() == null ? LocalDateTime.now() : e.getOccurTime();
        Timestamp now = Timestamp.valueOf(occur);

        jdbcTemplate.update(INSERT_SQL,
                e.getId(),
                e.getTraceId(),
                e.getServiceName(),
                e.getExceptionType(),
                truncate(e.getMessage(), 1000),
                e.getStackTrace(),
                truncate(e.getRequestUri(), 500),
                e.getRequestMethod(),
                e.getRequestParams(),
                e.getUserId(),
                truncate(e.getIp(), 64),
                now, now, now);
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
