package com.dzkj.service.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.project.ProMission;
import com.dzkj.mapper.project.ProMissionMapper;
import com.dzkj.service.project.IProMissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/3
 * @description 监测任务服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class ProMissionServiceImpl extends ServiceImpl<ProMissionMapper, ProMission> implements IProMissionService {

    @Override
    public IPage<ProMissionVO> getPage(Integer pageIndex, Integer pageSize, ProMissionCondition cond) {
        IPage<ProMission> result = baseMapper.getPage(new Page<>(pageIndex, pageSize), cond);
        return DzBeanUtils.pageCopy(result, ProMissionVO.class);
    }

    @Override
    public List<ProMissionVO> getList(ProMissionCondition cond) {
        List<ProMission> result = baseMapper.queryList(cond);
        return DzBeanUtils.listCopy(result, ProMissionVO.class);
    }

    @Override
    public List<ProMission> findByName(ProMissionVO mission) {
        LambdaQueryWrapper<ProMission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProMission::getName, mission.getName())
                .eq(ProMission::getProjectId, mission.getProjectId())
                .ne(mission.getId()!=null, ProMission::getId, mission.getId());
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ProMission> getMissionByProjectId(Long projectId) {
        LambdaQueryWrapper<ProMission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProMission::getProjectId,projectId)
                .orderByDesc(ProMission::getIdx);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ProMission> getList(Long projectId) {
        return baseMapper.getList(projectId);
    }

    @Override
    public List<ProMission> getList(List<Long> missionIds) {
        return baseMapper.getList2(missionIds);
    }

    @Override
    public List<ProMission> getListInProjIds(List<Long> projIds){
        return baseMapper.getListInProjIds(projIds);
    }

    @Override
    public ProMission findById(Long missionId) {
        return baseMapper.findById(missionId);
    }

    @Override
    public Integer getIndex(ProMissionVO mission) {
        LambdaQueryWrapper<ProMission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProMission::getProjectId, mission.getProjectId()).orderByDesc(ProMission::getIdx);
        List<ProMission> list = baseMapper.selectList(wrapper);
        return list.size() > 0 ? list.get(0).getIdx() : 0;
    }

    @Override
    public List<ProMission> getMissionInCompany(Long companyId, Long missionId) {
        return baseMapper.getMissionInCompany(companyId, missionId);
    }


    @Override
    public List<ProMission> getMissionOtherInCompany(Long companyId, Long missionId) {
        return baseMapper.getMissionOtherInCompany(companyId, missionId);
    }
}
