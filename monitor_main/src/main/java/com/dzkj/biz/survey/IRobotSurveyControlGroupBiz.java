package com.dzkj.biz.survey;

import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.biz.survey.vo.SurveyBackInfoVo;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/19 15:56
 * @description 多站联测业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IRobotSurveyControlGroupBiz {

    /**
     * 多站联测查询
     *
     * @description 多站联测查询
     * @author jing.fang
     * @date 2024/2/19 16:03
     * @param missionId 监测任务id
     * @return: java.util.List<com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo>
     **/
    List<RobotSurveyControlGroupVo> getList(Long missionId);

    /**
     * 新增多站联测信息
     *
     * @description 新增多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:03
     * @param data 多站联测信息
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(RobotSurveyControlGroupVo data);

    /**
     * 修改多站联测信息
     *
     * @description 修改多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:03
     * @param data 多站联测信息
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(RobotSurveyControlGroupVo data);

    /**
     * 删除多站联测信息
     *
     * @description 删除多站联测信息
     * @author jing.fang
     * @date 2024/2/19 16:05
     * @param id 多站id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil delete(Long id);

    /**
     * 获取多测站联测状态信息
     *
     * @description 获取多测站联测状态信息
     * @author jing.fang
     * @date 2024/3/4 15:39
     * @param id 多测站id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil getMultiSurveyStatusInfo(Long id);

    /**
     * 获取多站联测测站数据
     *
     * @description 获取多站联测测站数据
     * @author jing.fang
     * @date 2024/3/5 13:30
     * @param data data
     * @return: java.util.List<com.dzkj.biz.survey.vo.RobotSurveyControlVO>
     **/
    List<RobotSurveyControlVO> getStationTreeData(RobotSurveyControlGroupVo data);

    /**
     * 开测
     *
     * @description 开测
     * @author jing.fang
     * @date 2024/3/6 16:22
     * @param multiStationId multiStationId
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil startMultiSurvey(Long multiStationId);

    /**
     * 临时加测
     *
     * @description 临时加测
     * @author jing.fang
     * @date 2024/3/6 16:22
     * @param multiStationId multiStationId
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil startOnceMultiSurvey(Long multiStationId);

    /**
     * 停测
     *
     * @description 停测
     * @author jing.fang
     * @date 2024/3/6 16:22
     * @param multiStationId multiStationId
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil stopMultiSurvey(Long multiStationId);

    /**
     * 获取多站联测过程信息
     *
     * @description 获取多站联测过程信息
     * @author jing.fang
     * @date 2024/3/6 17:12
     * @param multiStationId multiStationId
     * @return: com.dzkj.biz.survey.vo.SurveyBackInfoVo
     **/
    SurveyBackInfoVo getMultiSurveyInfo(Long multiStationId);

    /**
     * 更新多站联测控制器在线状态
     *
     * @description 更新多站联测控制器在线状态
     * @author jing.fang
     * @date 2024/3/13 13:24
     * @param serialNoList serialNoList
     * @return: boolean
     **/
    boolean updateMultiStatus(List<String> serialNoList);

    /**
     * 获取多站联测加测结果
     * @param multiId 多站id
     * @return 加测结果
     */
    List<SurveyOnceResult> getMultiOnceResult(Long multiId);
}
