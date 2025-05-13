package com.dzkj.service.survey;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.entity.survey.RobotSurveyData;

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
public interface IRobotSurveyDataService extends IService<RobotSurveyData> {

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:03
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);

    /**
     * 根据任务ID查询
     *
     * @description 根据任务ID查询
     * @author jing.fang
     * @date 2023/3/10 14:03
     * @param missionId missionId
     * @return: com.dzkj.entity.survey.RobotSurveyData
     **/
    List<RobotSurveyData> getByMissionId(Long missionId);

    /**
     * 查询其他页数据
     *
     * @description 查询其他页数据
     * @author jing.fang
     * @date 2023/3/27 9:56
     * @param condition condition
     * @return: java.util.List<com.dzkj.entity.survey.RobotSurveyData>
     **/
    List<RobotSurveyData> getList(OtherDataCondition condition);

    /**
     * 分页查询其他页数据
     *
     * @description 分页查询其他页数据
     * @author jing.fang
     * @date 2023/3/27 9:56
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.survey.RobotSurveyData>
     **/
    Page<RobotSurveyData> getPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 删除临时加测数据
     * @param recycleNum recycleNum
     * @param missionId missionId
     * @return boolean
     */
    boolean removeOnceData(int recycleNum, long missionId);

    /**
     * 获取任务最新一期监测数据
     *
     * @description 获取任务最新一期监测数据
     * @author jing.fang
     * @date 2023/6/9 14:30
     * @param missionId missionId
     * @return: com.dzkj.entity.survey.RobotSurveyData
     **/
    List<RobotSurveyData> getLatestSurveyData(Long missionId);

    /**
     * 查询监测数据
     *
     * @description 查询监测数据
     * @author jing.fang
     * @date 2023/6/9 16:33
     * @param recycleNum recycleNum
     * @param missionId missionId
     * @return: com.dzkj.entity.survey.RobotSurveyData
     **/
    List<RobotSurveyData> findByRecycleNum(int recycleNum, Long missionId);
}
