package com.dzkj.service.alarm_setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.mapper.alarm_setting.AlarmInfoMapper;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class AlarmInfoServiceImpl extends ServiceImpl<AlarmInfoMapper, AlarmInfo> implements IAlarmInfoService {

    @Override
    public List<AlarmInfo> getMaxLevel(List<Long> projectIds) {
        return baseMapper.getMaxLevel(projectIds);
    }

    @Override
    public List<AlarmInfo> listByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<AlarmInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AlarmInfo::getMissionId, missionIds);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean removeOnceData(int recycleNum, long missionId) {
        LambdaQueryWrapper<AlarmInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmInfo::getMissionId, missionId).eq(AlarmInfo::getRecycleNum, recycleNum);
        return baseMapper.delete(wrapper) > 0;
    }

}
