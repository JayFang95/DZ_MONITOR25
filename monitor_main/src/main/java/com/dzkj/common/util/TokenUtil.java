package com.dzkj.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description token生成管理
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class TokenUtil {

    /**
     * 密钥
     */
    private static final String SECRET = "dz_monitor_secret";
    /**
     * 访问令牌过期时间： 毫秒
     */
    private static final long EXPIRATION = 6*60*60*1000;

    /**
     * 刷新令牌过期时间： 毫秒
     */
    private static final long REFRESH_EXPIRATION = 7*24*60*60*1000;

    /**
     * 会话时间 秒
     */
    private static final long SESSION_TIME = 3*60*60;

    /**
     * 请求头 key值
     */
    private static final String HEADER ="token";

    /**
     * 刷新令牌接口
     */
    private static final String REFRESH_URI = "/mt/auth/refresh_token";

    public static String getSecret() {
        return SECRET;
    }

    public static long getExpiration() {
        return EXPIRATION;
    }

    public static long getRefreshExpiration() {
        return REFRESH_EXPIRATION;
    }

    public static long getSessionTime() {
        return SESSION_TIME;
    }

    public static String getHeader() {
        return HEADER;
    }

    public static String getRefreshUri() {
        return REFRESH_URI;
    }

    /**
     * 生成token令牌
     *
     * @param username 用户
     * @return 令token牌
     */
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", username);
        //单位为秒
        claims.put("created", System.currentTimeMillis() / 1000);

        return generateToken(claims, EXPIRATION);
    }

    /**
     * 生成refresh_token令牌
     *
     * @param username 用户
     * @return 令refresh_token牌
     */
    public static String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", username);
        claims.put("created", System.currentTimeMillis() / 1000);

        return generateToken(claims, REFRESH_EXPIRATION);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("created", System.currentTimeMillis() / 1000);
            refreshedToken = generateToken(claims, EXPIRATION);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {

        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    /**
     * 从claims生成令牌,如果看不懂就看谁调用它
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private static String generateToken(Map<String, Object> claims, long expirationTime) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 从令牌中获取数据声明,如果看不懂就看谁调用它
     *
     * @param token 令牌
     * @return 数据声明
     */
    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }


}

