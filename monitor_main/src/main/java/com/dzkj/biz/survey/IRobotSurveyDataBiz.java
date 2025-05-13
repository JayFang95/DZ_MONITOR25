package com.dzkj.biz.survey;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IRobotSurveyDataBiz {

    /**
     * 查询三维自动监测任务最新的测量周期数
     *
     * @description 查询三维自动监测任务最新的测量周期数
     * @author jing.fang
     * @date 2023/3/20 9:06
     * @param missionId missionId
     * @return: int
     **/
    int getLastRecycleNumByMissionId(Long missionId);

    /**
     * 查询三维自动监测任务最新保存报告时间
     *
     * @description 查询三维自动监测任务最新保存报告时间
     * @author jing.fang
     * @date 2023/3/20 9:06
     * @param missionId missionId
     * @return: int
     **/
    Date getLatestTime(int recycleNum, Long missionId);
}
