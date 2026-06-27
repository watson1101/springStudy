package hong.com.common.infrastructure.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果包装类
 * 所有接口返回值统一使用此类封装，格式：{code, message, data}
 *
 * @param <T> 数据类型
 * @author admin
 * @since 2026-06-22
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    private Result() {}

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 成功 ==========

    /** 成功（无数据返回） */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(), null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功（自定义消息） */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ========== 失败 ==========

    /** 失败（默认） */
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL.getCode(),
                ResultCode.FAIL.getMessage(), null);
    }

    /** 失败（自定义消息） */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAIL.getCode(), message, null);
    }

    /** 失败（状态码 + 消息） */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /** 根据 ResultCode 构造失败响应 */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /** 根据 ResultCode 构造失败响应（自定义消息覆盖） */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    // ========== 快捷判断 ==========

    /** 是否成功 */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}

