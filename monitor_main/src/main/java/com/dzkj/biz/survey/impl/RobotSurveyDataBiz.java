package com.dzkj.biz.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.survey.IRobotSurveyDataBiz;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.service.survey.IRobotSurveyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Service
public class RobotSurveyDataBiz implements IRobotSurveyDataBiz {

    @Autowired
    private IRobotSurveyDataService surveyDataService;

    @Override
    public int getLastRecycleNumByMissionId(Long missionId) {
        List<RobotSurveyData> surveyDataList = surveyDataService.getByMissionId(missionId);
        if (!surveyDataList.isEmpty()){
            return surveyDataList.get(0).getRecycleNum();
        }
        return 0;
    }

    @Override
    public Date getLatestTime(int recycleNum, Long missionId) {
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyData::getRecycleNum,recycleNum)
                .eq(RobotSurveyData::getMissionId,missionId)
                .select(RobotSurveyData::getCreateTime).orderByDesc(RobotSurveyData::getCreateTime);
        List<RobotSurveyData> list = surveyDataService.list(wrapper);
        return !list.isEmpty() ? list.get(0).getCreateTime() : null;
    }
}
