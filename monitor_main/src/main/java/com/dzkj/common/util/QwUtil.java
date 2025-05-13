package com.dzkj.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzkj.biz.vo.AppMsgVO;
import com.dzkj.biz.vo.CreateMemberVO;
import com.dzkj.biz.vo.DeleteMemberBatch;
import com.dzkj.common.constant.RedisConstant;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/8/23 14:44
 * @description 企业微信工具类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@Component
@Log4j2
public class QwUtil {

    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRET";
    private static final String QR_CODE_URL = "https://qyapi.weixin.qq.com/cgi-bin/corp/get_join_qrcode?access_token=ACCESS_TOKEN&size_type=399x399";
    private static final String GET_USER_ID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserid?access_token=ACCESS_TOKEN";
    private static final String CREATE_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=ACCESS_TOKEN";
    private static final String DELETE_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=ACCESS_TOKEN&userid=USERID";
    private static final String DELETE_USER_BATCH_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete?access_token=ACCESS_TOKEN";
    private static final String SEND_APP_MSG_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

    private static final String CORP_ID = "ww5f39e213ce5b35fd";
    private static final String CORP_SECRET_TXL = "4SjKG-5MgHN2ROB4Bjgjg5BqxvkwAPN2l2hhPT47f70";
    private static final String CORP_SECRET_ALARM = "ajbVJ9X2hZb5aPySfjoPyP0O6uAsdmEMSfzHuZtf-Cs";
    private static final String CORP_SECRET_SURVEY = "PzWuPa6txrY_yLqvL2kzEmc8CrJ-hGMtGU-sRl3Dg30";
    private static final String CORP_SECRET_PROCESS = "tpRj23mnaolJ9aVKoMEA6ShBczFz6jIIURRlFbYpuaQ";

    private static final String OK = "ok";
    private static final String ERR_MSG = "errmsg";

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    /**
     * 获取accessToken
     */
    public String getAccessToken(String corpSecret){
        String accessToken = redisTemplate.opsForValue().get(RedisConstant.PREFIX + corpSecret);
        if (accessToken == null){
            accessToken = "";
            String accessTokenUrl = ACCESS_TOKEN_URL.replace("ID", CORP_ID).replace("SECRET", corpSecret);
            try {
                String responseToken = HttpUtils.doGet(accessTokenUrl);
                if (responseToken != null){
                    JSONObject object = JSON.parseObject(responseToken);
                    Object errMsg = object.get(ERR_MSG);
                    if (errMsg != null && OK.equals(errMsg.toString())){
                        accessToken = (String) object.get("access_token");
                        // token 有效期是 7200 秒， 提前一分钟过期
                        redisTemplate.opsForValue().set(RedisConstant.PREFIX + corpSecret, accessToken, 7140, TimeUnit.SECONDS);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    /**
     * 获取加入企业二维码
     * 需要先获取通讯应用的访问令牌
     */
    public String getTempTicket(){
        String accessToken = getAccessToken(CORP_SECRET_TXL);
        String qrCodeUrl = QR_CODE_URL.replace("ACCESS_TOKEN", accessToken);
        String responseBody = HttpUtils.doGet(qrCodeUrl);
        JSONObject object = JSON.parseObject(responseBody);
        Object errMsg = object.get(ERR_MSG);
        if (errMsg != null && OK.equals(errMsg.toString())){
            return  (String) object.get("join_qrcode");
        }
        return "";
    }

    /**
     * 添加通讯录人员
     * 默认所属部门id: 1
     */
    public boolean createUser(CreateMemberVO userVO){
        try {
            String accessToken = getAccessToken(CORP_SECRET_TXL);
            String createUserUrl = CREATE_USER_URL.replace("ACCESS_TOKEN", accessToken);
            String responseBody = HttpUtils.doPost(createUserUrl, JSONObject.toJSONString(userVO));
            return JSON.parseObject(responseBody).getIntValue(ERR_MSG) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除通讯录人员
     */
    public boolean deleteUser(String userId){
        try {
            String accessToken = getAccessToken(CORP_SECRET_TXL);
            String deleteUserUrl = DELETE_USER_URL.replace("ACCESS_TOKEN", accessToken).replace("USERID", userId);
            String responseBody = HttpUtils.doGet(deleteUserUrl);
            return JSON.parseObject(responseBody).getIntValue(ERR_MSG) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除通讯录人员
     */
    public boolean deleteUserBatch(DeleteMemberBatch memberBatch){
        try {
            String accessToken = getAccessToken(CORP_SECRET_TXL);
            String deleteUserUrl = DELETE_USER_BATCH_URL.replace("ACCESS_TOKEN", accessToken);
            String responseBody = HttpUtils.doPost(deleteUserUrl, JSONObject.toJSONString(memberBatch));
            return JSON.parseObject(responseBody).getIntValue(ERR_MSG) == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取人员userId
     */
    public String getUserId(String mobile){
        String accessToken = getAccessToken(CORP_SECRET_TXL);
        String findUserIdUrl = GET_USER_ID_URL.replace("ACCESS_TOKEN", accessToken);
        Map<String, String> map = new HashMap<>(16);
        map.put("mobile", mobile);
        String jsonString = JSONObject.toJSONString(map);
        String responseBody = HttpUtils.doPost(findUserIdUrl, jsonString);
        log.info("企微获取userid结果: {}" , responseBody);
        JSONObject object = JSON.parseObject(responseBody);
        Object errMsg = object.get(ERR_MSG);
        if (errMsg != null && OK.equals(errMsg.toString())){
            return  (String) object.get("userid");
        }
        return null;
    }

    /**
     * 发送文本应用消息
     */
    public JSONObject sendAppTextMsg(AppMsgVO msgVO){
        String accessToken = getAccessToken(CORP_SECRET_ALARM);
        String sendMsgUrl = SEND_APP_MSG_URL.replace("ACCESS_TOKEN", accessToken);
        String responseBody = HttpUtils.doPost(sendMsgUrl, JSONObject.toJSONString(msgVO));
        log.info("报警信息企业微信发送结果: {}" , responseBody);
        return JSON.parseObject(responseBody);
    }

    public JSONObject sendAppTextMsgProcess(AppMsgVO msgVO){
        String accessToken = getAccessToken(CORP_SECRET_PROCESS);
        String sendMsgUrl = SEND_APP_MSG_URL.replace("ACCESS_TOKEN", accessToken);
        String responseBody = HttpUtils.doPost(sendMsgUrl, JSONObject.toJSONString(msgVO));
        log.info("过程信息企业微信发送结果: {}" , responseBody);
        return JSON.parseObject(responseBody);
    }

    public JSONObject sendAppTextMsgSurvey(AppMsgVO msgVO){
        String accessToken = getAccessToken(CORP_SECRET_SURVEY);
        String sendMsgUrl = SEND_APP_MSG_URL.replace("ACCESS_TOKEN", accessToken);
        String responseBody = HttpUtils.doPost(sendMsgUrl, JSONObject.toJSONString(msgVO));
        log.info("测量信息企业微信发送结果: {}" , responseBody);
        return JSON.parseObject(responseBody);
    }

    /**
     * 发送文本卡片应用消息
     */
    public JSONObject sendAppCartMsg(AppMsgVO msgVO){
        String accessToken = getAccessToken(CORP_SECRET_ALARM);
        String sendMsgUrl = SEND_APP_MSG_URL.replace("ACCESS_TOKEN", accessToken);
        String msg = "{\n" +
                "   \"touser\" : \"" + msgVO.getTouser() + "\",\n" +
                "   \"msgtype\" : \"textcard\",\n" +
                "   \"agentid\" : 1000002,\n" +
                "   \"textcard\" : {\n" +
                "            \"title\" : \"" + msgVO.getTextcard().getTitle() + "\",\n" +
                "            \"description\" : \""+msgVO.getTextcard().getDescription()+"\",\n" +
                "            \"url\" : \""+msgVO.getTextcard().getUrl()+"\",\n" +
                "            \"btntxt\":\""+msgVO.getTextcard().getBtntxt()+"\"\n" +
                "   },\n" +
                "   \"enable_id_trans\": 0,\n" +
                "   \"enable_duplicate_check\": 0,\n" +
                "   \"duplicate_check_interval\": 1800\n" +
                "}";

        String responseBody = HttpUtils.doPost(sendMsgUrl, JSONObject.toJSONString(msgVO));
        return JSON.parseObject(responseBody);
    }

}
