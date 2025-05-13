package com.dzkj.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.TokenUtil;
import com.dzkj.entity.system.Online;
import com.dzkj.service.system.IOnlineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 请求拦截认证过滤器
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Configuration
@Slf4j
public class CustomAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailService userDetailService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IOnlineService onlineService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(TokenUtil.getHeader());
        String requestUri = request.getRequestURI();
        if(isPermitUri(requestUri)){
            filterChain.doFilter(request, response);
            return;
        }
        if(StringUtils.isNotEmpty(token) && (StringUtils.countMatches(token, ".") == 2)){
            String decoded = new String(Base64.decodeBase64(StringUtils.substringBetween(token, ".")));
            Map properties = new ObjectMapper().readValue(decoded, Map.class);
            String username = (String) properties.get("sub");
            int exp = (int) properties.get("exp");
            //请求超时验证
            String clientSessionTimeKey = RedisConstant.PREFIX + username + "_" + token + "_time";
            Date date = (Date) redisTemplate.opsForValue().get(clientSessionTimeKey);
            if (date != null) {
                long time = (System.currentTimeMillis() - date.getTime()) / 1000;
                if (time > TokenUtil.getSessionTime()) {
                    cleanInfo(response, token, username, "session_time_out", "连接超时, 请重新登录");
                    return;
                }
            }else {
                cleanInfo(response, token, username, "session_time_out", "连接超时, 请重新登录");
                return;
            }
            //token 有效性验证
            Set<Object> tokenSet = redisTemplate.opsForSet().members(RedisConstant.PREFIX + username);
            if(tokenSet!=null){
                for (Object redisToken: tokenSet) {
                    if(TokenUtil.isTokenExpired((String) redisToken)){
                        redisTemplate.opsForSet().remove(RedisConstant.PREFIX + username, redisToken);
                    }
                }
            }
            tokenSet = redisTemplate.opsForSet().members(RedisConstant.PREFIX + username);
            if (tokenSet == null || !tokenSet.contains(token)) {
                //传递的token未过期
                if (!TokenUtil.isTokenExpired(token)) {
                    cleanInfo(response, token, username, "logout", "当前账号已过期或已退出登录");
                }else {
                    //token过期，但在刷新时间内
                    if (DateUtil.getDate().getTime() / 1000 <= exp + 60 * 3) {
                        response.setStatus(401);
                        log.info("开始进行刷新动作...");
                    }else { // 超出刷新时间
                        cleanInfo(response, token, username, "logout", "当前账号已过期或已退出登录");
                    }
                }
                return;
            }

            redisTemplate.opsForValue().set(clientSessionTimeKey, new Date(), TokenUtil.getSessionTime(), TimeUnit.SECONDS);
            //取消缓存时需要重新保存认证信息（配置中可能设置了不应用session）
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                if (TokenUtil.validateToken(token, userDetails)) {
                    //给使用该JWT令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }else {
            log.info("token 不存在或者无效");
            response.setStatus(401);
            response.setHeader("msg", "no-auth");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 数据清理
     */
    private void cleanInfo(HttpServletResponse response, String token, String username, String head, String msg) {
        deleteOnline(token);
        deleteRedis(username, token);
        response.setStatus(401);
        response.setHeader("msg", head);
        log.error(msg);
    }

    /**
     * 放行url
     */
    private boolean isPermitUri(String requestUri) {
        return requestUri.contains("/mt/auth")
                || requestUri.contains("/mt/wx")
                || requestUri.contains("/mt/common/ws")
                || requestUri.contains("/mt/common/download")
                || requestUri.contains("/mt/common/data/other/download")
                || requestUri.contains("/mt/common/home_page")
                || requestUri.contains("/mt/common/login_page")
                || requestUri.contains("/mt/common/app")
                || requestUri.contains("/mt/common/surveyControl/survey/vw")
                || requestUri.contains("/websocket");
    }

    /**
     * 删除指定在线用户, 并给出socket提示
     */
    private void deleteOnline(String token){
        // 用户下线，更新在线表
        LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Online::getToken, token);
        onlineService.remove(wrapper);
    }

    /**
     * 清除redis 记录
     */
    private void deleteRedis(String username, String token) {
        redisTemplate.delete(RedisConstant.PREFIX + username + "_" + token + "_time");
        redisTemplate.opsForSet().remove(RedisConstant.PREFIX + username, token);
        LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Online::getUsername, username).ne(Online::getToken, token);
        if (onlineService.count() == 0){
            String menuKey = RedisConstant.PREFIX + username + "_menu";
            redisTemplate.opsForList().getOperations().expire(menuKey, 0, TimeUnit.MILLISECONDS);
        }
    }

}
