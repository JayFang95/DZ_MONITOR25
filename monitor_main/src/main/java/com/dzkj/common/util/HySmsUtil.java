package com.dzkj.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/6/25 上午11:53
 * @description 互亿无线短信
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class HySmsUtil {

    private final static String ACCESS_KEY_ID = "C28657421";
    private final static String ACCESS_KEY_SECRET = "33cd8f3f7f079ee376b3735cfa9ca699";
    //短信单条请求地址
    private static final String SMS_SINGLE_URL = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
    //短信批量请求地址
    private static final String SMS_BATCH_URL = "https://106.ihuyi.com/webservice/sms.php?method=SubmitBatch";

    //短信模板code
    private final static String CAPTCHA_TEMPLATE_CODE = "1";
    //报警通知模板新
    private final static String ALARM_TEMPLATE_CODE = "315466";
    //漏测通知模板
    private final static String SURVEY_MISS_TEMPLATE_CODE = "315467";
    //漏传通知模板
    private final static String PUSH_MISS_TEMPLATE_CODE = "315468";

    public static void main(String[] args) {
        sendAlarmMsg("15996266642", 1, "5201314");
    }

    /**
     * 发送单条通知短信
     * @param mobile 手机号
     * @param type 通知模板类型：1:超限； 2：漏测； 3：漏传
     * @param content 模板中变量的内容，多个变量以|符号隔开
     */
    public static void sendAlarmMsg(String mobile, int type, String content){
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(SMS_SINGLE_URL);

        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", ACCESS_KEY_ID), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", ACCESS_KEY_SECRET), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                new NameValuePair("mobile", mobile),
                new NameValuePair("templateid", type == 1 ? ALARM_TEMPLATE_CODE
                        : (type == 2 ? SURVEY_MISS_TEMPLATE_CODE : PUSH_MISS_TEMPLATE_CODE)),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try {
            client.executeMethod(method);
            String SubmitResult =method.getResponseBodyAsString();
            log.info("{}{}短信发送完成: {}", mobile, type+"(1-超限 2-漏测 3-漏传)", content);
            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");
            log.info("code:{}, msg:{}, msmId:{}", code, msg, smsid);
            if("2".equals(code)){
                log.info("{}{}短信发送成功: {}", mobile, type+"(1-超限 2-漏测 3-漏传)", content);
            }
        } catch (Exception e) {
            log.info("{}{}短信发送失败: {}_{}", mobile, type+"(1-超限 2-漏测 3-漏传)", content, e.getMessage());
        } finally {
            method.releaseConnection();
        }
    }

}
