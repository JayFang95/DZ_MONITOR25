package com.dzkj.biz.data;

import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.bean.SurveyPoint;
import com.dzkj.biz.data.vo.PointDataXyzhDto;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/16 17:57
 * @description 三维位移测点业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPointDataXyzhBiz {

    /**
     * 批量获取指定Id值的测点初始测量(X0,Y0,Z0)列表
     * 最近一次工况为“破坏重埋”或“基准点修正"的测量值，若无，则取首次测量值；
     * 若无首次，测点First=true
     *
     * @param pIds           pIds
     * @param hasGroupSurvey hasGroupSurvey
     * @param recycleNum     recycleNum
     * @return List<SurveyPoint>
     */
    List<SurveyPoint> findFirstByPIds(List<Long> pIds, boolean hasGroupSurvey, int recycleNum);

    /**
     * 批量获取指定Id值的最后一次测量点列表
     * 库中不存在的点,测点First=true
     * @param pIds pIds
     * @param recycleNum recycleNum 当前比较测量周期
     * @return List<SurveyPoint>
     */
    List<SurveyPoint> findLastByPIds(List<Long> pIds, int recycleNum);

    /**
     * 获取临时加测结果集合
     *
     * @param finalResults finalResults
     * @param pidList      pidList
     * @param missionId    missionId
     * @param recycleNum   recycleNum
     * @return List<SurveyOnceResult>
     */
    List<SurveyOnceResult> getSurveyOnceResult(List<String> finalResults, List<Long> pidList, int missionId, int recycleNum);

    /**
     * 保存控制自动监测数据结果
     *
     * @param finalResults        xyz成果数据列表(id,name,x,y,z,ha,va,sd)
     * @param surveyData          原始测量数据 monitorItemId|stCfg|recycleNum|rawDatas|calDatas
     * @param surveyAtOnce        surveyAtOnce
     * @param hasGroupSurvey      hasGroupSurvey
     * @param surveyCfgPointIds   surveyCfgPointIds
     * @param groupIndex          groupIndex
     * @description 保存控制自动监测数据结果
     * @author jing.fang
     * @date 2023/3/24 14:19
     * @return: void
     **/
    PointDataXyzhDto saveRobotResultOnSuccess(List<String> finalResults, String surveyData, boolean hasGroup,
                                              boolean surveyAtOnce, boolean hasGroupSurvey, List<Long> surveyCfgPointIds, int groupIndex);

    /**
     * 保存失败记录
     *
     * @param getTime    getTime
     * @param missionId  missionId
     * @param surveyData surveyData
     * @description 保存失败记录
     * @author jing.fang
     * @date 2023/6/20 9:53
     * @return: void
     **/
    void saveRobotData(Date getTime, Long missionId, String surveyData);


    /**
     * 获取任务下测点id 集合
     *
     * @description 获取任务下测点id 集合
     * @author jing.fang
     * @date 2024/11/20 14:08
     * @param missionId missionId
     * @return: java.util.List<java.lang.Long>
     **/
    List<Long> getPointIdWithMission(Long missionId);
}
