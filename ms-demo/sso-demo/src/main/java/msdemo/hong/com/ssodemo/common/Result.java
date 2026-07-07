package msdemo.hong.com.ssodemo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 统一返回结果类
 * 
 * <p>用于SSO模块中所有接口的统一响应格式，便于前端处理和错误排查。</p>
 * 
 * <p>响应结构：
 * <ul>
 *   <li>code: 状态码，200表示成功，其他表示失败</li>
 *   <li>message: 提示信息</li>
 *   <li>data: 业务数据</li>
 *   <li>timestamp: 响应时间戳</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 成功状态码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败状态码
     */
    public static final int ERROR_CODE = 500;

    /**
     * 未授权状态码
     */
    public static final int UNAUTHORIZED_CODE = 401;

    /**
     * 参数错误状态码
     */
    public static final int BAD_REQUEST_CODE = 400;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 响应时间戳（毫秒）
     */
    private long timestamp;

    /**
     * 创建成功响应
     * 
     * @param data 业务数据
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message("success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建成功响应（带自定义消息）
     * 
     * @param data 业务数据
     * @param message 提示信息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data, String message) {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(SUCCESS_CODE)
                .message("success")
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败响应
     * 
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> error(String message) {
        return Result.<T>builder()
                .code(ERROR_CODE)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败响应（带自定义状态码）
     * 
     * @param code 状态码
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> error(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建未授权响应
     * 
     * @param message 提示信息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> unauthorized(String message) {
        return Result.<T>builder()
                .code(UNAUTHORIZED_CODE)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建参数错误响应
     * 
     * @param message 错误信息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> badRequest(String message) {
        return Result.<T>builder()
                .code(BAD_REQUEST_CODE)
                .message(message)
                .data(null)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 判断响应是否成功
     * 
     * @return true表示成功
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }

    /**
     * 获取响应时间（LocalDateTime格式）
     * 
     * @return LocalDateTime时间对象
     */
    public LocalDateTime getDateTime() {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(this.timestamp),
                ZoneId.systemDefault()
        );
    }

}