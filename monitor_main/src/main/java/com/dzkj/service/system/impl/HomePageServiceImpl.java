package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.HomePage;
import com.dzkj.mapper.system.HomePageMapper;
import com.dzkj.service.system.IHomePageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 首页page service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class HomePageServiceImpl extends ServiceImpl<HomePageMapper, HomePage> implements IHomePageService {

    @Override
    public List<HomePage> listByCompanyId(Long companyId) {
        LambdaQueryWrapper<HomePage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomePage::getCompanyId, companyId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean removeByCompanyId(Long companyId) {
        LambdaQueryWrapper<HomePage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomePage::getCompanyId, companyId);
        return baseMapper.delete(wrapper) > 0;
    }
}
