package com.dzkj.service.alarm_setting.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.alarm_setting.AlarmGroup;
import com.dzkj.mapper.alarm_setting.AlarmGroupMapper;
import com.dzkj.service.alarm_setting.IAlarmGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 报警组服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class AlarmGroupServiceImpl extends ServiceImpl<AlarmGroupMapper, AlarmGroup> implements IAlarmGroupService {

    @Override
    public List<AlarmGroup> getAlarmList(Long projectId, Long missionId) {
        return baseMapper.getAlarmList(projectId, missionId);
    }
}
