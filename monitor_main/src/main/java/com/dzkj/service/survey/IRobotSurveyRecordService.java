package com.dzkj.service.survey;

import com.dzkj.entity.survey.RobotSurveyRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/19
 * @description 测量记录服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IRobotSurveyRecordService extends IService<RobotSurveyRecord> {

    /**
     * 删除任务下所有记录
     * @param missionIds missionIds
     */
    void removeByMissionIds(List<Long> missionIds);
}
