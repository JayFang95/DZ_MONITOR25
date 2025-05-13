package com.dzkj.biz.survey.impl;

import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.survey.IRobotSurveyControlGroupBiz;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.biz.survey.vo.SurveyBackInfoVo;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.job.RobotSurveyJobService;
import com.dzkj.robot.multi.MultiStationAo;
import com.dzkj.robot.multi.MultiSurveyHandler;
import com.dzkj.robot.survey.MultiSurveyBiz;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskOtherService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.survey.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/19 15:56
 * @description 多站联测义务接口实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class RobotSurveyControlGroupBizImpl implements IRobotSurveyControlGroupBiz {

    @Autowired
    private IRobotSurveyControlGroupService controlGroupService;
    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private ISurveyCycleService surveyCycleService;
    @Autowired
    private IRobotSurveyDataService robotSurveyDataService;
    @Autowired
    private RobotSurveyJobService surveyJobService;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IPointDataXyzhBiz dataXyzhBiz;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IRobotSurveyRecordService robotSurveyRecordService;
    @Autowired
    private IPushTaskOtherService pushTaskOtherService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    @Autowired
    private IAlarmInfoService infoService;
    @Autowired
    private IAlarmInfoCorrectService infoCorrectService;
    @Autowired
    private QwMsgService qwMsgService;

    @Override
    public List<RobotSurveyControlGroupVo> getList(Long missionId) {
        List<RobotSurveyControlGroupVo> list = DzBeanUtils.listCopy(controlGroupService.listByMissionId(missionId), RobotSurveyControlGroupVo.class);
        List<MultiStationAo> stations = MultiSurveyHandler.getAllStations();
        for (RobotSurveyControlGroupVo groupVo : list) {
            Optional<MultiStationAo> optional = stations.stream()
                    .filter(item -> item.getId().equals(groupVo.getId())).findAny();
            optional.ifPresent(item -> groupVo.setSurveyStatus(item.getSurveyStatus()));
        }
        return list;
    }

    @Override
    public ResponseUtil add(RobotSurveyControlGroupVo data) {
        boolean b = controlGroupService.add(data);
        if(b) {
            //添加到多站联测测站队列
            MultiStationAo station = DzBeanUtils.propertiesCopy(data, MultiStationAo.class);
            station.setSurveyStatus("停测");
            MultiSurveyBiz multiSurveyBiz = new MultiSurveyBiz(
                    station, dataXyzhBiz, surveyCycleService, robotSurveyDataService,
                    surveyJobService, controlBoxService, pushTaskService,
                    robotSurveyRecordService, controlGroupService, pushTaskOtherService,
                    dataXyzhService, dataXyzhCorrectService, infoService, infoCorrectService,
                    qwMsgService);
            station.setMultiSurveyBiz(multiSurveyBiz);
            MultiSurveyHandler.getAllStations().add(station);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "新增失败");
    }

    @Override
    public ResponseUtil update(RobotSurveyControlGroupVo data) {
        boolean b = controlGroupService.edit(data);
        if(b) {
            //更新多测站联测对象信息
            Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                    .filter(item -> item.getId().equals(data.getId()))
                    .findAny();
            optional.ifPresent(item -> {
                        item.setName(data.getName());
                        item.setParams(StringUtils.isEmpty(data.getParams()) ? null : data.getParams());
                        item.getMultiSurveyBiz().updateInit();
                    });
            //更新控制器多测站联测编组
            if (data.isSurveyCfg()) {
                ArrayList<ControlBoxAo> currentBoxList = new ArrayList<>();
                for (ControlBoxAo box : ControlBoxHandler.getAllControlBoxes()) {
                    //更新控制器多站编组信息
                    if (box.getMissionId().equals(data.getMissionId()) && data.getSerialNos().contains(box.getSerialNo())) {
                        box.setGroupId(data.getId());
                        box.setGroupInfo(data.getName());
                        box.setStationInfo(box.getStationInfo().split(",")[0] + "," + data.getName());
                        optional.ifPresent(item -> {
                            box.getSurveyBiz().setMultiStation(true);
                            box.getSurveyBiz().setMultiOperateCompleted(item.getMultiSurveyBiz().getSurveyOperateCompleted());
                            box.getSurveyBiz().initMultiData(data.getParams());
                        });
                        currentBoxList.add(box);
                    }
                    if (data.getId().equals(box.getGroupId()) && !data.getSerialNos().contains(box.getSerialNo())) {
                        box.setGroupId(-1L);
                        box.setGroupInfo(null);
                        box.setStationInfo(box.getStationInfo().split(",")[0]);
                        box.getSurveyBiz().setMultiOperateCompleted(null);
                        box.getSurveyBiz().setMultiStation(false);
                        // 2024-03-11 取消多站编组是否需要恢复单站设置
                        box.getSurveyBiz().initData();
                    }
                }
                optional.ifPresent(item -> item.getMultiSurveyBiz().setControlBoxList(currentBoxList));
                //更新RobotSurveyControl表多站编组id
                surveyControlService.updateGroupId(data);
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = controlGroupService.removeById(id);
        if (b) {
            //删除多测站联测信息
            Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                    .filter(item -> item.getId().equals(id))
                    .findAny();
            optional.ifPresent(item -> MultiSurveyHandler.getAllStations().remove(item));
            //更新控制器多测站联测编组
            for (ControlBoxAo box : ControlBoxHandler.getAllControlBoxes()) {
                //更新控制器多站编组信息
                if (id.equals(box.getGroupId())) {
                    box.setGroupId(-1L);
                    box.setGroupInfo(null);
                    box.setStationInfo(box.getStationInfo().split(",")[0]);
                    box.getSurveyBiz().setMultiOperateCompleted(null);
                    // 2024-03-11 取消多站编组是否需要恢复单站设置
                    box.getSurveyBiz().initData();
                }
            }
            //更新RobotSurveyControl表多站编组id
            surveyControlService.updateGroupId(id);
        }
        return b ? ResponseUtil.success(): ResponseUtil.failure(500, "删除失败");
    }

    @Override
    public ResponseUtil getMultiSurveyStatusInfo(Long id) {
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(id)).findAny();
        String surveyInfo = "";
        if (optional.isPresent()) {
            MultiSurveyBiz surveyBiz = optional.get().getMultiSurveyBiz();
            surveyInfo = surveyBiz.getSurveyStatusInfo();
        }
        return ResponseUtil.success(surveyInfo);
    }

    @Override
    public List<RobotSurveyControlVO> getStationTreeData(RobotSurveyControlGroupVo data) {
        List<RobotSurveyControlVO> list = DzBeanUtils.listCopy(surveyControlService.getStationTreeData(data), RobotSurveyControlVO.class);
        ArrayList<RobotSurveyControlVO> result = new ArrayList<>();
        for (RobotSurveyControlVO controlVO : list) {
            //只保留状态为'停测'的控制器
            Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> item.getSerialNo().equals(controlVO.getSerialNo())
                            && item.getMissionId().equals(data.getMissionId())
                            && "停测".equals(item.getSurveyStatus())).findAny();
            optional.ifPresent(item -> result.add(controlVO));
        }
        return result;
    }

    @Override
    public ResponseUtil startMultiSurvey(Long multiStationId) {
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(multiStationId)).findAny();
        if (optional.isPresent()) {
            optional.get().getMultiSurveyBiz().start(new Date());
            return ResponseUtil.success(true);
        } else {
            return ResponseUtil.success(false);
        }
    }

    @Override
    public ResponseUtil startOnceMultiSurvey(Long multiStationId) {
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(multiStationId)).findAny();
        if (optional.isPresent()) {
            if (!"待测".equals(optional.get().getSurveyStatus())) {
                return ResponseUtil.success(false);
            }
            optional.get().getMultiSurveyBiz().startAtOnce();
            return ResponseUtil.success(true);
        } else {
            return ResponseUtil.success(false);
        }
    }

    @Override
    public ResponseUtil stopMultiSurvey(Long multiStationId) {
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(multiStationId)).findAny();
        if (optional.isPresent()) {
            optional.get().getMultiSurveyBiz().stop();
            return ResponseUtil.success(true);
        } else {
            return ResponseUtil.success(false);
        }
    }

    @Override
    public SurveyBackInfoVo getMultiSurveyInfo(Long multiStationId) {
        SurveyBackInfoVo backInfoVo = new SurveyBackInfoVo();
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(multiStationId)).findAny();
        if (optional.isPresent()){
            MultiSurveyBiz surveyBiz = optional.get().getMultiSurveyBiz();
            if (surveyBiz == null){
                return null;
            }
            String statusInfo = surveyBiz.getSurveyStatusInfo();
            backInfoVo.setStatusInfo(statusInfo);
            backInfoVo.setSurveyStatus(optional.get().getSurveyStatus());
            backInfoVo.setStartSurveying("在测".equals(optional.get().getSurveyStatus()));
            backInfoVo.setBackInfos(surveyBiz.getSurveyBackInfos());
            return backInfoVo;
        }else {
            return null;
        }
    }

    @Override
    public boolean updateMultiStatus(List<String> serialNoList) {
        if (serialNoList == null || serialNoList.size() == 0) {
            return false;
        }
        long onLineCount = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(item -> serialNoList.contains(item.getSerialNo()) && "在线".equals(item.getStatus())).count();
        return onLineCount == serialNoList.size();
    }

    @Override
    public List<SurveyOnceResult> getMultiOnceResult(Long multiId) {
        Optional<MultiStationAo> optional = MultiSurveyHandler.getAllStations().stream()
                .filter(item -> item.getId().equals(multiId)).findAny();
        List<SurveyOnceResult> list = new ArrayList<>();
        if (optional.isPresent()){
            MultiSurveyBiz surveyBiz = optional.get().getMultiSurveyBiz();
            if (surveyBiz == null){
                return list;
            }
            //id,name,x,y,z,ha,va,sd
            List<String> finalResults = surveyBiz.getFinalResults();
            //原始测量数据 monitorItemId|stCfg|recycleNum|rawDatas|calDatas|ajReport
            String surveyData = surveyBiz.getSurveyData();
            if (finalResults.size() == 0 || StringUtils.isEmpty(surveyData)){
                return list;
            }
            List<Long> pidList = finalResults.stream().map(item -> Long.valueOf(item.split(",")[0])).collect(Collectors.toList());
            String[] split = surveyData.split("\\|");
            list = dataXyzhBiz.getSurveyOnceResult(finalResults, pidList, Integer.parseInt(split[0]), Integer.parseInt(split[2]));
        }
        return list;
    }

}
