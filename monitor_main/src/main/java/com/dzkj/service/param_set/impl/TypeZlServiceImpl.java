package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.param_set.vo.TypeZlVO;
import com.dzkj.entity.param_set.TypeZl;
import com.dzkj.mapper.param_set.TypeZlMapper;
import com.dzkj.service.param_set.ITypeZlService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class TypeZlServiceImpl extends ServiceImpl<TypeZlMapper, TypeZl> implements ITypeZlService {

    @Override
    public boolean removeByProjectId(Long projectId) {
        LambdaQueryWrapper<TypeZl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TypeZl::getProjectId, projectId);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByMissionId(Long missionId) {
        LambdaQueryWrapper<TypeZl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TypeZl::getMissionId, missionId);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<TypeZl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TypeZl::getMissionId, missionIds);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean checkName(TypeZlVO data) {
        LambdaQueryWrapper<TypeZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeZl::getName, data.getName())
                .eq(TypeZl::getProjectId, data.getProjectId())
                .ne(data.getId()!=null, TypeZl::getId, data.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }

}
