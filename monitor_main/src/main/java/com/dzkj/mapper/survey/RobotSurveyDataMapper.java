package com.dzkj.mapper.survey;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.survey.RobotSurveyData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:23
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface RobotSurveyDataMapper extends BaseMapper<RobotSurveyData> {

    /**
     * 查询任务最新一期监测数据
     *
     * @description 查询任务最新一期监测数据
     * @author jing.fang
     * @date 2023/6/9 14:34
     * @param missionId missionId
     * @return: com.dzkj.entity.survey.RobotSurveyData
     **/
    List<RobotSurveyData> getLatestSurveyData(@Param("missionId") Long missionId);
}
