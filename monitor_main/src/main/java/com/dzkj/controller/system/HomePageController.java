package com.dzkj.controller.system;


import com.dzkj.biz.system.IPageBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.HomePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 页面配置mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class HomePageController {

    @Autowired
    private IPageBiz pageBiz;

    @SysOperateLog(value = "保存主页信息", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "home_page/save", method = RequestMethod.POST)
    public ResponseUtil save(@RequestBody HomePage homePage){
        return pageBiz.saveHomePage(homePage);
    }

    /**
     * 查询首页信息
     */
    @RequestMapping(value = "common/home_page/{companyId}", method = RequestMethod.GET)
    public ResponseUtil getHomePage(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(pageBiz.getHomePage(companyId));
    }

}
