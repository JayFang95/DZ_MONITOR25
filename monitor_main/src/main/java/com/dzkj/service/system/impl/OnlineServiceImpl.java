package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.Online;
import com.dzkj.mapper.system.OnlineMapper;
import com.dzkj.service.system.IOnlineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class OnlineServiceImpl extends ServiceImpl<OnlineMapper, Online> implements IOnlineService {

    @Override
    public List<Online> getOnlines(Long companyId) {
        LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Online::getCompanyId, companyId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Long> getOnlineUserIds(Long companyId) {
        return baseMapper.getOnlineUserIds(companyId);
    }
}
