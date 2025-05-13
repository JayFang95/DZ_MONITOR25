package com.dzkj.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.equipment.vo.ControlBoxVO;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.entity.equipment.ControlBox;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.mapper.equipment.ControlBoxMapper;
import com.dzkj.service.equipment.IControlBoxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class ControlBoxServiceImpl extends ServiceImpl<ControlBoxMapper, ControlBox> implements IControlBoxService {

    @Override
    public Page<ControlBox> getPage(Integer pageIndex, Integer pageSize, EquipCondition cond) {
        Page<ControlBox> page = new Page<>(pageIndex, pageSize);
        return baseMapper.getPage(page, cond);
    }

    @Override
    public List<ControlBox> getList(EquipCondition cond) {
        return baseMapper.getList(cond);
    }

    @Override
    public boolean findSerialNoByMission(ControlBoxVO data) {
        LambdaQueryWrapper<ControlBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ControlBox::getMissionId, data.getMissionId())
                .eq(ControlBox::getSerialNo, data.getSerialNo())
                .ne(data.getId() != null ,ControlBox::getId, data.getId());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean updateBindStatus(Long id, boolean bindStatus) {
        LambdaUpdateWrapper<ControlBox> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(ControlBox::getBindMission, bindStatus).eq(ControlBox::getId, id);
        return baseMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public int countBindSerialNo(Long id) {
        ControlBox data = baseMapper.selectById(id);
        if (data == null){
            return 0;
        }
        LambdaQueryWrapper<ControlBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ControlBox::getSerialNo, data.getSerialNo()).eq(ControlBox::getBindMission, true)
                .ne(ControlBox::getId, id);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean removeByProjectId(Long projectId) {
        LambdaQueryWrapper<ControlBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ControlBox::getProjectId, projectId);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByMissionId(Long missionId) {
        LambdaQueryWrapper<ControlBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ControlBox::getMissionId, missionId);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<ControlBox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ControlBox::getMissionId, missionIds);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean updateDeviceInfo(ControlBoxVO data) {
        LambdaUpdateWrapper<ControlBox> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ControlBox::getDeviceType, data.getDeviceType())
                .set(StringUtils.isNotEmpty(data.getDeviceInfo()), ControlBox::getDeviceInfo, data.getDeviceInfo())
                .eq(ControlBox::getId, data.getId());
        return baseMapper.update(null, wrapper) > 0;
    }

    @Override
    public List<String> getSerialNoList(Long missionId) {
        LambdaQueryWrapper<ControlBox> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ControlBox::getMissionId, missionId);
        List<ControlBox> list = baseMapper.selectList(wrapper);
        return list.stream().map(ControlBox::getSerialNo).collect(Collectors.toList());
    }

    @Override
    public RobotSurveyRecord getSurveyRecordInfoByControlBoxId(Long controlBoxId) {
        return baseMapper.getSurveyRecordInfoByControlBoxId(controlBoxId);
    }

    @Override
    public void updateSurvey(Long id, int survey) {
        LambdaUpdateWrapper<ControlBox> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ControlBox::getId, id)
                .set(ControlBox::getSurvey, survey);
        baseMapper.update(null, wrapper);
    }

    @Override
    public List<ControlBox> getMeteBoxListByMissionId(Long missionId) {
        LambdaQueryWrapper<ControlBox> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ControlBox::getId, ControlBox::getSerialNo, ControlBox::getName)
                .eq(ControlBox::getMissionId, missionId)
                .eq(ControlBox::getBindMission, true)
                .eq(ControlBox::getDeviceType, "温度气压控制器")
                .orderByDesc(ControlBox::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ControlBox> getControlBoxInfo(Long missionId, String serialNo) {
        LambdaQueryWrapper<ControlBox> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ControlBox::getMissionId, missionId)
                .eq(ControlBox::getSerialNo, serialNo);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ControlBox> getBindListByMissionId(Long missionId) {
        LambdaQueryWrapper<ControlBox> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ControlBox::getMissionId, missionId)
                .eq(ControlBox::getBindMission, true);
        return baseMapper.selectList(wrapper);
    }

}
