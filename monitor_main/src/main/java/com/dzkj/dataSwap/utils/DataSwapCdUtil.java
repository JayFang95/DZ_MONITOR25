package com.dzkj.dataSwap.utils;

import com.dzkj.common.constant.RedisConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/24 下午2:16
 * @description 数据交换工具类-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
@Slf4j
public class DataSwapCdUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;

    public String getToken(String username, String password, String uri){
        String token = (String) redisTemplate.opsForValue().get(RedisConstant.PREFIX_CD + username + password + "_TOKEN");
        if(token != null){
            return token;
        }
        String getTokenUrl = uri + "/getToken";
        //设置请求参数
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.set("username", username);
        formData.set("password", password);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(getTokenUrl, HttpMethod.POST, new HttpEntity<>(formData), TokenResponse.class);
        TokenResponse body = response.getBody();
        if (body == null || body.getCode() != 200){
            return null;
        }
        token = body.getToken();
        redisTemplate.opsForValue().set(RedisConstant.PREFIX_CD + username + password + "_TOKEN", token, 29, TimeUnit.MINUTES);
        return token;
    }

    @Getter
    @Setter
    static class TokenResponse{
        private String token;
        private int code;
        private String msg;
    }


}
