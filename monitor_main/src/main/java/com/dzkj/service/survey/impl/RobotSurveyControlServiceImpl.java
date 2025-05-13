package com.dzkj.service.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.survey.vo.RobotSurveyCond;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.entity.survey.RobotSurveyControl;
import com.dzkj.mapper.survey.RobotSurveyControlMapper;
import com.dzkj.service.survey.IRobotSurveyControlService;
import org.apache.commons.lang3.StringUtils;
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
public class RobotSurveyControlServiceImpl extends ServiceImpl<RobotSurveyControlMapper, RobotSurveyControl> implements IRobotSurveyControlService {

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotSurveyControl::getMissionId, missionIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<RobotSurveyControl> findByMissionId(Long missionId) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyControl::getMissionId, missionId)
                .orderByDesc(RobotSurveyControl::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<RobotSurveyControl> findByCompanyId(Long companyId) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotSurveyControl::getCompanyId, companyId)
                .orderByDesc(RobotSurveyControl::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public RobotSurveyControl findControlInfo(RobotSurveyCond surveyCond) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyControl::getMissionId, surveyCond.getMissionId())
                .eq(RobotSurveyControl::getSerialNo, surveyCond.getSerialNo());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean removeBySerialNo(String serialNo) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyControl::getSerialNo, serialNo);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<RobotSurveyControl> getStationTreeData(RobotSurveyControlGroupVo data) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        if (data.getParams() == null) {
            wrapper.eq(RobotSurveyControl::getMissionId, data.getMissionId())
                    .eq(RobotSurveyControl::getGroupId, -1);
        } else {
            wrapper.eq(RobotSurveyControl::getMissionId, data.getMissionId())
                    .in(RobotSurveyControl::getGroupId, -1, data.getId());
        }
        wrapper.orderByDesc(RobotSurveyControl::getSerialNo);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void updateGroupId(RobotSurveyControlGroupVo data) {
        LambdaUpdateWrapper<RobotSurveyControl> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RobotSurveyControl::getMissionId, data.getMissionId())
                .in(RobotSurveyControl::getSerialNo, data.getSerialNos())
                .set(RobotSurveyControl::getGroupId, data.getId());
        baseMapper.update(null, wrapper);
        LambdaUpdateWrapper<RobotSurveyControl> wrapper2 = new LambdaUpdateWrapper<>();
        wrapper2.eq(RobotSurveyControl::getMissionId, data.getMissionId())
                .eq(RobotSurveyControl::getGroupId, data.getId())
                .notIn(RobotSurveyControl::getSerialNo, data.getSerialNos())
                .set(RobotSurveyControl::getGroupId, -1);
        baseMapper.update(null, wrapper2);
    }

    @Override
    public void updateGroupId(Long groupId) {
        LambdaUpdateWrapper<RobotSurveyControl> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RobotSurveyControl::getGroupId, groupId)
                .set(RobotSurveyControl::getGroupId, -1);
        baseMapper.update(null, wrapper);
    }

    @Override
    public List<RobotSurveyControl> findByMissionIdAndSerialNo(Long missionId, String serialNo) {
        LambdaQueryWrapper<RobotSurveyControl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyControl::getMissionId, missionId)
                .eq(StringUtils.isNotEmpty(serialNo), RobotSurveyControl::getSerialNo, serialNo)
                .orderByDesc(RobotSurveyControl::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

}
