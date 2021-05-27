package com.sysu.swzl.utils;

import com.sysu.swzl.constant.WeChatConstant;
import com.sysu.swzl.vo.WxAccountResponseVo;
import io.jsonwebtoken.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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
    /**表头授权*/
    public static final String AUTHORIZATION = "Authorization";
    /**前缀*/
    public static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 根据微信用户登陆信息创建 token
     * 注 : 这里的token会被缓存到redis中,用作为二次验证
     * redis里面缓存的时间应该和jwt token的过期时间设置相同
     *
     * @param wxAccount 微信用户信息
     * @return 返回 jwt token
     */
    public String generateTokenForWxAccount(WxAccountResponseVo wxAccount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("openId", wxAccount.getOpenid());
        claims.put("sessionKey", wxAccount.getSession_key());
        // 获取token
        String token = generateToken(claims);
        String keyToken = WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + token;
        redisTemplate.opsForHash().put(keyToken, WeChatConstant.WxJwtConstant.VERIFY_KEY, wxAccount.getOpenid());
        redisTemplate.expire(keyToken, expiration, TimeUnit.SECONDS);
        return token;
    }

    /**
     * 校验token是否正确，若正确则进行续期
     *
     * @param token 密钥
     * @return 返回是否校验通过
     */
    public boolean weChatVerifyToken(String token) {
        try {
            // parse the token.
            Map<String, Object> claims = getClaimsMapFromToken(token);
            String openid = (String) claims.get("openId");
            String key = WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + token;
            String openidInRedis = (String) redisTemplate.opsForHash().get(key, WeChatConstant.WxJwtConstant.VERIFY_KEY);
            if (openid != null && openid.equals(openidInRedis)){
                // 刷新token，即更新在redis里的过期时间
                refreshWeChatToken(key, openid);
                return true;
            }
            return false;
        }catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException e) {
            throw e;
        } catch (MalformedJwtException e) {
            throw e;
        } catch (SignatureException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e){
            throw e;
        }
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
     * 刷新token
     */
    private void refreshWeChatToken(String key, String openid) {
        redisTemplate.opsForHash().put(key, WeChatConstant.WxJwtConstant.VERIFY_KEY, openid);
        redisTemplate.expire(key, expiration, TimeUnit.SECONDS);
    }
}


