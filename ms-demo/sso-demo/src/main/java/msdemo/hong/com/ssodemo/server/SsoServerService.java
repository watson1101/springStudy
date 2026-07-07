package msdemo.hong.com.ssodemo.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.ssodemo.common.JwtUtil;
import msdemo.hong.com.ssodemo.common.Result;
import msdemo.hong.com.ssodemo.common.SsoConstants;
import msdemo.hong.com.ssodemo.common.SsoProperties;
import msdemo.hong.com.ssodemo.common.SsoUserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * SSO Server服务层
 * 
 * <p>负责处理SSO身份提供者（IdP）的核心业务逻辑，包括：
 * <ul>
 *   <li>用户认证（验证用户名密码）</li>
 *   <li>JWT令牌生成与签发</li>
 *   <li>令牌存储与管理（Redis）</li>
 *   <li>令牌验证</li>
 *   <li>令牌失效（登出）</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SsoServerService {

    /**
     * JWT工具类
     */
    private final JwtUtil jwtUtil;

    /**
     * Redis模板
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * SSO配置属性
     */
    private final SsoProperties ssoProperties;

    /**
     * 验证用户凭据
     * 
     * <p>模拟用户认证过程，验证用户名和密码是否正确。
     * 在实际项目中，此处应调用用户服务进行数据库验证。</p>
     * 
     * @param username 用户名
     * @param password 密码
     * @return 验证结果，成功返回true，失败返回false
     */
    public boolean validateCredentials(String username, String password) {
        log.info("验证用户凭据: username={}", username);
        
        if (SsoConstants.DEMO_USERNAME.equals(username) 
                && SsoConstants.DEMO_PASSWORD.equals(password)) {
            log.info("用户凭据验证成功: username={}", username);
            return true;
        }
        
        log.warn("用户凭据验证失败: username={}", username);
        return false;
    }

    /**
     * 生成并签发SSO令牌
     * 
     * <p>步骤：
     * 1. 验证用户凭据
     * 2. 构建用户信息对象
     * 3. 生成JWT令牌
     * 4. 将令牌和用户信息存储到Redis
     * 5. 返回令牌信息</p>
     * 
     * @param username 用户名
     * @param password 密码
     * @return 包含令牌的结果对象，如果验证失败返回错误信息
     */
    public Result<String> issueToken(String username, String password) {
        log.info("签发SSO令牌: username={}", username);
        
        // 1. 验证用户凭据
        if (!validateCredentials(username, password)) {
            return Result.error("用户名或密码错误");
        }
        
        // 2. 构建用户信息对象
        SsoUserDTO userDTO = SsoUserDTO.createDemoUser(username);
        
        // 3. 生成JWT令牌
        String token = jwtUtil.generateToken(
                userDTO, 
                ssoProperties.getServer().getJwtSecret(),
                ssoProperties.getServer().getTokenExpireSeconds()
        );
        
        // 4. 将令牌和用户信息存储到Redis
        if (ssoProperties.getServer().isPersistToken()) {
            saveTokenToRedis(token, userDTO);
            log.info("令牌已存储到Redis: token={}", token.substring(0, 20) + "...");
        }
        
        log.info("SSO令牌签发成功: username={}, userId={}", username, userDTO.getUserId());
        return Result.success(token, "登录成功");
    }

    /**
     * 将令牌和用户信息存储到Redis
     * 
     * @param token JWT令牌
     * @param userDTO 用户信息
     */
    private void saveTokenToRedis(String token, SsoUserDTO userDTO) {
        long expireSeconds = ssoProperties.getServer().getTokenExpireSeconds();
        
        // 存储令牌 -> 用户信息映射
        String tokenKey = SsoConstants.REDIS_TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(
                tokenKey, 
                jwtUtil.serializeUser(userDTO), 
                expireSeconds, 
                TimeUnit.SECONDS
        );
        
        // 存储用户ID -> 令牌映射（用于登出时根据用户ID查找令牌）
        String sessionKey = SsoConstants.REDIS_SESSION_KEY_PREFIX + userDTO.getUserId();
        redisTemplate.opsForValue().set(
                sessionKey, 
                token, 
                expireSeconds, 
                TimeUnit.SECONDS
        );
    }

    /**
     * 验证SSO令牌
     * 
     * <p>验证令牌的有效性，包括：
     * 1. JWT签名验证
     * 2. 令牌过期检查
     * 3. Redis中令牌存在性检查</p>
     * 
     * @param token JWT令牌
     * @return 验证结果，成功返回用户信息，失败返回错误信息
     */
    public Result<SsoUserDTO> validateToken(String token) {
        log.info("验证SSO令牌: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        
        // 1. 参数校验
        if (token == null || token.isEmpty()) {
            return Result.unauthorized("令牌不能为空");
        }
        
        try {
            // 2. JWT签名验证和过期检查
            boolean isValid = jwtUtil.validateToken(token, ssoProperties.getServer().getJwtSecret());
            if (!isValid) {
                return Result.unauthorized("令牌无效或已过期");
            }
            
            // 3. Redis中令牌存在性检查（如果启用了持久化）
            if (ssoProperties.getServer().isPersistToken()) {
                String tokenKey = SsoConstants.REDIS_TOKEN_KEY_PREFIX + token;
                String userJson = redisTemplate.opsForValue().get(tokenKey);
                if (userJson == null) {
                    return Result.unauthorized("令牌已失效，请重新登录");
                }
            }
            
            // 4. 提取用户信息
            SsoUserDTO userDTO = jwtUtil.extractUser(token, ssoProperties.getServer().getJwtSecret());
            log.info("令牌验证成功: username={}, userId={}", userDTO.getUsername(), userDTO.getUserId());
            
            return Result.success(userDTO, "令牌验证成功");
            
        } catch (Exception e) {
            log.error("令牌验证失败: {}", e.getMessage());
            return Result.unauthorized("令牌验证失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID验证令牌
     * 
     * @param userId 用户ID
     * @param token JWT令牌
     * @return 验证结果
     */
    public Result<SsoUserDTO> validateTokenByUserId(Long userId, String token) {
        log.info("根据用户ID验证令牌: userId={}", userId);
        
        if (userId == null) {
            return Result.badRequest("用户ID不能为空");
        }
        
        // 查询用户对应的令牌
        String sessionKey = SsoConstants.REDIS_SESSION_KEY_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(sessionKey);
        
        if (storedToken == null) {
            return Result.unauthorized("用户未登录或会话已过期");
        }
        
        // 验证令牌是否匹配
        if (!storedToken.equals(token)) {
            return Result.unauthorized("令牌不匹配");
        }
        
        // 验证令牌有效性
        return validateToken(token);
    }

    /**
     * 使令牌失效（登出）
     * 
     * <p>从Redis中删除令牌相关数据，使令牌失效。</p>
     * 
     * @param token JWT令牌
     * @return 操作结果
     */
    public Result<Void> invalidateToken(String token) {
        log.info("使令牌失效: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        
        if (token == null || token.isEmpty()) {
            return Result.badRequest("令牌不能为空");
        }
        
        try {
            // 1. 从令牌中提取用户ID
            SsoUserDTO userDTO = jwtUtil.extractUser(token, ssoProperties.getServer().getJwtSecret());
            
            // 2. 删除令牌 -> 用户信息映射
            String tokenKey = SsoConstants.REDIS_TOKEN_KEY_PREFIX + token;
            redisTemplate.delete(tokenKey);
            
            // 3. 删除用户ID -> 令牌映射
            String sessionKey = SsoConstants.REDIS_SESSION_KEY_PREFIX + userDTO.getUserId();
            redisTemplate.delete(sessionKey);
            
            log.info("令牌失效成功: username={}, userId={}", userDTO.getUsername(), userDTO.getUserId());
            return Result.success(null, "登出成功");
            
        } catch (Exception e) {
            log.error("令牌失效失败: {}", e.getMessage());
            return Result.error("令牌失效失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID使令牌失效
     * 
     * <p>用于强制用户下线的场景。</p>
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    public Result<Void> invalidateTokenByUserId(Long userId) {
        log.info("根据用户ID使令牌失效: userId={}", userId);
        
        if (userId == null) {
            return Result.badRequest("用户ID不能为空");
        }
        
        // 查询用户对应的令牌
        String sessionKey = SsoConstants.REDIS_SESSION_KEY_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(sessionKey);
        
        if (token == null) {
            return Result.error("用户未登录或会话已过期");
        }
        
        // 使令牌失效
        return invalidateToken(token);
    }

    /**
     * 获取用户信息
     * 
     * @param token JWT令牌
     * @return 用户信息
     */
    public Result<SsoUserDTO> getUserInfo(String token) {
        log.info("获取用户信息: token={}", token != null ? token.substring(0, 20) + "..." : "null");
        return validateToken(token);
    }

}