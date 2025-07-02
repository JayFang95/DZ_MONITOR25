package com.dzkj.biz.survey;

import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.biz.survey.vo.OnlineCfgResultVo;
import com.dzkj.biz.survey.vo.RobotSurveyCond;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.biz.survey.vo.SurveyBackInfoVo;
import com.dzkj.common.util.ResponseUtil;

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
public interface IRobotSurveyControlBiz {

    /**
     * 查询当前用户单位下控制器列表
     *
     * @description 查询当前用户单位下控制器列表
     * @author jing.fang
     * @date 2023/3/10 10:49
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.biz.survey.vo.RobotSurveyControlVO>
    **/
    List<RobotSurveyControlVO> getList(Long companyId);

    /**
     * 查询指定控制器配置信息
     *
     * @description 查询指定控制器配置信息
     * @author jing.fang
     * @date 2023/3/13 13:34
     * @param surveyCond surveyCond
     * @return: com.dzkj.biz.survey.vo.RobotSurveyControlVO
     **/
    RobotSurveyControlVO findControlInfo(RobotSurveyCond surveyCond);

    /**
     * 保存控制器配置信息
     *
     * @description 保存控制器配置信息
     * @author jing.fang
     * @date 2023/3/13 13:36
     * @param surveyControlVO surveyControlVO
     * @return: com.dzkj.biz.survey.vo.RobotSurveyControlVO
     **/
    ResponseUtil saveControlInfo(RobotSurveyControlVO surveyControlVO);

    /**
     * 测站验证
     *
     * @description 测站验证
     * @author jing.fang
     * @date 2023/3/20 17:55
     * @param controlBoxId controlBoxId
     * @return: void
     **/
    boolean checkStation(Long controlBoxId);

    /**
     * 测点验证
     *
     * @description 测点验证
     * @author jing.fang
     * @date 2023/3/22 13:29
     * @param controlBoxId controlBoxId
     * @param resultVo resultVo
     * @return: boolean
     **/
    boolean checkPoint(Long controlBoxId, OnlineCfgResultVo resultVo);

    /**
     * 刷新挂接仪器类型
     *
     * @description 刷新挂接仪器类型
     * @author jing.fang
     * @date 2023/3/21 10:52
     * @param controlBoxId controlBoxId
     * @param deviceType deviceType
     * @return: boolean
     **/
    boolean refreshControlBox(Long controlBoxId, Integer deviceType);

    /**
     * 开机
     *
     * @description 开机
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean openDevice(Long controlBoxId);

    /**
     * 关机
     *
     * @description 关机
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean closeDevice(Long controlBoxId);

    /**
     * 打开激光
     *
     * @description 打开激光
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean openLaser(Long controlBoxId);

    /**
     * 关闭激光
     *
     * @description 关闭激光
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean closeLaser(Long controlBoxId);

    /**
     * 正倒棱镜
     *
     * @description 正倒棱镜
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean changeFace(Long controlBoxId);

    /**
     * 打开补偿
     *
     * @description 打开补偿
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean openComp(Long controlBoxId);

    /**
     * 测量测试
     *
     * @description 测量测试
     * @author jing.fang
     * @date 2023/3/21 11:36
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean surveyTest(Long controlBoxId);

    /**
     * ha va值转换
     *
     * @description ha va值转换
     * @author jing.fang
     * @date 2023/3/23 15:29
     * @param cfgResultVo cfgResultVo
     * @return: com.dzkj.biz.survey.vo.OnlineCfgResultVo
     **/
    OnlineCfgResultVo calculateHv(OnlineCfgResultVo cfgResultVo);

    /**
     * 测量结果计算
     *
     * @description 测量结果计算
     * @author jing.fang
     * @date 2023/3/21 15:12
     * @param cfgResultVo cfgResultVo
     * @return: com.dzkj.biz.survey.vo.OnlineCfgResultVo
     **/
    OnlineCfgResultVo calculate(OnlineCfgResultVo cfgResultVo);

    /**
     * 测点验证计算
     *
     * @description 测点验证计算
     * @author jing.fang
     * @date 2023/3/22 14:13
     * @param cfgResultVo cfgResultVo
     * @return: com.dzkj.biz.survey.vo.OnlineCfgResultVo
     **/
    OnlineCfgResultVo calculatePoint(OnlineCfgResultVo cfgResultVo);

    /**
     * 开启仪器测量
     *
     * @description 开启仪器测量
     * @author jing.fang
     * @date 2023/3/22 15:27
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean startSurvey(Long controlBoxId);

    /**
     * 获取测量过程信息
     *
     * @description 获取测量过程信息
     * @author jing.fang
     * @date 2023/3/22 15:58
     * @param controlBoxId controlBoxId
     * @return: com.dzkj.biz.survey.vo.SurveyBackInfoVo
     **/
    SurveyBackInfoVo getSurveyInfo(Long controlBoxId);

    /**
     * 停止仪器测量
     *
     * @description 停止仪器测量
     * @author jing.fang
     * @date 2023/3/23 9:05
     * @param controlBoxId equipId
     * @return: boolean
     **/
    boolean stopSurvey(Long controlBoxId);

    /**
     * 临时加测
     *
     * @description 临时加测
     * @author jing.fang
     * @date 2023/3/23 9:27
     * @param controlBoxId equipId
     * @return: boolean
     **/
    boolean startOnce(Long controlBoxId);

    /**
     * 获取临时加测计算结果
     *
     * @description 获取临时加测计算结果
     * @author jing.fang
     * @date 2023/3/28 8:54
     * @param controlBoxId controlBoxId
     * @return: java.util.List<com.dzkj.bean.SurveyOnceResult>
     **/
    List<SurveyOnceResult> getOnceResult(Long controlBoxId);

    /**
     * 删除临时加测结果
     *
     * @description 删除临时加测结果
     * @author jing.fang
     * @date 2023/3/28 15:15
     * @param resultList resultList
     * @return: boolean
     **/
    boolean deleteOnce(List<SurveyOnceResult> resultList);

    /**
     * 开始温度气压测量
     *
     * @description 开始温度气压测量
     * @author jing.fang
     * @date 2024/3/25 17:30
     * @param serialNo serialNo
     * @param controlBoxId controlBoxId
     * @return: boolean
     **/
    boolean meteSurvey(String serialNo, Long controlBoxId);

    /**
     * DTU振弦采集测量
     *
     * @description DTU振弦采集测量
     * @author jing.fang
     * @date 2025/1/2 9:55
     * @param serialNo serialNo
     * @param equipId equipId
     * @return: boolean
     **/
    boolean vibrationWireSurvey(String serialNo, Long equipId);

    /**
     * 声光报警器测试
     * @param serialNoList serialNoList
     * @return boolean
     */
    boolean soundLightTest(List<String> serialNoList);
}
