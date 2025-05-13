package com.dzkj.controller;

import com.dzkj.biz.IAuthBiz;
import com.dzkj.biz.vo.LoginInfo;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 权限controller
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt/auth")
@Slf4j
public class AuthController {

    @Autowired
    private IAuthBiz authBiz;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @SysOperateLog(value = "登录", type = LogConstant.UPDATE, modelName = LogConstant.AUTH_LOGIN)
    public ResponseUtil login(@RequestBody LoginInfo info){
       return authBiz.login(info);
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @SysOperateLog(value = "退出", type = LogConstant.UPDATE, modelName = LogConstant.AUTH_LOGIN)
    public ResponseUtil logout(HttpServletRequest request){
        log.info("正在退出。。。");
        return ResponseUtil.success(authBiz.logout(request));
    }

    @RequestMapping(value = "captcha", method = RequestMethod.POST)
    @SysOperateLog(value = "获取验证码", type = LogConstant.UPDATE, modelName = LogConstant.AUTH_LOGIN)
    public ResponseUtil getCaptcha(@RequestBody LoginInfo info){
        String phone = info.getPhone();
        if(StringUtils.isEmpty(phone) || !StringUtils.isNumeric(phone) || phone.length()!=11){
            return ResponseUtil.failure(500, "手机号格式不正确");
        }
        return authBiz.getCaptcha(phone);
    }

    /**
     * 刷新令牌
     */
    @RequestMapping(value = "refresh", method = RequestMethod.POST)
    public ResponseUtil refresh(HttpServletRequest request, HttpServletResponse response){
        return authBiz.refresh(request, response);
    }

}
