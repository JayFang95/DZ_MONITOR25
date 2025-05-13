package com.dzkj.runner;

import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.survey.IRobotSurveyDataBiz;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.survey.RobotSurveyControl;
import com.dzkj.entity.survey.RobotSurveyControlGroup;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.job.RobotSurveyJobService;
import com.dzkj.robot.multi.MultiStationAo;
import com.dzkj.robot.multi.MultiSurveyHandler;
import com.dzkj.robot.socket.netty.NettyServer;
import com.dzkj.robot.socket.netty2.NettyMeteServer;
import com.dzkj.robot.survey.MultiSurveyBiz;
import com.dzkj.robot.survey.SurveyBiz;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskOtherService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.survey.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/18
 * @description 初始化ControlBox信息
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
@Slf4j
@Order(0)
public class ControlBoxInitRunner implements CommandLineRunner {

    @Value("${netty.port}")
    private int nettyPort;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private ISurveyCycleService surveyCycleService;
    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private IRobotSurveyControlGroupService surveyControlGroupService;
    @Autowired
    private IPointDataXyzhBiz dataXyzhBiz;
    @Autowired
    private IRobotSurveyDataBiz surveyDataBiz;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private RobotSurveyJobService surveyJobService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IRobotSurveyRecordService robotSurveyRecordService;
    @Autowired
    private IRobotSurveyControlGroupService controlGroupService;
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
    public void run(String... args) throws Exception {
        log.info("开始初始化控制器信息。。。");
        List<ControlBoxAo> list = DzBeanUtils.listCopy(controlBoxService.list(), ControlBoxAo.class);
        List<Long> missionIds = list.stream().map(ControlBoxAo::getMissionId).collect(Collectors.toList());
        List<ProMission> missions = new ArrayList<>();
        List<Project> projects = new ArrayList<>();
        if (missionIds.size() > 0){
            missions = missionService.listByIds(missionIds);
            List<Long> projectIds = missions.stream().map(ProMission::getProjectId).collect(Collectors.toList());
            projects = projectService.listByIds(projectIds);
        }
        List<RobotSurveyControl> controlList = surveyControlService.list();
        for (ControlBoxAo ao : list) {
            ao.setStatus("离线");
            ao.setSurveyStatus("停测");
            Optional<ProMission> anyMission = missions.stream().filter(it -> it.getId().equals(ao.getMissionId())).findAny();
            if (anyMission.isPresent()){
                ao.setMissionName(anyMission.get().getName());
                Optional<Project> anyPro = projects.stream().filter(it -> it.getId().equals(anyMission.get().getProjectId())).findAny();
                anyPro.ifPresent(it -> ao.setProjectName(it.getName()));
            }
            Optional<RobotSurveyControl> optional = controlList.stream().filter(item -> item.getMissionId().equals(ao.getMissionId())
                    && Objects.equals(item.getSerialNo(), ao.getSerialNo())).findAny();
            // 设置测站信息
            optional.ifPresent(surveyControl -> {
                ao.setGroupId(surveyControl.getGroupId());
                ao.setSurveyConfigInfo(surveyControl.getParams());
                ao.setStationInfo(surveyControl.getStationConfig());
                if (surveyControl.getGroupId()!=-1) {
                    RobotSurveyControlGroup controlGroup = controlGroupService.getById(surveyControl.getGroupId());
                    if (controlGroup != null) {
                        ao.setStationInfo(surveyControl.getStationConfig() + "," + controlGroup.getName());
                    }
                }
            });
            SurveyBiz surveyBiz = new SurveyBiz(ao,
                    dataXyzhBiz,
                    surveyDataBiz,
                    surveyJobService,
                    pushTaskService,
                    robotSurveyRecordService,
                    controlBoxService,
                    surveyCycleService,
                    surveyControlService,
                    surveyControlGroupService,
                    pushTaskOtherService,
                    dataXyzhService,
                    dataXyzhCorrectService,
                    infoService,
                    infoCorrectService,
                    qwMsgService);
            // 设置是否多站
            surveyBiz.setMultiStation(ao.getGroupId()!=-1);
            ao.setSurveyBiz(surveyBiz);
        }
        ControlBoxHandler.getAllControlBoxes().addAll(list);
        log.info("控制器信息初始化成功");

        // 需要在控制器信息初始化之后初始化socket服务器
        log.info("多测站信息初始化开始...");
        List<MultiStationAo> stationAos = DzBeanUtils.listCopy(controlGroupService.list(), MultiStationAo.class);
        for (MultiStationAo station : stationAos) {
            //设置测站状态
            station.setSurveyStatus("停测");
            //设置测站业务对象
            MultiSurveyBiz multiSurveyBiz = new MultiSurveyBiz(
                    station,
                    dataXyzhBiz,
                    surveyCycleService,
                    surveyDataService,
                    surveyJobService,
                    controlBoxService,
                    pushTaskService,
                    robotSurveyRecordService,
                    controlGroupService,
                    pushTaskOtherService,
                    dataXyzhService,
                    dataXyzhCorrectService,
                    infoService,
                    infoCorrectService,
                    qwMsgService
            );
            //设置多站控制器
            ArrayList<ControlBoxAo> currentBoxList = new ArrayList<>();
            if (StringUtils.isNotEmpty(station.getParams())) {
                String[] paramsArray = station.getParams().split("\\|");
                for (int i = 3; i < paramsArray.length; i++) {
                    String serialNo = paramsArray[i].split(";")[0].split(",")[1];
                    Optional<ControlBoxAo> optional = list.stream()
                            .filter(item -> item.getMissionId().equals(station.getMissionId())
                                    && item.getSerialNo().equals(serialNo)).findAny();
                    optional.ifPresent(item -> {
                        item.setGroupInfo(station.getName());
                        item.getSurveyBiz().setMultiOperateCompleted(multiSurveyBiz.getSurveyOperateCompleted());
                        item.getSurveyBiz().initData();
                        currentBoxList.add(item);
                    });
                }
            }
            multiSurveyBiz.setControlBoxList(currentBoxList);
            station.setMultiSurveyBiz(multiSurveyBiz);
        }
        MultiSurveyHandler.getAllStations().addAll(stationAos);
        log.info("多测站信息初始化成功");

        // 2023/6/26 功能调整：系统重启自启动之前的任务
        for (ControlBoxAo boxAo : list) {
            if(boxAo.getSurvey() == 1 && boxAo.getGroupId() == -1){
                boxAo.getSurveyBiz().start(new Date());
            }
        }
        // 2024/3/7 完善多站重启
        for (MultiStationAo stationAo : stationAos) {
            if(stationAo.getSurvey() == 1){
                stationAo.getMultiSurveyBiz().start(new Date());
            }
        }
        NettyServer.initNettyServer(nettyPort);
        NettyMeteServer.initNettyServer(nettyPort + 1);
    }

}
