package com.dzkj.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.wx.vo.ActivityMsgVO;
import com.dzkj.biz.wx.vo.TemplateData;
import com.dzkj.biz.wx.vo.TemplateVO;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.entity.system.User;
import com.dzkj.service.system.IUserService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/5/26
 * @description 微信业务工具类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ConfigurationProperties(prefix = "wx")
@Component
@Log4j2
public class WeiXinUtil {

    private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static final String SET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
    private static final String GET_INDUSTRY_URL = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=ACCESS_TOKEN";
    private static final String SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    private static final String GET_USER_LIST_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
    private static final String GET_USER_LIST_URL_OPENID = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
    private static final String GET_TEMP_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
    private static final String GET_CODE_TICKET_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";

    private static final String SCAN_EVENT = "SCAN";
    private static final String SUBSCRIBE_EVENT = "subscribe";
    private static final String UNSUBSCRIBE_EVENT = "unsubscribe";

    private static final String ALARM_TEMPLATE_ID = "F0FC8r15Q38X3hMPi7jv_xI8LzfkKg7I1cYV2l2RWS0";

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;
    @Autowired
    private IUserService userService;

    /**
     * 微信token
     */
    private String token;

    /**
     * 微信appId
     */
    private String appId;

    /**
     * 微信appSecret
     */
    private String appSecret;

    /**
     * 获取微信accessToken
     *
     * @description 获取微信accessToken
     * @author jing.fang
     * @date 2021/5/26 13:48
     * @return java.lang.String
    **/
    public String getAccessToken(){
        String accessToken = redisTemplate.opsForValue().get(RedisConstant.PREFIX + "access_token");
        if(accessToken==null){
            //缓存中没有，发起网络请求获取
            String url = GET_ACCESS_TOKEN_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
            String tokenStr = HttpUtils.doGet(url);
            //错误时微信会返回错误码errcode等信息
            if(tokenStr==null || tokenStr.contains("errcode")){
                log.info(tokenStr);
                return "";
            }
            JSONObject jsonObject = JSONObject.parseObject(tokenStr);
            accessToken = jsonObject.getString("access_token");
            int expiresIn = jsonObject.getIntValue("expires_in");
            redisTemplate.opsForValue().set(RedisConstant.PREFIX + "access_token", accessToken, expiresIn, TimeUnit.SECONDS);
        }
        return accessToken;
    }

    /**
     * 设置公众号所属行业
     *
     * @description 设置公众号所属行业
     * @author jing.fang
     * @date 2021/5/26 16:18
     * @param data data
     **/
    public void setIndustry(String data){
        String accessToken = getAccessToken();
        String url = SET_INDUSTRY_URL.replace("ACCESS_TOKEN", accessToken);
        String result = HttpUtils.doPost(url, data);
        System.out.println(result);
    }

    /**
     * 获取公众号所属行业信息
     *
     * @description 获取公众号所属行业信息
     * @author jing.fang
     * @date 2021/5/26 16:41
     * @return java.lang.String
    **/
    public String getIndustry(){
        String accessToken = getAccessToken();
        String url = GET_INDUSTRY_URL.replace("ACCESS_TOKEN", accessToken);
        String result = HttpUtils.doGet(url);
        System.out.println(result);
        return result;
    }

    /**
     * 发送报警模板消息
     *
     * @description 发送报警模板消息
     * @author jing.fang
     * @date 2021/5/26 17:08
     * @param data data
     * @param appId appId
     **/
    public void sentAlarmTemplate(String appId, ActivityMsgVO data){
        String accessToken = getAccessToken();
        String url = SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", accessToken);
        TemplateVO vo = new TemplateVO(appId, ALARM_TEMPLATE_ID);
        Map<String, TemplateData> map = new HashMap<>(16);
        map.put("first",new TemplateData(data.getContent(),"#000000"));
        map.put("keyword1",new TemplateData(data.getKey1(),"#000000"));
        map.put("keyword2",new TemplateData(data.getKey2(),"#000000"));
        map.put("keyword3",new TemplateData(data.getKey3(),"#0072FF"));
        map.put("remark",new TemplateData(data.getRemark(),"#00AD00"));
        vo.setData(map);
        String jsonString = JSONObject.toJSONString(vo);
        String result = HttpUtils.doPost(url, jsonString);
        System.out.println(result);
    }

    /**
     * 获取所有关注公众号的openid
     *
     * @description
     * @author jing.fang
     * @date 2021/5/27 9:01
     * @return java.util.List<java.lang.String>
    **/
    public List<String> getUserOpenIdList(){
        ArrayList<String> list = new ArrayList<>();
        String accessToken = getAccessToken();
        String url = GET_USER_LIST_URL.replace("ACCESS_TOKEN", accessToken);
        String result = HttpUtils.doGet(url);
        JSONArray jsonArray = JSONObject.parseObject(result).getJSONObject("data").getJSONArray("openid");
        for (Object obj : jsonArray) {
            list.add((String) obj);
        }
        return list;
    }

    /**
     * 获取二维码ticket
     */
    public String getTempTicket(String sceneStr){
//        String body = "{\"expire_seconds\": 604800, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneStr+"\"}}}";
        String body = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneStr+"\"}}}";
        String accessToken = getAccessToken();
        String url = GET_TEMP_TICKET_URL.replace("TOKEN", accessToken);
        String result = HttpUtils.doPost(url, body);
        return JSONObject.parseObject(result).getString("ticket");
    }

    /**
     * 解析微信推送消息和事件内容
     *
     * @description
     * @author jing.fang
     * @date 2021/6/30 11:23
     * @param request request
     * @return java.util.Map<java.lang.String,java.lang.String>
    **/
    public Map<String, String> parseRequest(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>(16);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(request.getInputStream());
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            elements.forEach(element->{
                map.put(element.getName(), element.getStringValue());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        handelRequestMap(map);
        return map;
    }

    /**
     * 处理微信信息map
     *
     * @description
     * @author jing.fang
     * @date 2021/7/22 10:39
     * @param requestMap requestMap
    **/
    private void handelRequestMap(Map<String, String> requestMap){
        String userId = requestMap.get("EventKey");
        String event = requestMap.get("Event");
        String appId = requestMap.get("FromUserName");
        //关注扫描
        if(StringUtils.isNotEmpty(userId) && (SCAN_EVENT.equals(event) || SUBSCRIBE_EVENT.equals(event))){
            if(SUBSCRIBE_EVENT.equals(event)){
                //qrscene_
                userId = userId.substring(8);
            }
            log.info("================关注=============");
            // 关联用户微信
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(User::getAppId, appId).eq(User::getId, userId);
            userService.update(wrapper);
        }
        //用户取消关注
        if(UNSUBSCRIBE_EVENT.equals(event)){
            log.info("================取消关注=============");
            // 删除关联的分发信息
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getAppId, appId);
            userService.remove(wrapper);
        }
    }
}
