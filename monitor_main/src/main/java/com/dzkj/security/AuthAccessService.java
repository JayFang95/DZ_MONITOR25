package com.dzkj.security;

import com.dzkj.biz.vo.Permission;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 访问控制服务
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Service("authAccessService")
@Slf4j
public class AuthAccessService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 访问权限验证
     */
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) throws IOException {
        //anonymousUser
        String username = "";
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader(TokenUtil.getHeader());
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            if (StringUtils.isNoneEmpty(token) && StringUtils.countMatches(token, ".") == 2) {
                String authorization2 = StringUtils.substringBetween(token, ".");
                String decoded = new String(Base64.decodeBase64(authorization2));
                Map properties = new ObjectMapper().readValue(decoded, Map.class);
                username = (String) properties.get("sub");
            }
        }
        //验证访问权限
        String menuKey = RedisConstant.PREFIX + username + "_menu";
        Long size = redisTemplate.opsForList().size(menuKey);
        if (size == null || size == 0) {
            //redis管理
            return false;
        }
        if ("/mt/user/test".equals(uri)){
            return true;
        }
        boolean flg = false;
        // 会出现未知错误，先强转会好点
        List<Object> list = null;
        try {
            list = redisTemplate.opsForList().range(menuKey, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        List<Permission> permissions = (ArrayList<Permission>)(List)list;
        assert permissions != null;
        for (Permission p : permissions) {
            String pUrl = p.getUrl();
            if (pUrl != null && pUrl.contains("/*")) {
                int index = pUrl.lastIndexOf("/");
                String subUrl = pUrl.substring(0, index).trim();
                if ((uri.contains(subUrl) && method.equals(p.getMethod()))) {
                    flg = true;
                    break;
                }
            }
            if ((uri.equals(p.getUrl().trim()) && method.equals(p.getMethod()))) {
                flg = true;
                break;
            }
        }
        return flg;
    }
}
