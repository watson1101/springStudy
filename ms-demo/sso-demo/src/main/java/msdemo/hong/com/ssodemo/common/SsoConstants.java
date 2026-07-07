package msdemo.hong.com.ssodemo.common;

/**
 * SSO单点登录常量定义类
 * 
 * <p>该类定义了SSO模块中使用的所有常量，包括：
 * <ul>
 *   <li>Redis键名前缀</li>
 *   <li>Session属性名</li>
 *   <li>令牌相关常量</li>
 *   <li>默认配置值</li>
 * </ul>
 * </p>
 */
public class SsoConstants {

    /**
     * 私有构造函数，防止实例化
     */
    private SsoConstants() {
        throw new AssertionError("Cannot instantiate utility class SsoConstants");
    }

    // ==================== Redis 键名前缀 ====================

    /**
     * SSO令牌在Redis中的键名前缀
     * 完整键名格式：sso:token:{token}
     */
    public static final String REDIS_TOKEN_KEY_PREFIX = "sso:token:";

    /**
     * 用户会话在Redis中的键名前缀
     * 完整键名格式：sso:session:{userId}
     */
    public static final String REDIS_SESSION_KEY_PREFIX = "sso:session:";

    /**
     * SSO服务注册信息在Redis中的键名前缀
     * 完整键名格式：sso:client:{clientId}
     */
    public static final String REDIS_CLIENT_KEY_PREFIX = "sso:client:";

    // ==================== Session 属性名 ====================

    /**
     * 当前登录用户信息在HttpSession中的属性名
     */
    public static final String SESSION_USER_ATTR = "sso_login_user";

    /**
     * SSO令牌在HttpSession中的属性名
     */
    public static final String SESSION_TOKEN_ATTR = "sso_token";

    // ==================== 令牌相关常量 ====================

    /**
     * JWT令牌过期时间（秒），默认2小时
     */
    public static final long TOKEN_EXPIRE_SECONDS = 7200L;

    /**
     * JWT刷新令牌过期时间（秒），默认7天
     */
    public static final long REFRESH_TOKEN_EXPIRE_SECONDS = 604800L;

    /**
     * JWT签名算法
     */
    public static final String JWT_ALGORITHM = "HS256";

    /**
     * JWT令牌类型
     */
    public static final String JWT_TOKEN_TYPE = "Bearer";

    // ==================== 请求参数名 ====================

    /**
     * SSO登录请求中令牌参数名
     */
    public static final String PARAM_TOKEN = "token";

    /**
     * SSO登录请求中回调URL参数名
     */
    public static final String PARAM_REDIRECT_URI = "redirectUri";

    /**
     * SSO登录请求中客户端ID参数名
     */
    public static final String PARAM_CLIENT_ID = "clientId";

    /**
     * SSO登录请求中用户名参数名
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * SSO登录请求中密码参数名
     */
    public static final String PARAM_PASSWORD = "password";

    // ==================== 默认配置值 ====================

    /**
     * 默认的SSO Server地址
     */
    public static final String DEFAULT_SERVER_URL = "http://localhost:8006";

    /**
     * 默认的SSO登录页面路径
     */
    public static final String DEFAULT_LOGIN_PATH = "/sso/login";

    /**
     * 默认的SSO令牌验证接口路径
     */
    public static final String DEFAULT_VALIDATE_PATH = "/sso/validate";

    /**
     * 默认的SSO客户端回调接口路径
     */
    public static final String DEFAULT_CALLBACK_PATH = "/sso/callback";

    /**
     * 默认的SSO登出接口路径
     */
    public static final String DEFAULT_LOGOUT_PATH = "/sso/logout";

    /**
     * 演示用的测试用户名
     */
    public static final String DEMO_USERNAME = "admin";

    /**
     * 演示用的测试密码
     */
    public static final String DEMO_PASSWORD = "123456";

    /**
     * 演示用的测试用户ID
     */
    public static final Long DEMO_USER_ID = 1L;

    /**
     * 演示用的测试用户昵称
     */
    public static final String DEMO_USER_NICKNAME = "管理员";

}