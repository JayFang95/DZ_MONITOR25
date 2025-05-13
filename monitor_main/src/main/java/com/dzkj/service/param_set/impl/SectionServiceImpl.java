package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.param_set.Section;
import com.dzkj.mapper.param_set.SectionMapper;
import com.dzkj.service.param_set.ISectionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 断面服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section> implements ISectionService {

    @Override
    public boolean findByName(Section section) {
        LambdaQueryWrapper<Section> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Section::getMissionId, section.getMissionId())
                .eq(Section::getName, section.getName())
                .ne(section.getId()!=null, Section::getId, section.getId())
                .orderByDesc(Section::getCreateTime);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public int deleteByMission(Long missionId) {
        LambdaQueryWrapper<Section> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Section::getMissionId, missionId);
        return baseMapper.delete(wrapper);
    }

    @Override
    public int deleteByMissions(List<Long> missionIds) {
        LambdaQueryWrapper<Section> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Section::getMissionId, missionIds);
        return baseMapper.delete(wrapper);
    }

    @Override
    public boolean removeByGroupId(Long groupId) {
        LambdaQueryWrapper<Section> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Section::getGroupId, groupId);
        return baseMapper.delete(wrapper) > 0;
    }
}
