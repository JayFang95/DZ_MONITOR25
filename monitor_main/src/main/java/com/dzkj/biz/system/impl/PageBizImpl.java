package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.system.IPageBiz;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.File;
import com.dzkj.entity.system.HomePage;
import com.dzkj.entity.system.LoginPage;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IHomePageService;
import com.dzkj.service.system.ILoginPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/16
 * @description 页面配置接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class PageBizImpl implements IPageBiz {

    @Autowired
    private ILoginPageService loginPageService;
    @Autowired
    private IHomePageService homePageService;
    @Autowired
    private ICommonBiz commonBiz;
    @Autowired
    private ICompanyService companyService;

    @Override
    public ResponseUtil saveLoginPage(LoginPage data) {
        boolean b = loginPageService.updateById(data);
       //删除多余的图片
        File file = new File().setCategoryId(data.getId()).setCategoryName("login-page");
        List<File> list = commonBiz.getByCategoryInfo(file);
        List<Long> ids = list.stream()
                .filter(item -> !item.getId().equals(data.getTopId())
                        && !item.getId().equals(data.getCarouselOneId())
                        && !item.getId().equals(data.getCarouselTwoId())
                        && !item.getId().equals(data.getCarouselThreeId()))
                .map(File::getId).collect(Collectors.toList());
        if(ids.size() > 0){
            commonBiz.deleteFileByIds(ids);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil saveHomePage(HomePage data) {
        boolean b = homePageService.updateById(data);
        //删除多余的图片
        File file = new File().setCategoryId(data.getId()).setCategoryName("home-page");
        List<File> list = commonBiz.getByCategoryInfo(file);
        List<Long> ids = list.stream()
                .filter(item -> !item.getId().equals(data.getLeftFullId())
                        && !item.getId().equals(data.getLeftRollId()))
                .map(File::getId).collect(Collectors.toList());
        if(ids.size() > 0){
            commonBiz.deleteFileByIds(ids);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public LoginPage getLoginPage() {
        List<LoginPage> pages = loginPageService.list();
        if (pages.size() > 0){
            return loginPageService.list().get(0);
        }
        LoginPage page = new LoginPage();
        page.setName("合肥市鼎足空间信息技术有限公司");
        page.setYear(2016);
        loginPageService.save(page);
        return loginPageService.list().get(0);
    }

    @Override
    public HomePage getHomePage(Long companyId) {
        if(companyId==null){
            return null;
        }
        if(companyId==0){
            companyId = companyService.getCurrentCompany();
        }
        List<HomePage> list = homePageService.listByCompanyId(companyId);
        return list.size()>0 ? list.get(0) : null;
    }
}
