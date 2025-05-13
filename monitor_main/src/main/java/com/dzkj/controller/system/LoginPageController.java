package com.dzkj.controller.system;


import com.dzkj.biz.system.IPageBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.LoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 页面配置controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class LoginPageController {

    @Autowired
    private IPageBiz pageBiz;

    @SysOperateLog(value = "保存登录信息", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "login_page/save", method = RequestMethod.POST)
    public ResponseUtil save(@RequestBody LoginPage loginPage){
        return pageBiz.saveLoginPage(loginPage);
    }

    /**
     * 查询登录信息
     */
    @RequestMapping(value = "common/login_page", method = RequestMethod.GET)
    public ResponseUtil getLoginPage(){
        return ResponseUtil.success(pageBiz.getLoginPage());
    }

}
