package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.Groups;
import com.dzkj.mapper.system.GroupMapper;
import com.dzkj.service.system.IGroupService;
import org.springframework.stereotype.Service;

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
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Groups> implements IGroupService {

    @Override
    public boolean removeByCompanyId(Long companyId) {
        LambdaQueryWrapper<Groups> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Groups::getCompanyId, companyId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean checkName(Groups groups) {
        LambdaQueryWrapper<Groups> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(groups.getId()!=null, Groups::getId, groups.getId())
                .eq(Groups::getCompanyId, groups.getCompanyId())
                .eq(Groups::getName, groups.getName());
        return baseMapper.selectCount(wrapper) > 0;
    }
}
