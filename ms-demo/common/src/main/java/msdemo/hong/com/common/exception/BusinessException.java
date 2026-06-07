package msdemo.hong.com.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 *
 * <p>用于封装业务逻辑中的异常情况，如参数校验失败、数据不存在等</p>
 * <p>与系统异常（如空指针、数组越界等）不同，业务异常是可预知、可处理的异常</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 构造函数 - 指定错误码
     *
     * @param resultCode 错误码枚举
     */
    public BusinessException(msdemo.hong.com.common.model.result.ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数 - 指定错误码和自定义消息
     *
     * @param resultCode 错误码枚举
     * @param message    自定义错误消息
     */
    public BusinessException(msdemo.hong.com.common.model.result.ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造函数 - 指定错误码和消息
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
