package msdemo.hong.com.common.model.result;

import lombok.Getter;

/**
 * 返回状态码枚举
 *
 * <p>定义系统中所有可能的返回状态码，确保状态码的统一管理。</p>
 *
 * @author hong
 * @since 1.0.0
 */
@Getter
public enum ResultCode {

    // ============ 通用状态码 (1xxx) ============
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    SERVER_ERROR(500, "服务器内部错误"),

    // ============ 用户相关 (10xxx) ============
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_PASSWORD_ERROR(10002, "用户密码错误"),
    USER_ACCOUNT_LOCKED(10003, "用户账号已锁定"),
    USER_ACCOUNT_EXPIRED(10004, "用户账号已过期"),
    USER_CREDENTIALS_EXPIRED(10005, "用户密码已过期"),
    USER_DISABLED(10006, "用户账号已禁用"),
    USER_ALREADY_EXISTS(10007, "用户已存在"),
    USER_REGISTER_ERROR(10008, "用户注册失败"),
    USER_LOGIN_SUCCESS(10009, "用户登录成功"),
    USER_LOGOUT_SUCCESS(10010, "用户退出成功"),

    // ============ 商品相关 (20xxx) ============
    PRODUCT_NOT_FOUND(20001, "商品不存在"),
    PRODUCT_STOCK_ERROR(20002, "商品库存不足"),
    PRODUCT_PRICE_ERROR(20003, "商品价格变动"),
    PRODUCT_OFFLINE(20004, "商品已下架"),
   _PRODUCT_DELETED(20005, "商品已删除"),
    PRODUCT_CREATE_ERROR(20006, "商品创建失败"),
    PRODUCT_UPDATE_ERROR(20007, "商品更新失败"),
    PRODUCT_DELETE_ERROR(20008, "商品删除失败"),
    PRODUCT_CODE_EXISTS(20009, "商品编码已存在"),

    // ============ 订单相关 (30xxx) ============
    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_CREATE_ERROR(30002, "订单创建失败"),
    ORDER_PAY_ERROR(30003, "订单支付失败"),
    ORDER_CANCEL_ERROR(30004, "订单取消失败"),
    ORDER_STATUS_ERROR(30005, "订单状态错误"),
    ORDER_TIMEOUT(30006, "订单已超时"),
    ORDER_PAID(30007, "订单已支付"),

    // ============ 权限相关 (40xxx) ============
    PERMISSION_DENIED(40001, "权限不足"),
    PERMISSION_NOT_FOUND(40002, "权限不存在"),
    ROLE_NOT_FOUND(40003, "角色不存在"),
    ROLE_ALREADY_EXISTS(40004, "角色已存在"),
    UNAUTHORIZED(40005, "未授权，请先登录"),
    TOKEN_INVALID(40006, "令牌无效"),
    TOKEN_EXPIRED(40007, "令牌已过期"),

    // ============ 文件相关 (50xxx) ============
    FILE_UPLOAD_ERROR(50001, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(50002, "文件下载失败"),
    FILE_NOT_FOUND(50003, "文件不存在"),
    FILE_TYPE_ERROR(50004, "文件类型不支持"),
    FILE_SIZE_ERROR(50005, "文件大小超限"),

    // ============ 业务相关 (60xxx) ============
    BUSINESS_ERROR(60001, "业务处理失败"),
    DATA_NOT_FOUND(60002, "数据不存在"),
    DATA_ALREADY_EXISTS(60003, "数据已存在"),
    DATA_DELETE_ERROR(60004, "数据删除失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态信息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 状态信息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
