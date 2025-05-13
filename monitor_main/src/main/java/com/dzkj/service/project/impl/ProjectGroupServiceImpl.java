package com.dzkj.service.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.project.ProjectGroup;
import com.dzkj.mapper.project.ProjectGroupMapper;
import com.dzkj.service.project.IProjectGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目人员配置服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class ProjectGroupServiceImpl extends ServiceImpl<ProjectGroupMapper, ProjectGroup> implements IProjectGroupService {

    @Override
    public void deleteByProjectId(Long id) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getProjectId, id)
                .isNull(ProjectGroup::getMissionId).isNull(ProjectGroup::getPtGroupId);
        baseMapper.delete(wrapper);
    }

    @Override
    public void deleteByMissionId(Long id) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getMissionId, id).isNull(ProjectGroup::getPtGroupId);
        baseMapper.delete(wrapper);
    }

    @Override
    public void deleteByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProjectGroup::getMissionId, missionIds);
        baseMapper.delete(wrapper);
    }

    @Override
    public void deleteByPtGroupId(Long id) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getPtGroupId, id);
        baseMapper.delete(wrapper);
    }

    @Override
    public List<Long> getMissionGroupIds(Long id) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getProjectId, id);
        wrapper.isNotNull(ProjectGroup::getMissionId);
        return baseMapper.selectList(wrapper).stream().map(ProjectGroup::getGroupId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<Long> getPtGroupGroupIds(Long id) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getMissionId, id);
        wrapper.isNotNull(ProjectGroup::getPtGroupId);
        return baseMapper.selectList(wrapper).stream().map(ProjectGroup::getGroupId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<ProjectGroup> listByUserId(Long userId) {
        return baseMapper.listByUserId(userId);
    }

    @Override
    public boolean removeByGroupId(Long groupId) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getGroupId, groupId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<ProjectGroup> listByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getProjectId, projectId)
                .isNull(ProjectGroup::getMissionId)
                .isNull(ProjectGroup::getPtGroupId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ProjectGroup> listByMissionId(Long missionId) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getMissionId, missionId)
                .isNotNull(ProjectGroup::getProjectId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ProjectGroup> listByPtGroupId(Long ptGroupId) {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getPtGroupId, ptGroupId)
                .isNotNull(ProjectGroup::getMissionId);
        return baseMapper.selectList(wrapper);
    }

}
