package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.system.IOPerateLogBiz;
import com.dzkj.biz.system.vo.OperateLogCondition;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.system.OperateLog;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IOperateLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/27
 * @description OperateLogBizImpl
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class OperateLogBizImpl implements IOPerateLogBiz {

    @Autowired
    private IOperateLogService iOperateLogService;
    @Autowired
    private ICompanyService iCompanyService;

    @Override
    public IPage<OperateLog> getPage(int pageIndex, int pageSize, OperateLogCondition condition) {
        // 超级管理员使用的置顶单位公司id
        if (condition.getCompanyId() == 0){
            condition.setCompanyId(iCompanyService.getCurrentCompany());
        }
        return iOperateLogService.getPage(pageIndex, pageSize, condition);
    }


}
