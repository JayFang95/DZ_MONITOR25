package com.dzkj.common.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/6
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class SmsUtil {

    private final static String SIGN_NAME = "鼎足空间";
    private final static String ACCESS_KEY_ID = "LTAI5tPF6N7QuwJ6WMRjdaff";
    private final static String ACCESS_KEY_SECRET = "yn0a9bdGYq97MYZhElI4V81pHxBLzC";
    private final static String ENDPOINT = "dysmsapi.aliyuncs.com";
    /**
     * 短信模板code
     */
    private final static String CAPTCHA_TEMPLATE_CODE = "SMS_221145011";
    private final static String ALARM_TEMPLATE_CODE = "SMS_248620076";

    private final static Config CONFIG;

    static {
        CONFIG = new Config()
                .setAccessKeyId(ACCESS_KEY_ID)
                .setAccessKeySecret(ACCESS_KEY_SECRET);
        CONFIG.endpoint = ENDPOINT;
    }

    /**
     * 发送短信验证码
     *
     * @description
     * @author jing.fang
     * @date 2021/8/6 10:33
     * @param code code
     * @param phone phone
     **/
    public static void sendCode(String code, String phone) {
        try {
            Client client = new Client(CONFIG);
            SendSmsRequest request = new SendSmsRequest();
            request.signName = SIGN_NAME;
            request.phoneNumbers = phone;
            request.templateCode = CAPTCHA_TEMPLATE_CODE;
            request.templateParam = "{'code':'"+code+"'}";
            client.sendSms(request);
        } catch (Exception e) {
            log.info("发送短信失败,手机号： " + phone);
        }
    }

    /**
     * 发送报警信息
     *
     * @description
     * @author jing.fang
     * @date 2022/8/10 10:10
     * @param map map
     * @param phone phone
    **/
    public static void sendAlarmMsg(Map<String, String> map, String phone) {
        try {
            Client client = new Client(CONFIG);
            SendSmsRequest request = new SendSmsRequest();
            request.signName = SIGN_NAME;
            request.phoneNumbers = phone;
            request.templateCode = ALARM_TEMPLATE_CODE;
            request.templateParam = JSON.toJSONString(map);
            SendSmsResponse response = client.sendSms(request);
        } catch (Exception e) {
            log.info("发送短信失败,手机号： " + phone);
        }
    }

}
