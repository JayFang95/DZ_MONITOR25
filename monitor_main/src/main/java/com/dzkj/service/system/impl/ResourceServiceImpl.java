package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.entity.system.Resource;
import com.dzkj.mapper.system.ResourceMapper;
import com.dzkj.service.system.IResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {

    @Override
    public List<Resource> getMenus(ResourceCondition condition) {
        LambdaQueryWrapper<Resource> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(condition.getName()),Resource::getName, condition.getName())
                .eq(StringUtils.isNotEmpty(condition.getType()),Resource::getType, condition.getType())
                .eq(Resource::getWhiteUrl, false)
                .or(temp->temp.eq(Resource::getWhiteUrl, true).eq(Resource::getType, "普通菜单")
                        .like(StringUtils.isNotEmpty(condition.getName()),Resource::getName, condition.getName())
                        .eq(StringUtils.isNotEmpty(condition.getType()),Resource::getType, condition.getType()))
                .orderByAsc(Resource::getType).orderByAsc(Resource::getIndex);
        return baseMapper.selectList(queryWrapper);
    }


    @Override
    public List<Resource> findByRoleId(Long id) {
        return baseMapper.findByRoleId(id);
    }
}
