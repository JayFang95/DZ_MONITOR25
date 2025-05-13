package com.dzkj.biz;

import com.dzkj.biz.vo.LoginInfo;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 认证授权接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IAuthBiz {

    /**
     * 登录请求
     *
     * @description 登录请求
     * @author jing.fang
     * @date 2021/8/2 17:54
     * @param info info
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil login(LoginInfo info);

    /**
     * 获取手机验证码
     *
     * @description 获取手机验证码
     * @author jing.fang
     * @date 2021/8/2 18:02
     * @param phone phone
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil getCaptcha(String phone);

    /**
     * 刷新令牌
     *
     * @description 刷新令牌
     * @author jing.fang
     * @date 2021/8/5 9:49
     * @param request request
     * @param response
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil refresh(HttpServletRequest request, HttpServletResponse response);

    /**
     * 用户退出
     *
     * @description 用户退出
     * @author jing.fang
     * @date 2021/8/23 9:01
     * @param request request
     * @return void
    **/
    boolean logout(HttpServletRequest request);
}
