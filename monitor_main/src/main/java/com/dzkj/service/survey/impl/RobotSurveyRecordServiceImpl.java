package com.dzkj.service.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.mapper.survey.RobotSurveyRecordMapper;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/19
 * @description 测量记录服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class RobotSurveyRecordServiceImpl extends ServiceImpl<RobotSurveyRecordMapper, RobotSurveyRecord> implements IRobotSurveyRecordService {

    @Override
    public void removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<RobotSurveyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotSurveyRecord::getMissionId, missionIds);
        baseMapper.delete(wrapper);
    }
}
