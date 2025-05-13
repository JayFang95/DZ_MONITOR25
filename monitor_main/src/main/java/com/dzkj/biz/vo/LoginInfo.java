package com.dzkj.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 登录请求数据
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class LoginInfo {

    //登录类型 1：用户名密码 2：手机号验证码
    private String type;
    //用户名
    private String username;
    //密码
    private String password;
    //手机号
    private String phone;
    //验证码
    private String captcha;

}
