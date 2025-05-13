package com.dzkj.dataSwap.utils;

import com.dzkj.common.constant.RedisConstant;
import com.dzkj.dataSwap.bean.DataSwapResponse;
import com.dzkj.dataSwap.enums.DataSwapEnum;
import com.dzkj.entity.data.PushTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/3 12:36
 * @description 上海铁路数据交换系统工具库
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class DataSwapUtil {

    @Autowired
    private DataSwapConst dataSwapConst;
    @Autowired
    private RsaUtil rsaUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${data-swap.dataType}")
    private int dataType;

    /**
     * 获取上铁数据对接系统访问令牌
     * @return 访问令牌
     */
    public String getToken(PushTask pushTask){
        String token = (String) redisTemplate.opsForValue().get(RedisConstant.PREFIX + pushTask.getAppId() + "_STD_TOKEN");
        if (token != null){
            return token;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String data = "{\"s2pHttpUrl\":\"" + dataSwapConst.getDzUrl() + "\"}";
            String time = format.format(new Date());
            DataSwapResponse response = doRequest(
                    DataSwapEnum.SHANK_HAND.getAction(),
                    time,
                    rsaUtil.encryptByPublicKey(data, pushTask.getPublicKey()),
                    null,
                    pushTask);
            log.info("接收到握手结果: {}", response);
            if (response!=null && response.getHead().getResult() == 0){
                String newToken = response.getBody().get("token");
                redisTemplate.opsForValue().set(RedisConstant.PREFIX + pushTask.getAppId() + "_STD_TOKEN", newToken, 7, TimeUnit.HOURS);
                return newToken;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("握手消息发送出现异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 发起接口请求
     *
     * @param action   接口标识
     * @param time     时间字符串
     * @param data     请求参数数据JSONString(RSA加密)
     * @param token    接口访问令牌
     * @param pushTask
     * @return 请求结果
     */
    public DataSwapResponse doRequest(String action, String time, String data, String token, PushTask pushTask){
        //获取请求参数对象
        MultiValueMap<String, Object> formData = getRequestFormData(action, time, data, token, pushTask);
        //设置对接协议请求头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //发起接口请求，获取结果
        return restTemplate.exchange(
                pushTask.getStdUrl(),
                HttpMethod.POST,
                new HttpEntity<>(formData, headers),
                DataSwapResponse.class
        ).getBody();
    }

    /**
     * 获取上海铁路对接接口请求参数对象
     *
     * @param action   接口标识,描述对应的接口行为
     * @param time     时间字符串yyyyMMddHHmmss
     * @param data     jsonString或者protobuf字节数组的base64字符串.如果需要加密，此处放置的是加密后数据。依据dataType决定
     * @param pushTask pushTask
     * @return 请求参数对象
     */
    private MultiValueMap<String, Object> getRequestFormData(String action, String time, String data, String token, PushTask pushTask){
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("action", action);
        formData.add("appId", pushTask.getAppId());
        formData.add("time", time);
        formData.add("dataType", dataType);
        formData.add("data", data);
        formData.add("sign", getSign(data, time, pushTask));
        if (token != null){
            formData.add("token", token);
        }
        return formData;
    }

    /**
     * MD5(APP_KEY+data+appId +time)得到的签名
     *
     * @param data     data
     * @param time     time
     * @param pushTask pushTask
     * @return 签名字符串信息
     */
    private String getSign(String data, String time, PushTask pushTask) {
        String sourceToMd5text = pushTask.getAppKey()
                + data
                + pushTask.getAppId()
                + time;
        return DigestUtils.md5Hex(sourceToMd5text);
    }

}
