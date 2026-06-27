package hong.com.common.infrastructure.result;

import lombok.Getter;

/**
 * 统一返回状态码枚举
 * 定义系统全局状态码，遵循 HTTP 语义扩展
 *
 * @author admin
 * @since 2026-06-22
 */
@Getter
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 失败 */
    FAIL(400, "操作失败"),

    /** 未认证 */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /** 无权限 */
    FORBIDDEN(403, "没有操作权限"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 请求超时 */
    TIMEOUT(408, "请求超时"),

    /** 参数错误 */
    PARAM_ERROR(422, "参数错误"),

    /** 业务异常 */
    BUSINESS_ERROR(500, "业务处理异常"),

    /** 服务器内部错误 */
    INTERNAL_ERROR(500, "服务器内部错误"),

    /** 用户名或密码错误 */
    LOGIN_FAIL(401, "用户名或密码错误"),

    /** 账号已锁定 */
    ACCOUNT_LOCKED(403, "账号已被锁定"),

    /** 验证码错误 */
    CAPTCHA_ERROR(422, "验证码错误"),

    /** 用户已存在 */
    USER_EXIST(409, "用户已存在"),

    /** 重复操作 */
    REPEAT_OPERATION(429, "操作过于频繁");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
