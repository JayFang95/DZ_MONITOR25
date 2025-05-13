package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.param_set.vo.GroupCondition;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.mapper.param_set.PtGroupMapper;
import com.dzkj.service.param_set.IPtGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 点组服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class PtGroupServiceImpl extends ServiceImpl<PtGroupMapper, PtGroup> implements IPtGroupService {

    @Override
    public List<PtGroup> getList(GroupCondition condition) {
        if (condition.getMissionIds() == null || condition.getMissionIds().size() == 0){
            return new ArrayList<>();
        }
        return baseMapper.getList(condition);
    }

    @Override
    public Page<PtGroup> getPage(Integer pi, Integer ps, GroupCondition condition) {
        if (condition.getMissionIds() == null || condition.getMissionIds().size() == 0){
            return new Page<>(pi, ps, 0);
        }
        return baseMapper.getPage(new Page<PtGroup>(pi, ps), condition);
    }

    @Override
    public boolean findByName(PtGroupVO ptGroup) {
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PtGroup::getName, ptGroup.getName())
                .eq(PtGroup::getMissionId, ptGroup.getMissionId())
                .ne(ptGroup.getId()!=null, PtGroup::getId, ptGroup.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public int deleteByMission(Long missionId) {
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PtGroup::getMissionId, missionId);
        return baseMapper.delete(wrapper);
    }

    @Override
    public List<PtGroup> findByProjectId(Long projectId, Long missionId) {
        return baseMapper.findByProjectId(projectId, missionId);
    }

    @Override
    public List<PtGroup> listByMissionIds(List<Long> missionId) {
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PtGroup::getMissionId, missionId).orderByDesc(PtGroup::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Long> getGroupIdsInMission(Long ptGroupId) {
        PtGroup group = baseMapper.selectById(ptGroupId);
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PtGroup::getMissionId, group.getMissionId());
        wrapper.select(PtGroup::getId);
        return baseMapper.selectList(wrapper).stream().map(PtGroup::getId).collect(Collectors.toList());
    }
}
