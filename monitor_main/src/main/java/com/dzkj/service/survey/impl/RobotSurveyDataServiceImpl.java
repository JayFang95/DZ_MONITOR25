package com.dzkj.service.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.mapper.survey.RobotSurveyDataMapper;
import com.dzkj.service.survey.IRobotSurveyDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:24
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class RobotSurveyDataServiceImpl extends ServiceImpl<RobotSurveyDataMapper, RobotSurveyData> implements IRobotSurveyDataService {

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotSurveyData::getMissionId, missionIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<RobotSurveyData> getByMissionId(Long missionId) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getMissionId, missionId)
                .isNotNull(RobotSurveyData::getRecycleNum)
                .orderByDesc(RobotSurveyData::getRecycleNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<RobotSurveyData> getList(OtherDataCondition condition) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getMissionId, condition.getMissionId())
                .eq(RobotSurveyData::getAuto, true)
                .ge(condition.getCreateTime() != null, RobotSurveyData::getCreateTime, condition.getCreateTime())
                .orderByDesc(RobotSurveyData::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Page<RobotSurveyData> getPage(Integer pi, Integer ps, OtherDataCondition condition) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getMissionId, condition.getMissionId())
                .eq(RobotSurveyData::getAuto, true)
                .ge(condition.getCreateTime() != null, RobotSurveyData::getCreateTime, condition.getCreateTime())
                .orderByDesc(RobotSurveyData::getCreateTime);
        return baseMapper.selectPage(new Page<>(pi, ps), wrapper);
    }

    @Override
    public boolean removeOnceData(int recycleNum, long missionId) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getMissionId, missionId)
                .eq(RobotSurveyData::getRecycleNum, recycleNum);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<RobotSurveyData> getLatestSurveyData(Long missionId) {
        return baseMapper.getLatestSurveyData(missionId);
    }

    @Override
    public List<RobotSurveyData> findByRecycleNum(int recycleNum, Long missionId) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getMissionId, missionId)
                .eq(RobotSurveyData::getRecycleNum, recycleNum);
        return baseMapper.selectList(wrapper);
    }

}
