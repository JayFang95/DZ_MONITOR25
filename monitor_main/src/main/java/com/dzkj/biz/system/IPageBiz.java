package com.dzkj.biz.system;

import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.HomePage;
import com.dzkj.entity.system.LoginPage;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/16
 * @description 页面配置业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IPageBiz {

    /**
     * 保存登录页信息
     *
     * @description 保存登录页信息
     * @author jing.fang
     * @date 2021/8/16 16:35
     * @param loginPage loginPage
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil saveLoginPage(LoginPage loginPage);

    /**
     * 保存首页信息
     *
     * @description 保存首页信息
     * @author jing.fang
     * @date 2021/8/16 17:30
     * @param homePage homePage
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil saveHomePage(HomePage homePage);

    /**
     * 查询登录页资源
     *
     * @description
     * @author jing.fang
     * @date 2022/3/2 9:43
     * @param
     * @return com.dzkj.entity.system.LoginPage
    **/
    LoginPage getLoginPage();

    /**
     * 查询首页信息
     *
     * @description 查询首页信息
     * @author jing.fang
     * @date 2022/3/2 9:47
     * @param companyId companyId
     * @return com.dzkj.entity.system.HomePage
    **/
    HomePage getHomePage(Long companyId);
}
