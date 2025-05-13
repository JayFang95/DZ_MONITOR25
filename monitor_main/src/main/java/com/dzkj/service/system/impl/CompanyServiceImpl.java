package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.system.vo.CompanyCondition;
import com.dzkj.entity.system.Company;
import com.dzkj.mapper.system.CompanyMapper;
import com.dzkj.service.system.ICompanyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements ICompanyService {

    @Override
    public IPage<Company> getPage(int pageIndex, int pageSize, CompanyCondition condition) {
        Page<Company> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(condition.getName()), Company::getName, condition.getName())
                .eq(condition.getActive()!=null, Company::getActive, condition.getActive())
                .ge(condition.getCreateTime()!=null, Company::getCreateTime, condition.getCreateTime())
                .orderByDesc(Company::getCurrent).orderByDesc(Company::getCreateTime);
        return baseMapper.selectPage(page, queryWrapper);
    }


    @Override
    public boolean findCodeExist(Company company) {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getCode, company.getCode())
                .ne(company.getId()!=null, Company::getId, company.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Long getCurrentCompany() {
        LambdaQueryWrapper<Company> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Company::getCurrent, true);
        List<Company> list = baseMapper.selectList(wrapper);
        return list.size()==0 ? -1 : list.get(0).getId();
    }

    @Override
    public boolean updateCurrent(Long id) {
        LambdaUpdateWrapper<Company> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Company::getCurrent, true);
        wrapper.eq(Company::getId, id);
        boolean b = baseMapper.update(null, wrapper) > 0;
        if(b){
            LambdaUpdateWrapper<Company> wrapper1 = new LambdaUpdateWrapper<>();
            wrapper1.set(Company::getCurrent, false);
            wrapper1.ne(Company::getId, id);
            wrapper1.eq(Company::getCurrent, true);
            baseMapper.update(null, wrapper1);
        }
        return b;
    }

    @Override
    public boolean updateStatus(Long id) {
        Company company = baseMapper.selectById(id);
        LambdaUpdateWrapper<Company> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Company::getActive, !company.getActive());
        wrapper.set(Company::getStatusTime, new Date());
        wrapper.eq(Company::getId, id);
        return baseMapper.update(null, wrapper) > 0;
    }
}
