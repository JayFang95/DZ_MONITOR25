package com.dzkj.service.survey;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.survey.vo.RobotSurveyCond;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.entity.survey.RobotSurveyControl;

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
public interface IRobotSurveyControlService extends IService<RobotSurveyControl> {

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:02
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);

    /**
     * 根据任务查询配置信息
     *
     * @description 根据任务查询配置信息
     * @author jing.fang
     * @date 2023/3/10 9:29
     * @param missionId missionId
     * @return: com.dzkj.entity.survey.RobotSurveyControl
    **/
    List<RobotSurveyControl> findByMissionId(Long missionId);

    /**
     * 查询公司下控制列表
     *
     * @description 查询公司下控制列表
     * @author jing.fang
     * @date 2023/3/10 11:01
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.entity.survey.RobotSurveyControl>
     **/
    List<RobotSurveyControl> findByCompanyId(Long companyId);

    /**
     * 查询控制器信息
     *
     * @description 查询控制器信息
     * @author jing.fang
     * @date 2023/3/13 13:41
     * @param surveyCond surveyCond
     * @return: com.dzkj.entity.survey.RobotSurveyControl
     **/
    RobotSurveyControl findControlInfo(RobotSurveyCond surveyCond);

    /**
     * 删除指定控制器配置
     *
     * @description 删除指定控制器配置
     * @author jing.fang
     * @date 2023/3/20 15:20
     * @param serialNo serialNo
     * @return: boolean
     **/
    boolean removeBySerialNo(String serialNo);

    /**
     * 查詢多测站任务测站数据
     *
     * @description 查詢多测站任务测站数据
     * @author jing.fang
     * @date 2024/3/5 13:39
     * @param data data
     * @return: java.util.List<com.dzkj.entity.survey.RobotSurveyControl>
     **/
    List<RobotSurveyControl> getStationTreeData(RobotSurveyControlGroupVo data);

    /**
     * 更新多站编组信息
     *
     * @description 更新多站编组信息
     * @author jing.fang
     * @date 2024/3/6 13:53
     * @param data data
     * @return: void
     **/
    void updateGroupId(RobotSurveyControlGroupVo data);
    /**
     * 更新控制器组信息
     *
     * @description 更新控制器组信息
     * @author jing.fang
     * @date 2024/3/11 14:36
     * @param groupId groupId
     * @return: void
     **/
    void updateGroupId(Long groupId);
    /**
     * 按需求查询
     *
     * @description 按需求查询
     * @author jing.fang
     * @date 2024/3/11 14:36
     * @param missionId missionId
     * @param serialNo serialNo
     * @return: java.util.List<com.dzkj.entity.survey.RobotSurveyControl>
     **/

    List<RobotSurveyControl> findByMissionIdAndSerialNo(Long missionId, String serialNo);

}
