package com.dzkj.biz.system.impl;

import com.dzkj.biz.system.IOnlineBiz;
import com.dzkj.common.util.TokenUtil;
import com.dzkj.entity.system.Online;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description 在线情况业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class OnlineBizImpl implements IOnlineBiz {

    @Autowired
    private IOnlineService iOnlineService;
    @Autowired
    private ICompanyService iCompanyService;

    @Override
    public List<Online> getOnLines(Long companyId) {
        // 超级管理员使用的置顶单位公司id
        if (companyId == 0){
            companyId = iCompanyService.getCurrentCompany();
        }
        List<Online> onLines = iOnlineService.getOnlines(companyId);
        ArrayList<Online> list = new ArrayList<>();
        for (Online online : onLines) {
            // 删除过期未退出用户
            if(TokenUtil.isTokenExpired(online.getToken())){
                iOnlineService.removeById(online.getId());
            }else {
                list.add(online);
            }
        }
        return list;
    }
}
