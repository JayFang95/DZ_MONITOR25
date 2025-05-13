package com.dzkj.controller;

import com.dzkj.biz.wx.IWeiXinBiz;
import com.dzkj.common.util.QwUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.common.util.WeiXinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 微信Controller
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt")
public class WxController {

    @Autowired
    private IWeiXinBiz weiXinBiz;
    @Autowired
    private WeiXinUtil weiXinUtil;
    @Autowired
    private QwUtil qwUtil;

    /**
     * 微信官方验证
     *
     * @date 2021/8/26 10:30
     * @param request request
     * @param response response
     * @return void
    **/
    @RequestMapping(value = "wx", method = RequestMethod.GET)
    public void checkWx(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //signature	微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        //timestamp	时间戳
        //nonce	随机数
        //echostr 随机字符串
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        if(weiXinBiz.check(signature, timestamp, nonce, echostr)){
            //原样返回echostr
            PrintWriter writer = response.getWriter();
            writer.println(echostr);
            writer.flush();
            writer.close();
        }else {
            System.out.println("验证失败");
        }
    }

    /**
     * 微信公众号消息处理
     * 关注时推送事件 EventKey:test
     * 未关注时推送事件 EventKey:qrscene_test
     *
     * @date 2021/8/26 10:30
     * @param request request
     * @param response response
     * @return void
    **/
    @RequestMapping(value = "wx", method = RequestMethod.POST)
    public void sendMsg(HttpServletRequest request, HttpServletResponse response) {
        Map<String ,String> requestMap = weiXinUtil.parseRequest(request);
    }

    /**
     * 获取二维码ticket
     *
     * @date 2021/8/26 10:30
     * @param sceneStr sceneStr
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "wx/ticket/{scene_str}", method = RequestMethod.GET)
    public ResponseUtil getTicket(@PathVariable("scene_str") String sceneStr){
        String ticket = weiXinUtil.getTempTicket(sceneStr);
        return ResponseUtil.success(ticket);
    }

}
