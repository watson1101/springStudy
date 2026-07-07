package msdemo.hong.com.common.model.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 *
 * <p>整个项目中所有接口的返回值都使用该类进行包装，确保返回格式统一。</p>
 * <p>标准返回格式：</p>
 * <pre>
 * {
 *   "code": 200,           // 状态码
 *   "message": "success",  // 返回消息
 *   "data": {}             // 返回数据
 * }
 * </pre>
 *
 * @param <T> 返回数据的类型
 * @author hong
 * @since 1.0.0
 */
@Data
@Schema(description = "统一返回结果，所有API接口都使用此类包装返回值")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
        @Schema(description = "状态码：200-成功，其他-失败", example = "200")
    private Integer code;

    /**
     * 返回消息
     */
        @Schema(description = "提示信息", example = "操作成功")
    private String message;

    /**
     * 返回数据
     */
        @Schema(description = "返回数据")
    private T data;

    /**
     * 时间戳
     */
        @Schema(description = "时间戳（毫秒）", example = "1700000000000")
    private Long timestamp;

    /**
     * 私有构造函数，防止直接创建实例
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 私有构造函数，指定状态码和消息
     *
     * @param code    状态码
     * @param message 返回消息
     */
    private Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    /**
     * 私有构造函数，指定状态码、消息和数据
     *
     * @param code    状态码
     * @param message 返回消息
     * @param data    返回数据
     */
    private Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    // ============ 成功返回 ============

    /**
     * 成功返回，无数据
     *
     * @param <T> 返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功返回，带数据
     *
     * @param data 返回数据
     * @param <T>  返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回，指定消息
     *
     * @param message 返回消息
     * @param <T>     返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message);
    }

    /**
     * 成功返回，指定消息和数据
     *
     * @param message 返回消息
     * @param data    返回数据
     * @param <T>     返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ============ 失败返回 ============

    /**
     * 失败返回，默认错误信息
     *
     * @param <T> 返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }

    /**
     * 失败返回，指定错误信息
     *
     * @param message 错误消息
     * @param <T>     返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message);
    }

    /**
     * 失败返回，指定状态码和错误信息
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 失败返回，指定错误码
     *
     * @param resultCode 错误码枚举
     * @param <T>       返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败返回，指定错误码和数据
     *
     * @param resultCode 错误码枚举
     * @param data      返回数据
     * @param <T>       返回数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(ResultCode resultCode, T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    // ============ 判断方法 ============

    /**
     * 判断是否成功
     *
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     *
     * @return true-失败，false-成功
     */
    public boolean isError() {
        return !isSuccess();
    }
}
