package msdemo.hong.com.ssodemo.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * <p>提供JWT令牌的生成、解析、验证功能，用于SSO单点登录中的身份认证。</p>
 * 
 * <p>主要功能：
 * <ul>
 *   <li>生成JWT令牌（包含用户信息）</li>
 *   <li>解析JWT令牌获取用户信息</li>
 *   <li>验证JWT令牌的有效性</li>
 *   <li>检查令牌是否过期</li>
 * </ul>
 * </p>
 * 
 * <p>JWT结构：
 * <ul>
 *   <li>Header: 包含令牌类型和签名算法</li>
 *   <li>Payload: 包含用户信息和过期时间</li>
 *   <li>Signature: 使用密钥对Header和Payload进行签名</li>
 * </ul>
 * </p>
 */
@Component
public class JwtUtil {

    /**
     * 默认签名密钥
     */
    private static final String DEFAULT_SECRET = "sso-demo-jwt-secret-key-2026";

    /**
     * 根据密钥字符串生成SecretKey对象
     * 
     * @param secret 密钥字符串
     * @return SecretKey对象
     */
    private SecretKey getSecretKey(String secret) {
        byte[] keyBytes = StrUtil.isNotBlank(secret) 
                ? secret.getBytes(StandardCharsets.UTF_8) 
                : DEFAULT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT令牌
     * 
     * @param userDTO 用户信息对象
     * @param secret 签名密钥
     * @param expireSeconds 过期时间（秒）
     * @return JWT令牌字符串
     */
    public String generateToken(SsoUserDTO userDTO, String secret, long expireSeconds) {
        // 设置令牌过期时间
        Date expireDate = new Date(System.currentTimeMillis() + expireSeconds * 1000L);

        // 设置JWT Payload中的自定义字段
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDTO.getUserId());
        claims.put("username", userDTO.getUsername());
        claims.put("nickname", userDTO.getNickname());
        claims.put("email", userDTO.getEmail());
        claims.put("phone", userDTO.getPhone());
        claims.put("roles", userDTO.getRoles());
        claims.put("systemId", userDTO.getSystemId());

        // 生成JWT令牌
        return Jwts.builder()
                .claims(claims)
                .subject(userDTO.getUsername())
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(getSecretKey(secret))
                .compact();
    }

    /**
     * 使用默认配置生成JWT令牌
     * 
     * @param userDTO 用户信息对象
     * @return JWT令牌字符串
     */
    public String generateToken(SsoUserDTO userDTO) {
        return generateToken(userDTO, DEFAULT_SECRET, SsoConstants.TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 解析JWT令牌获取Claims对象
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return Claims对象，包含令牌中的所有声明
     * @throws io.jsonwebtoken.JwtException 如果令牌无效或过期
     */
    public Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSecretKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 使用默认密钥解析JWT令牌
     * 
     * @param token JWT令牌字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        return parseToken(token, DEFAULT_SECRET);
    }

    /**
     * 从JWT令牌中提取用户信息
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return SsoUserDTO用户信息对象
     */
    public SsoUserDTO extractUser(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return SsoUserDTO.builder()
                .userId(claims.get("userId", Long.class))
                .username(claims.get("username", String.class))
                .nickname(claims.get("nickname", String.class))
                .email(claims.get("email", String.class))
                .phone(claims.get("phone", String.class))
                .roles(claims.get("roles", String.class))
                .systemId(claims.get("systemId", String.class))
                .build();
    }

    /**
     * 使用默认密钥从JWT令牌中提取用户信息
     * 
     * @param token JWT令牌字符串
     * @return SsoUserDTO用户信息对象
     */
    public SsoUserDTO extractUser(String token) {
        return extractUser(token, DEFAULT_SECRET);
    }

    /**
     * 验证JWT令牌是否有效（未过期且签名正确）
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return true表示令牌有效，false表示无效或过期
     */
    public boolean validateToken(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用默认密钥验证JWT令牌
     * 
     * @param token JWT令牌字符串
     * @return true表示令牌有效
     */
    public boolean validateToken(String token) {
        return validateToken(token, DEFAULT_SECRET);
    }

    /**
     * 检查JWT令牌是否过期
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return true表示已过期，false表示未过期
     */
    public boolean isTokenExpired(String token, String secret) {
        try {
            Claims claims = parseToken(token, secret);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取JWT令牌的过期时间
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return 过期时间Date对象
     */
    public Date getExpiration(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getExpiration();
    }

    /**
     * 获取JWT令牌的签发时间
     * 
     * @param token JWT令牌字符串
     * @param secret 签名密钥
     * @return 签发时间Date对象
     */
    public Date getIssuedAt(String token, String secret) {
        Claims claims = parseToken(token, secret);
        return claims.getIssuedAt();
    }

    /**
     * 将用户信息对象序列化为JSON字符串
     * 
     * @param userDTO 用户信息对象
     * @return JSON字符串
     */
    public String serializeUser(SsoUserDTO userDTO) {
        return JSONUtil.toJsonStr(userDTO);
    }

    /**
     * 将JSON字符串反序列化为用户信息对象
     * 
     * @param json JSON字符串
     * @return SsoUserDTO用户信息对象
     */
    public SsoUserDTO deserializeUser(String json) {
        return JSONUtil.toBean(json, SsoUserDTO.class);
    }

}