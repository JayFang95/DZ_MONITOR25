package com.dzkj.service.survey;

import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.entity.survey.RobotSurveyControlGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 监测控制组服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IRobotSurveyControlGroupService extends IService<RobotSurveyControlGroup> {

    /**
     * 查询多站联测信息
     *
     * @description 查询多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:23
     * @param missionId 任务id
     * @return: java.util.List<com.dzkj.entity.survey.RobotSurveyControlGroup>
     **/
    List<RobotSurveyControlGroup> listByMissionId(Long missionId);

    /**
     * 新增多站联测信息
     *
     * @description 新增多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:23
     * @param data 多站联测信息
     * @return: boolean
     **/
    boolean add(RobotSurveyControlGroupVo data);

    /**
     * 修改多站联测信息
     *
     * @description 修改多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:23
     * @param data 多站联测信息
     * @return: boolean
     **/
    boolean edit(RobotSurveyControlGroupVo data);

    /**
     * 更新测量状态
     *
     * @description 更新测量状态
     * @author jing.fang
     * @date 2024/3/11 16:52
     * @param id 测站id
     * @param survey 状态值
     * @return: void
     **/
    void updateSurvey(Long id, int survey);
}
