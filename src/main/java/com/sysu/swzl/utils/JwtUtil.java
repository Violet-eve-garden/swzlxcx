package com.sysu.swzl.utils;

import com.sysu.swzl.constant.WeChatConstant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * JwtToken生成的工具类
 * 规定前缀操作都在这里完成
 * @author 49367
 * @date 2021/5/23 16:06
 */
@Component
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    /**小于该时间的token不可用，应该被刷新*/
    @Value("${jwt.advance_expire_time}")
    private Long advanceExpiration;
    @Value("${jwt.redis_exp}")
    private Long redisExp;
    /**表头授权*/
    public static final String AUTHORIZATION = "Authorization";
    /**前缀*/
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据微信用户登陆信息创建 token
     * 注 : 这里的token会被缓存到redis中,用作为二次验证
     * redis里面缓存的过期时间较长，因为如果token未过期，redis中的token必须保证还存在
     *
     * @return 返回 jwt token
     */
    public String generateTokenForWxAccount(String openId, String sessionKey) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("openId", openId);
        claims.put("sessionKey", sessionKey);
        // 获取token
        String token = generateToken(claims);
        String keyToken = WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + token;
        redisTemplate.opsForHash().put(keyToken, WeChatConstant.WxJwtConstant.VERIFY_KEY, openId);
        redisTemplate.expire(keyToken, redisExp, TimeUnit.SECONDS);
        return token;
    }

    /**
     * 校验token是否正确
     *
     * @param token 密钥
     * @return 如果token是正确的，则返回最新的可用的token，否则返回null
     */
    public String weChatVerifyToken(String token) {
        try {
            if (!StringUtils.hasText(token))
                return null;
            // parse the token.
            // getClaimsMapFromToken如果token过期则返回null
            Map<String, Object> claims = getClaimsMapFromToken(token);
            if (claims == null)
                return null;
            LOGGER.info("claims is not null "  + claims.toString());
            String openId = (String) claims.get("openId");
            String sessionKey = (String) claims.get("sessionKey");

            String keyToken = WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + token;
            String openIdInRedis = (String) redisTemplate.opsForHash().get(keyToken, WeChatConstant.WxJwtConstant.VERIFY_KEY);
            LOGGER.info("openId in redis: " + openIdInRedis);
            // 校验token对应的信息是否合法
            if (!StringUtils.hasText(openId) || !StringUtils.hasText(sessionKey) || !openId.equals(openIdInRedis))
                return null;

            // 判断当前token能否继续使用(需不需要更新)
            boolean expValid = isExpValid(token);
            if (expValid)
                return token;

            // 删除redis中的旧token
            redisTemplate.delete(keyToken);
            String newToken = generateTokenForWxAccount(openId, sessionKey);
            LOGGER.info("更新token: " + newToken);
            // 返回新token
            return newToken;
        }catch (Exception e) {
            LOGGER.info(e.getMessage());
        }

        return null;
    }

    /**
     * 根据负责生成JWT的token
     */
    private String generateToken(Map<String, Object> claims) {
        // 加密算法进行签名得到token
        return TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从token中获取JWT中的负载, 以Map的形式返回
     */
    private Map<String, Object> getClaimsMapFromToken(String token) {
        Map<String, Object> body = null;
        try {
            body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
        } catch (Exception e) {
            // token过期也会抛出异常
            LOGGER.info(e.getMessage());
            LOGGER.info("JWT格式验证失败:{}",token);
        }
        return body;
    }

    /**
     * 获取Claims
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
        } catch (Exception e) {
            LOGGER.info("JWT格式验证失败:{}",token);
        }
        return claims;
    }

    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 返回token里的openId
     * @param token
     * @return
     */
    public String getOpenIdFromToken(String token){
        Map<String, Object> claims = getClaimsMapFromToken(token);
        return (String) claims.get("openId");
    }


    /**
     * 判断是否可以继续使用
     * @param authToken
     * @return
     */
    private boolean isExpValid(String authToken) {
        Claims claims = getClaimsFromToken(authToken);
        if (claims == null)
            return false;
        // 如果有效时间小于advanceExpiration，则该token已经不可用
        LOGGER.info("survival time : " + (claims.getExpiration().getTime() - System.currentTimeMillis()));
        return claims.getExpiration().getTime() - System.currentTimeMillis() > advanceExpiration * 1000;
    }
}


