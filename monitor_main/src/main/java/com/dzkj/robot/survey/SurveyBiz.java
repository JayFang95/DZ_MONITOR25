package com.dzkj.robot.survey;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.bean.*;
import com.dzkj.biz.SurveyResultProcess;
import com.dzkj.biz.SurveyResultProcessNewClassicUpdate;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.data.vo.PointDataXyzhDto;
import com.dzkj.biz.survey.IRobotSurveyDataBiz;
import com.dzkj.common.Angle;
import com.dzkj.common.CommonUtil;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.FileUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmInfoCorrect;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataXyzhCorrect;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.data.PushTaskOther;
import com.dzkj.entity.equipment.ControlBox;
import com.dzkj.entity.survey.RobotSurveyControlGroup;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.entity.survey.SurveyCycle;
import com.dzkj.enums.DeviceType;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.bean.MultiOperateCompleteResult;
import com.dzkj.robot.bean.OperateCompleteResult;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.box.ControlBoxMete;
import com.dzkj.robot.job.*;
import com.dzkj.robot.job.common.MonitorJobUtil;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskOtherService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.survey.IRobotSurveyControlGroupService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import com.dzkj.service.survey.ISurveyCycleService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/17
 * @description 测量业务类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Getter
@Setter
@Slf4j
public class SurveyBiz {

    //region 数据
    /**
     * 配置好的测量点列表(第1点为测站点，其余为监测点)
     **/
    private List<SurveyCfgPoint> surveyCfgPoints = new ArrayList<>();
    private List<SurveyCfgPoint> surveyCfgPointsAll = new ArrayList<>();
    private List<List<SurveyCfgPoint>> surveyCfgPointGroup = new ArrayList<>();
    /**
     * 配置好的预测量测量点列表(第1点为测站点，其余为监测点;当不需要预测时，长度为0)
     */
    private List<SurveyCfgPoint> preSurveyCfgPoints = new ArrayList<>();
    private List<SurveyCfgPoint> preSurveyCfgPointsAll = new ArrayList<>();
    private List<List<SurveyCfgPoint>> preSurveyCfgPointGroup = new ArrayList<>();
    /**
     * 预测失败时，最多重测次数
     */
    private int preSurveyNum;
    /**
     * 当前预测量次数号
     */
    private int currentPreSurveyIndex;

    /**
     * 测点初始测量点列表(尚未测量的点，First=true)
     **/
    private List<SurveyPoint> surveyPoints0 = new ArrayList<>();

    /**
     * 测量限差参数类
     **/
    private LimitParams limitParams;

    /**
     * 当前测量周期
     **/
    private String currentSurveyCycleCfg;
    /**
     * 时间点
     * 起始时间
     * 周期间隔:天
     */
    private List<LocalTime> surveyTimes = new ArrayList<>();
    private List<Date> dateList = new ArrayList<>();
    private int cycleInterval = 1;
    /**
     * 当前测量点序号
     **/
    private int currentSurveyPointIndex;
    /**
     * 当前测回数序号
     **/
    private int currentChIndex;
    /**
     * 当前目标遮挡重测次数序号
     **/
    private int currentRepeatIndexCover;
    /**
     * 当前坐标偏差超限重测次数序号
     **/
    private int currentRepeatIndexRunOver;
    /**
     * 当前数据处理失败后重测点组次数序号
     **/
    private int currentRepeatIndexFail;

    /**
     * 测量时间信息--用于测量期间状态信息显示
     * 定时任务赋值
     **/
    private String surveyDateTimeInfo;
    /**
     * 当前测量期数
     **/
    private int recycleNum;
    /**
     * 是否是正镜测量--用于测量期间状态信息显示
     **/
    private boolean face1;

    /**
     * 控制器掉线时间(掉线3分钟以内继续测量，3分钟以外不再继续)
     **/
    private Date offlineTime;

    /**
     * 通用测量业务类
     **/
    private IDeviceBiz deviceBiz;
    /**
     * 温度气压传感器业务类
     */
    private IDeviceMeteBiz deviceMeteBiz;
    /**
     * 测量回调
     **/
    private Consumer<OperateCompleteResult> surveyOperateCompleted;

    /**
     * 多站测量回调
     **/
    private Consumer<MultiOperateCompleteResult> multiSurveyOperateCompleted;

    /**
     * 当前控制器(业务)
     **/
    private ControlBoxBo controlBoxBo;
    /**
     * 当前控制器(业务-温度气压)
     */
    private ControlBoxMete currentBoxMete;
    /**
     * 关联控制器
     **/
    private ControlBoxAo controlBoxAo;

    /**
     * 气象信息字符串--"manual,温度,气压,相对湿度 | serialNo,温度,气压,相对湿度"
     */
    private String meteInfo;

    /**
     * 报警器配置信息--"manual| serialNo,serialNo;报警时间;漏测,漏测,超限"
     */
    private String soundAlarmCfg = "manual";
    /**
     * 数据后处理方法
     */
    private int processMethod;
    /**
     * 是否多站联测
     */
    private boolean multiStation;
    /**
     *  是否需要预测
     */
    private boolean needPreSurvey;
    /**
     * 是否正在预测量
     */
    private boolean preSurveying;
    /**
     * 测量结束
     */
    private boolean surveyFinished;
    /**
     * 测点测点完成
     */
    private boolean surveyFinishSuccess;
    /**
     * 数据后处理后--平差报告
     */
    private String adjReport;

    /**
     * 测量成果列表
     **/
    private final List<SurveyResult> surveyResults = new ArrayList<>();
    /**
     * 预测量关键点测量成果列表
     */
    private final List<SurveyResult> preSurveyResults = new ArrayList<>();
    /**
     * 测量计算成果列表
     **/
    private List<SurveyCalcResult> surveyCalcResults = new ArrayList<>();
    /**
     * 测量计算成果列表_用于保存计算数据到数据库
     **/
    private List<SurveyCalcResult> surveyCalcResults2Db = new ArrayList<>();

    /**
     * 是否临时加测
     **/
    private boolean surveyAtOnce;
    /**
     * 预测成功
     */
    private boolean preSurveyOk;
    /**
     * 是否正在测量
     **/
    private boolean startSurveying;

    /**
     * 测量回传信息
     **/
    private List<String> surveyBackInfos = new ArrayList<>();
    /**
     * 数据后处理结果字符串列表(id,name,x,y,z,ha,va,sd)
     **/
    private List<String> finalResults = new ArrayList<>();
    /**
     * 测量数据
     **/
    private String surveyData = "";
    /**
     * 数据计算成功标识(由于结束测量，引发结束事件时，判断最后是否处理成功)
     **/
    private boolean finalOk;

    private SurveyStation surveyStation;

    /**
     * 2024/3/14 取消时是否真的停止：多站时单站测量失败会取消测量但不停止
     */
    private boolean realStop;

    /**
     * 最后一次测量时间
     */
    private Date surveyTime;
    /**
     * 添加已经开始的监测点，防止重复暂停
     */
    private Set<Long> surveyPointIds = new HashSet<>();
    /**
     * 是否设置了编组测量
     */
    private boolean hasGroupSurvey = false;
    /**
     * 是否存在编组测量成功的
     */
    private boolean hasSurveySuccess = false;
    /**
     * 当前测量编组序号
     */
    private int currentGroupIndex = 0;
    /**
     * 编组测量睡眠时间
     */
    private List<Integer> groupSleepTimes = new ArrayList<>();
    /**
     * 开机失败重试次数
     */
    private int openDeviceRetryTime = 1;
    //endregion

    private IPointDataXyzhBiz dataXyzhBiz;
    private IRobotSurveyDataBiz surveyDataBiz;
    private RobotSurveyJobService surveyJobService;
    private IPushTaskService pushTaskService;
    private IRobotSurveyRecordService robotSurveyRecordService;
    private IControlBoxService controlBoxService;
    private ISurveyCycleService surveyCycleService;
    private IRobotSurveyControlService surveyControlService;
    private IRobotSurveyControlGroupService surveyControlGroupService;
    private IPushTaskOtherService pushTaskOtherService;
    private IPointDataXyzhService dataXyzhService;
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    private IAlarmInfoService infoService;
    private IAlarmInfoCorrectService infoCorrectService;
    private QwMsgService qwMsgService;

    public SurveyBiz(ControlBoxAo controlBoxAo, IPointDataXyzhBiz pointDataXyzhService
            , IRobotSurveyDataBiz robotSurveyDataService, RobotSurveyJobService jobService
            , IPushTaskService pushTaskService, IRobotSurveyRecordService recordService
            , IControlBoxService controlBoxService, ISurveyCycleService surveyCycleService
            , IRobotSurveyControlService surveyControlService, IRobotSurveyControlGroupService surveyControlGroupService
            , IPushTaskOtherService pushTaskOtherService, IPointDataXyzhService dataXyzhService
            , IPointDataXyzhCorrectService dataXyzhCorrectService, IAlarmInfoService infoService
            , IAlarmInfoCorrectService infoCorrectService, QwMsgService qwMsgService){
        this.controlBoxAo = controlBoxAo;
        this.dataXyzhBiz = pointDataXyzhService;
        this.surveyDataBiz = robotSurveyDataService;
        this.surveyJobService = jobService;
        this.pushTaskService = pushTaskService;
        this.robotSurveyRecordService = recordService;
        this.controlBoxService = controlBoxService;
        this.surveyCycleService = surveyCycleService;
        this.surveyControlService = surveyControlService;
        this.surveyControlGroupService = surveyControlGroupService;
        this.pushTaskOtherService = pushTaskOtherService;
        this.dataXyzhService = dataXyzhService;
        this.dataXyzhCorrectService = dataXyzhCorrectService;
        this.infoService = infoService;
        this.infoCorrectService = infoCorrectService;
        this.qwMsgService = qwMsgService;
        //初始化
        init();
    }

    private void init(){
        this.offlineTime = DateUtil.getDate("2022-01-01 00:00:00", DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        //仪器测量回调
        this.surveyOperateCompleted = this::handleSurveyOperateResult;
        this.deviceMeteBiz = new DeviceMeteBiz(this.surveyOperateCompleted);
        initData();
        refreshCurrentBoxBo();
    }

    /**
     * 设置多测站回调
     * @param multiSurveyOperateCompleted multiSurveyOperateCompleted
     */
    public void setMultiOperateCompleted(Consumer<MultiOperateCompleteResult> multiSurveyOperateCompleted){
        this.multiSurveyOperateCompleted = multiSurveyOperateCompleted;
    }
    //region 初始化方法
    /**
     * 刷新当前控制器(业务)
     **/
    public void refreshCurrentBoxBo(){
        Optional<ControlBoxBo> optional = ControlBoxHandler.getOnlineControlBoxes().stream()
                .filter(item -> Objects.equals(item.getSerialNo(), controlBoxAo.getSerialNo()))
                .findAny();
        controlBoxBo = optional.orElse(null);
        //刷新控制器(业务)中挂接仪器的品牌类型--重要，直接影响仪器命令类型
        if (controlBoxBo != null) {
            switch (controlBoxAo.getDeviceType()) {
                case 0:
                    controlBoxBo.setDeviceType(DeviceType.NULL);
                    deviceBiz = null;
                    return;
                case 1:
                    controlBoxBo.setDeviceType(DeviceType.LEI_CA);
                    deviceBiz = new DeviceBizLeica(this.surveyOperateCompleted);
                    break;
                default:
            }
            deviceBiz.refreshCurrentBoxBo(this.controlBoxBo);
        }else {
            deviceBiz = null;
        }
    }

    /**
     * 设置多站配置
     */
    public void initMultiData(String params){
        if (StringUtils.isEmpty(params)) {
            return;
        }
        String[] msParams = params.split("\\|");
        //获取测量周期配置
        try {
            if(Integer.parseInt(msParams[2]) <= 0){
                this.currentSurveyCycleCfg = "";
            } else {
                SurveyCycle cycle = this.surveyCycleService.getById(Integer.parseInt(msParams[2]));
                if(cycle != null){
                    this.currentSurveyCycleCfg = cycle.getParams();
                    parseSurveyCycleCfg();
                } else {
                    this.currentSurveyCycleCfg = "";
                }
            }
        } catch (NumberFormatException e) {
            log.error("多测站配置测量策略id转换异常:{}", e.getMessage());
        }
        //获取预测量关键点配置信息
        String preSurveyParams = msParams[1];
        if ("False".equals(preSurveyParams)) {
            return;
        }
        //通过设备测站名称找到对应测站配置:测站名称,多测站名称
        String[] stInfoArr = controlBoxAo.getStationInfo().split(",");
        Optional<String> optional = Arrays.stream(msParams).filter(item -> item.contains(stInfoArr[0])).findFirst();
        if (optional.isPresent()) {
            String theParams = optional.get().substring(optional.get().indexOf(";"));
            preSurveyParams += theParams;
            getPreSurveyPtsFromParams(preSurveyParams);
        }
        //是否需要预测量关键点
        setNeedPreSurvey(!preSurveyCfgPoints.isEmpty());
    }

    /**
     * 初始化测量信息
     **/
    public void initData() {
        //清除策略周期时间数据
        surveyTimes.clear();
        dateList.clear();
        cycleInterval = 1;
        //尚未挂接测量配置信息
        if (StringUtils.isEmpty(controlBoxAo.getSurveyConfigInfo())) {
            return;
        }
        //获取是否多测站配置
        multiStation = -1 != controlBoxAo.getGroupId();
        //预测量标识初始为True,只要有一点预测不成功，则置为False,中断测量
        preSurveyOk = true;
        if (isMultiStation()) {
            initDataMultiStation();
        }else {
            initDataSingleStation();
        }
    }

    /**
     * 初始化数据-单测站
     */
    private void initDataSingleStation(){
        //测量参数配置
        controlBoxAo.setGroupInfo("无");
        //获取测站配置列表
        String[] paramStr = controlBoxAo.getSurveyConfigInfo().split("\\|");
        if (StringUtils.isNotEmpty(paramStr[0])){
            String[] ptsParams = paramStr[0].split(";");
            getPtsFromParams(ptsParams);
        }
        //获取限差参数配置
        limitParams = new LimitParams();
        if (StringUtils.isNotEmpty(paramStr[1])){
            String[] limitParams = paramStr[1].split(";");
            //获取限差参数配置
            this.limitParams = getLimitsFromParams(limitParams);
        }
        //获取测量周期配置
        try {
            if(Integer.parseInt(paramStr[2]) <= 0){
                this.currentSurveyCycleCfg = "";
            } else {
                SurveyCycle cycle = this.surveyCycleService.getById(Integer.parseInt(paramStr[2]));
                if(cycle != null){
                    this.currentSurveyCycleCfg = cycle.getParams();
                    parseSurveyCycleCfg();
                } else {
                    this.currentSurveyCycleCfg = "";
                }
            }
        } catch (NumberFormatException e) {
            log.error("单测站配置测量策略id转换异常:{}", e.getMessage());
        }
        //获取气象信息(测量方式,温度、气压、湿度)
        meteInfo = paramStr[3];
        //获取预测量关键点配置信息
        getPreSurveyPtsFromParams(paramStr[4]);
        //是否需要预测量关键点
        needPreSurvey = !preSurveyCfgPoints.isEmpty();
        //获取数据后处理方法
        processMethod = Integer.parseInt(paramStr[5]);
        //是否配置了编组
        hasGroupSurvey = paramStr.length == 7 && StringUtils.isNotEmpty(paramStr[6]) && !"False".equals(paramStr[6]);
        if (hasGroupSurvey) {
            getSurveyGroupConfig(paramStr[6]);
            //确认有成功获取到有效的编组配置，再对编组测量配置标识赋值
            hasGroupSurvey = !this.surveyCfgPointGroup.isEmpty();
        }
        //报警器配置
        if(paramStr.length == 8 && !StringUtils.isEmpty(paramStr[7])){
            this.soundAlarmCfg = paramStr[7];
        }
    }

    /**
     * 初始化数据-多测站
     */
    private void initDataMultiStation(){
        //获取测站配置列表
        String[] paramStr = controlBoxAo.getSurveyConfigInfo().split("\\|");
        if (StringUtils.isNotEmpty(paramStr[0])){
            String[] ptsParams = paramStr[0].split(";");
            getPtsFromParams(ptsParams);
        }
        //获取限差参数配置
        limitParams = new LimitParams();
        if (StringUtils.isNotEmpty(paramStr[1])){
            String[] limitParams = paramStr[1].split(";");
            //获取限差参数配置
            this.limitParams = getLimitsFromParams(limitParams);
        }
        //获取气象信息(测量方式,温度、气压、湿度)
        meteInfo = paramStr[3];
        //从多测站配置信息中获取：测量周期信息、预测量关键点信息
        RobotSurveyControlGroup controlGroup = surveyControlGroupService.getById(controlBoxAo.getGroupId());
        //完善关联控制器VO中的测站信息
        controlBoxAo.setStationInfo(controlBoxAo.getStationInfo().split(",")[0] + "," + controlGroup.getName());
        controlBoxAo.setGroupInfo(controlGroup.getName());
        //初始化测量周期和预测点信息
        this.initMultiData(controlGroup.getParams());
    }

    /**
     * 解析周期策略时间
     */
    private void parseSurveyCycleCfg(){
        String[] cycleCfgParams = this.currentSurveyCycleCfg.split(",");
        for (int i = 0; i < cycleCfgParams.length; i++) {
            String param = cycleCfgParams[i];
            if (i == 0) {
                dateList.add(DateUtil.getDate(param, DateUtil.yyyy_MM_dd_EN));
            }
            if (i == 1) {
                Date date = DateUtil.getDate(param, DateUtil.yyyy_MM_dd_EN);
                Calendar calendar = Calendar.getInstance();
                if (date != null) {
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                dateList.add(calendar.getTime());
            }
            if(i == 2){
                cycleInterval = Integer.parseInt(param);
            }
            if ( i > 2) {
                surveyTimes.add(LocalTime.parse(param, DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        }
    }

    /**
     * 从配置参数中获取测量编组配置点信息
     * @param surveyGroupParams 测量编组配置信息字符串
     */
    private void getSurveyGroupConfig(String surveyGroupParams) {
        this.surveyCfgPointGroup.clear();
        this.preSurveyCfgPointGroup.clear();
        this.groupSleepTimes.clear();
        String[] paramsInfos = surveyGroupParams.split(";");
        for (String paramsInfo : paramsInfos) {
            List<SurveyCfgPoint> cfgPoints = new ArrayList<>();
            List<SurveyCfgPoint> preCfgPoints = new ArrayList<>();
            //添加测站点为第一点
            cfgPoints.add(this.surveyCfgPoints.get(0));
            if(!this.preSurveyCfgPoints.isEmpty()){
                preCfgPoints.add(this.preSurveyCfgPoints.get(0));
            }
            String[] ptInfos = paramsInfo.split(",");
            for (int i = 0; i < ptInfos.length; i++) {
                if (i != ptInfos.length - 1) {
                    long pid = Long.parseLong(ptInfos[i]);
                    Optional<SurveyCfgPoint> optional = surveyCfgPointsAll.stream().filter(item -> item.getId() == pid).findAny();
                    Optional<SurveyCfgPoint> optionalPre = preSurveyCfgPointsAll.stream().filter(item -> item.getId() == pid).findAny();
                    optional.ifPresent(cfgPoints::add);
                    optionalPre.ifPresent(preCfgPoints::add);
                }
            }
            //存在有点测点再添加
            if (cfgPoints.size() > 1){
                this.surveyCfgPointGroup.add(cfgPoints);
                this.preSurveyCfgPointGroup.add(preCfgPoints);
                this.groupSleepTimes.add(Integer.parseInt(ptInfos[ptInfos.length - 1]));
            }
        }
    }

    /**
     * 从配置参数字符串中获取预测量关键点配置信息列表
     * @param preSurveyParams 预测量关键点配置信息字符串
     */
    private void getPreSurveyPtsFromParams(String preSurveyParams){
        this.preSurveyCfgPointsAll.clear();
        this.preSurveyCfgPoints.clear();
        if ("False".equals(preSurveyParams)) {
            return;
        }
        //获取关键点信息列表
        String[] paramsInfos = preSurveyParams.split(";");
        preSurveyNum = Integer.parseInt(paramsInfos[0]);
        //第1点位测站点
        this.preSurveyCfgPointsAll.add(this.surveyCfgPoints.get(0));
        this.preSurveyCfgPoints.add(this.surveyCfgPoints.get(0));
        for (int i = 1; i < paramsInfos.length; i++) {
            String paramsInfo = paramsInfos[i];
            String[] ptInfos = paramsInfo.split(",");
            int ptId = Integer.parseInt(ptInfos[0]);
            Optional<SurveyCfgPoint> optional = surveyCfgPoints.stream().filter(item -> item.getId() == ptId).findAny();
            optional.ifPresent(surveyCfgPoint -> {
                this.preSurveyCfgPointsAll.add(surveyCfgPoint);
                this.preSurveyCfgPoints.add(surveyCfgPoint);
            });
        }
    }

    /**
     * 从配置参数字符列表中获取配置测点列表
     * @param configParams id name 稳定/固定 x y z hi/ht ha va
     */
    private void getPtsFromParams(String[] configParams) {
        this.surveyCfgPointsAll.clear();
        this.surveyCfgPoints.clear();
        for (int i = 0; i < configParams.length; i++) {
            String[] infos = configParams[i].split(",");
            SurveyCfgPoint pt = new SurveyCfgPoint();
            pt.setId(Long.parseLong(infos[0]));
            pt.setName(infos[1]);
            pt.setX(Double.parseDouble(infos[3]));
            pt.setY(Double.parseDouble(infos[4]));
            pt.setZ(Double.parseDouble(infos[5]));

            if (i == 0) {
                pt.setStation(true);
                pt.setStable("1".equals(infos[2]));
                //获取仪器高、目标高
                pt.setHi(Double.parseDouble(infos[6]));
                pt.setSleepTime(0);
            } else {
                pt.setAsFixed("1".equals(infos[2]));
                //获取仪器高、目标高
                pt.setHt(Double.parseDouble(infos[6]));
                pt.setHa(Double.parseDouble(infos[7]));
                pt.setVa(Double.parseDouble(infos[8]));
                pt.setSleepTime(infos.length == 10 ? Integer.parseInt(infos[9]) : 0);
            }
            this.surveyCfgPointsAll.add(pt);
            this.surveyCfgPoints.add(pt);
        }
    }

    /**
     * 从参数字符列表获取限差参数配置
     * @param configParams limitParams
     **/
    private LimitParams getLimitsFromParams(String[] configParams) {
        LimitParams limitParams = new LimitParams();
        //开关机及测回
        String[] infos = configParams[0].split(",");
        limitParams.setTurnOffWhenOk("1".equals(infos[0]));
        //针对0.5测回，调整测回数为0--20230529
        infos[1] = "0.5".equals(infos[1]) ? "0" : infos[1];
        limitParams.setChNum(Integer.parseInt(infos[1]));
        //电子气泡偏量限差(弧度)
        infos = configParams[1].split(",");
        limitParams.setLimitT(Double.parseDouble(infos[0]));
        limitParams.setLimitL(Double.parseDouble(infos[1]));
        limitParams.setLimitT(Angle.deg2Rad(limitParams.getLimitT() / 3600.0, false));
        limitParams.setLimitL(Angle.deg2Rad(limitParams.getLimitL() / 3600.0, false));
        //自动搜索范围(弧度)
        infos = configParams[2].split(",");
        limitParams.setRangHa(Integer.parseInt(infos[0]));
        limitParams.setRangVa(Integer.parseInt(infos[1]));
        limitParams.setRangHa(Angle.deg2Rad(limitParams.getRangHa(), false));
        limitParams.setRangVa(Angle.deg2Rad(limitParams.getRangVa(), false));
        //目标遮挡处理
        infos = configParams[3].split(",");
        limitParams.setPauseTimeCover(Integer.parseInt(infos[0]));
        limitParams.setRepeatNumCover(Integer.parseInt(infos[1]));
        //坐标偏量限差(m)
        infos = configParams[4].split(",");
        limitParams.setLimitX(Double.parseDouble(infos[0]));
        limitParams.setLimitY(Double.parseDouble(infos[1]));
        limitParams.setLimitZ(Double.parseDouble(infos[2]));
        //测量失败处理
        infos = configParams[5].split(",");
        limitParams.setRepeatNumRunOver(Integer.parseInt(infos[0]));
        limitParams.setRepeatNumFail(Integer.parseInt(infos[1]));
        limitParams.setCPointFailedNum(Integer.parseInt(infos[2]));
        //测回内限差(弧度)
        infos = configParams[6].split(",");
        limitParams.setDHa0(Double.parseDouble(infos[0]));
        limitParams.setDVa0(Double.parseDouble(infos[1]));
        limitParams.setD2C(Double.parseDouble(infos[2]));
        limitParams.setDi(Double.parseDouble(infos[3]));
        limitParams.setDHa0(Angle.deg2Rad(limitParams.getDHa0() / 3600.0, false));
        limitParams.setDVa0(Angle.deg2Rad(limitParams.getDVa0() / 3600.0, false));
        limitParams.setD2C(Angle.deg2Rad(limitParams.getD2C() / 3600.0, false));
        limitParams.setDi(Angle.deg2Rad(limitParams.getDi() / 3600.0, false));
        //测回间限差(弧度)
        infos = configParams[7].split(",");
        limitParams.setDHa(Double.parseDouble(infos[0]));
        limitParams.setDVa(Double.parseDouble(infos[1]));
        limitParams.setDSd(Double.parseDouble(infos[2]));
        limitParams.setDHa(Angle.deg2Rad(limitParams.getDHa() / 3600.0, false));
        limitParams.setDVa(Angle.deg2Rad(limitParams.getDVa() / 3600.0, false));
        return limitParams;
    }
    //endregion

    //region 完成事件
    private void onMeteActionCompleted(OperateCompleteResult result) {
        //发送结果到页面
        try {
            WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_DATA.getCode(),
                    SocketMsgConst.CONTROL_DATA.getMessage() + "_"
                            + (deviceMeteBiz.getControlBoxMete() != null ? deviceMeteBiz.getControlBoxMete().getSerialNo() : "设备不存在"),
                    result));
        } catch (IOException e) {
            log.warn("onMeteActionCompleted socket发送失败，原因：{}", e.getMessage());
        }
    }

    /**
     * 测量完成事件处理
     * @param result result
     */
    private void onActionCompleted(OperateCompleteResult result) {
        //发送结果到页面
        try {
            WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_DATA.getCode(),
                    SocketMsgConst.CONTROL_DATA.getMessage() + "_" + controlBoxAo.getSerialNo(),
                    result));
        } catch (IOException e) {
            log.warn("onActionCompleted socket发送失败，原因：{}", e.getMessage());
        }
    }

    /**
     * 测量子过程完成事件处理
     * @param result result
     */
    private void onSubActionCompleted(OperateCompleteResult result) {
        //发送结果到页面
        try {
            WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_DATA_SUB.getCode(),
                    SocketMsgConst.CONTROL_DATA_SUB.getMessage() + "_" + controlBoxAo.getSerialNo(),
                    result));
        } catch (IOException e) {
            log.warn("onSubActionCompleted socket发送失败，原因：{}", e.getMessage());
        }
    }
    //endregion

    /**
     * 启动测量开关开启
     * @param startTime 启动时间
     */
    public void start(Date startTime){
        //清空测量结果
        surveyResults.clear();
        surveyBackInfos.clear();

        //处于待测状态
        controlBoxAo.setSurveyStatus("待测");
        this.surveyAtOnce = false;
        // 2023/6/26 记录状态
        controlBoxService.updateSurvey(controlBoxAo.getId(), 1);
        //配置了测量策略，启动系统监测
        if (StringUtils.isNotEmpty(currentSurveyCycleCfg)) {
            //立即启动监测，后续交给系统计时器事件
            this.monitor(startTime);
        }
        //激发开测事件
        if (multiSurveyOperateCompleted != null) {
            multiSurveyOperateCompleted.accept(new MultiOperateCompleteResult(false, null, null));
        }
        this.onActionCompleted(new OperateCompleteResult(null, null, null));
    }

    /**
     * 临时加测
     */
    public void startAtOnce(){
        //开始测量
        hasSurveySuccess = false;
        currentGroupIndex = 0;
        openDeviceRetryTime = 1;
        //获得测量周期数
        recycleNum = surveyDataBiz.getLastRecycleNumByMissionId(controlBoxAo.getMissionId()) + 1;
        //开始测量
        //仪器从关机状态到开机可能需要十几秒的时间，期间不允许“停止测量”
        this.controlBoxAo.setSurveyStatus("在测");
        startSurvey(false);
    }

    /**
     * 定时任务测量
     */
    public void startTask(){
        //仪器操作业务类尚未初始化，或在测(测量超时或临时加测情况),或离线
        if (deviceBiz==null || "在测".equals(controlBoxAo.getSurveyStatus())) {
            if("在测".equals(controlBoxAo.getSurveyStatus()) && surveyTime != null && new Date().getTime() - surveyTime.getTime() > 60 * 5000) {
                //开始测量
                hasSurveySuccess = false;
                currentGroupIndex = 0;
                openDeviceRetryTime = 1;
                //仪器从关机状态到开机可能需要十几秒的时间，期间不允许“停止测量”
                this.controlBoxAo.setSurveyStatus("在测");
                this.surveyAtOnce = false;
                startSurvey(false);
                return;
            }
            String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 错过计划测量时间点!";
            surveyBackInfos.add(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
            log.info("{} {} -- {}", surveyInfo, deviceBiz ,controlBoxAo.getSurveyStatus());

            if(!surveyAtOnce){
                // 2024/11/26 漏测处理,判断是否加测
                qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
            }
        } else {
            //开始测量
            hasSurveySuccess = false;
            currentGroupIndex = 0;
            openDeviceRetryTime = 1;
            //仪器从关机状态到开机可能需要十几秒的时间，期间不允许“停止测量”
            this.controlBoxAo.setSurveyStatus("在测");
            this.surveyAtOnce = false;
            startSurvey(false);
        }
    }

    /**
     * 停止仪器测量
     */
    public void stop(){
        // 2023/6/26 记录状态
        controlBoxService.updateSurvey(controlBoxAo.getId(), 0);
        //关闭定时任务
        surveyJobService.removeJob(MonitorJobConst.JOB_PREFIX + controlBoxAo.getSerialNo()
                , MonitorJobConst.TRIGGER_PREFIX + controlBoxAo.getSerialNo());
        //仪器操作业务类尚未初始化，或没有在测--改变测量状态后，立即返回
        if (deviceBiz == null || !"在测".equals(controlBoxAo.getSurveyStatus())) {
            //激发停测事件
            this.onActionCompleted(new OperateCompleteResult(null, null, null));
            prepareStopSurveying("停测",false, true);
            return;
        }

        //中断正在进行的测量操作
        realStop = true;
        deviceBiz.cancelOperate();
        // 恢复停测状态
        controlBoxAo.setSurveyStatus("停测");
        controlBoxBo.setProcessCommandStatus(3);
        this.startSurveying = false;
        this.surveyAtOnce = false;
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] >>> 停止测量 <<<";
        this.surveyBackInfos.add(surveyInfo);
        qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
        //激发停测事件
        this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, null, null));
    }

    /**
     * 取消测量
     */
    public void cancelSurveying(){
        if (!"在测".equals(controlBoxAo.getSurveyStatus())) {
            return;
        }
        //中断正在进行的测量操作
        realStop = false;
        deviceBiz.cancelOperate();
    }

    /**
     * 测量完成事件
     */
    public void complete() {
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 平差完成!";
        surveyBackInfos.add(surveyInfo);
        qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
        //引发测量完成事件--用于显示测量完成状态
        onActionCompleted(new OperateCompleteResult(null, null, null));
        prepareStopSurveying("待测",false, false);
    }

    /**
     * 获取测量状态信息
     */
    public String getSurveyStatusInfo(){
        if (preSurveying) {
            return getPreSurveyStatusInfo();
        }
        //返回的状态信息
        String statusInfo = "";
        //还没有测量配置信息
        if (controlBoxAo.getSurveyConfigInfo() == null) {
            statusInfo = "--,--,--,0,0,0,0,下次测量时间： --,0";
            return statusInfo;
        }

        //待测总点数
        int totalNum = surveyCfgPoints.size() - 1;
        switch (controlBoxAo.getSurveyStatus()) {
            //处于在测状态
            case "在测":
                statusInfo += surveyAtOnce ? "临时加测," : "计划测量,";
                statusInfo += recycleNum + ",";
                int currentCount = (int) surveyResults.stream()
                        .filter(it -> it.getChIndex() == currentChIndex && it.isFace1() == face1).count() - 1;
                currentCount = currentCount == -1 ? 0 : currentCount;
                String ptName = surveyCfgPoints.get(currentSurveyPointIndex).getName();
                statusInfo += isFace1() ? "正镜--" + ptName + "," : "倒镜--" + ptName + ",";
                statusInfo += currentCount + "," + totalNum + ",";
                statusInfo += currentChIndex + 1 + "," + limitParams.getChNum() + ",";
                statusInfo += surveyDateTimeInfo == null ? "下次测量时间：--," + currentRepeatIndexFail
                        : surveyDateTimeInfo + "," + currentRepeatIndexFail;
                break;
            case "待测":
                //没有测量策略计划
                if (Objects.equals(currentSurveyCycleCfg, "")) {
                    statusInfo += "无测量周期策略,";
                    statusInfo += "--,";
                    statusInfo += "--,";
                    statusInfo += "0," + totalNum + ",";
                    statusInfo += "0," + limitParams.getChNum() + ",";
                    statusInfo += "下次测量时间： --,0";
                } else {
                    statusInfo += "计划测量,";
                    statusInfo += "--,";
                    statusInfo += "--,";
                    statusInfo += "0," + totalNum + ",";
                    statusInfo += "0," + limitParams.getChNum() + ",";
                    statusInfo += surveyDateTimeInfo == null ? "下次测量时间：--," + currentRepeatIndexFail
                            : surveyDateTimeInfo + "," + currentRepeatIndexFail;
                }

                break;
            case "停测":
                statusInfo += "--,";
                statusInfo += "--,";
                statusInfo += "--,";
                statusInfo += "0," + totalNum + ",";
                statusInfo += "0," + limitParams.getChNum() + ",";
                statusInfo += "下次测量时间： --,0";
                break;
            default:
        }

        return statusInfo;
    }

    /**
     * 获取预测量状态信息
     */
    private String getPreSurveyStatusInfo(){
        //返回的状态信息
        String statusInfo = "";
        //还没有测量配置信息
        if (controlBoxAo.getSurveyConfigInfo() == null)
        {
            statusInfo = "--,--,--,0,0,0,0,下次测量时间： --,0";
            return statusInfo;
        }

        //待测总点数
        int totalNum = preSurveyCfgPoints.size() - 1;
        switch (controlBoxAo.getSurveyStatus())
        {
            //处于在测状态
            case "在测":
                statusInfo += surveyAtOnce ? "临时加测--预测量," : "计划测量--预测量,";
                statusInfo += recycleNum + ",";
                int currentCount = (preSurveyResults.size() % 2 == 0) ? preSurveyResults.size() / 2 : (preSurveyResults.size()) / 2 + 1;
                String ptName = preSurveyCfgPoints.get(currentCount).getName();
                statusInfo += isFace1() ? "正镜--" + ptName + "," : "倒镜--" + ptName + ",";
                statusInfo += currentCount + "," + totalNum + ",";
                statusInfo += "1,1,";
                statusInfo += surveyDateTimeInfo == null ? "下次测量时间：--," + currentRepeatIndexFail
                        : surveyDateTimeInfo + "," + currentRepeatIndexFail;
                break;
            case "待测":
                //没有测量策略计划
                if (Objects.equals(currentSurveyCycleCfg, ""))
                {
                    statusInfo += "无测量周期策略,";
                    statusInfo += "--,";
                    statusInfo += "--,";
                    statusInfo += "0," + totalNum + ",";
                    statusInfo += "0,1,";
                    statusInfo += "下次测量时间： --,0";
                }
                else
                {
                    statusInfo += "计划测量,";
                    statusInfo += "--,";
                    statusInfo += "--,";
                    statusInfo += "0," + totalNum + ",";
                    statusInfo += "0,1,";
                    statusInfo += surveyDateTimeInfo + "," + currentRepeatIndexFail;
                }

                break;
            case "停测":
                statusInfo += "--,";
                statusInfo += "--,";
                statusInfo += "--,";
                statusInfo += "0," + totalNum + ",";
                statusInfo += "0,1,";
                statusInfo += "下次测量时间： --,0";
                break;
        }

        return statusInfo;
    }
    //region 私有方法
    /**
     * 从数据库中获得对应的初始测量X0,Y0,Z0
     * 最近一次工况为“破坏重埋”或“基准点修正"的测量值，若无，则为首次测量值；
     * 若无首次，测点First=true
     * @param surveyCfgPts 测量配置测点列表
     */
    private List<SurveyPoint> getPts0FromDb(List<SurveyCfgPoint> surveyCfgPts){
        List<Long> pIds = surveyCfgPts.stream().map(SurveyCfgPoint::getId).collect(Collectors.toList());
        //删除测站点pid
        pIds.remove(0);
        List<SurveyPoint> surveyPoints = dataXyzhBiz.findFirstByPIds(pIds, hasGroupSurvey, recycleNum);

        //赋值surveyPoints中各点的Name(尚未测量的点Name值为null,需要赋值),AsFixed属性(是否固定，后续计算用到，非常重要)
        for (SurveyPoint surveyPt : surveyPoints)
        {
            Optional<SurveyCfgPoint> optional = surveyCfgPts.stream().filter(it -> it.getId() == surveyPt.getId()).findFirst();
            if (optional.isPresent()){
                surveyPt.setName(optional.get().getName());
                surveyPt.setAsFixed(optional.get().isAsFixed());
            }
        }

        return surveyPoints;
    }

    /**
     * 判定是否启动测量(通过backMsg中的Ok值判断是否启动)
     *
     * @param dtNow  当前时间
     * @param surveyCycleCfg 测量周期策略中的时间点配置字符串
     * @return: param 定时任务参数对象
     */
    private JobParam checkStart(Date dtNow, String surveyCycleCfg){
        String dateNowStr = DateUtil.dateToDateString(dtNow, DateUtil.yyyy_MM_dd_EN);
        Date dtNowFormat = DateUtil.getDate(dateNowStr, DateUtil.yyyy_MM_dd_EN);
        assert dtNowFormat != null;
        if (dtNowFormat.getTime() > dateList.get(1).getTime()){
            surveyBackInfos.add("已到计划截止时间，停止计划测量!");
            log.info("已到计划截止时间，停止计划测量!");
            this.surveyDateTimeInfo = "下次测量时间： 周期策略已结束!";
            return null;
        }
        //判断当前日期是否在策略计划中
        int dayNum = checkJobIntervalDay(cycleInterval, dateList.get(0), dtNow);
        //获取下一次可执行时间
        Date firstTime = getFirstJobDate(dayNum);
        if(firstTime == null || firstTime.getTime() >= dateList.get(1).getTime()){
            this.surveyDateTimeInfo = "下次测量时间： 周期策略中无有效可执行时间!";
            return null;
        }
        //设置下次的执行时间
        this.surveyDateTimeInfo = "下次测量时间： " +  DateUtil.dateToDateString(firstTime, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        //构建定时任务执行参数,并启动定时任务
        JobParam jobParam = new JobParam();
        jobParam.setControlBoxId(controlBoxAo.getId());
        jobParam.setMissionId(controlBoxAo.getMissionId());
        jobParam.setSerialNo(controlBoxAo.getSerialNo());
        jobParam.setCycleInfo(surveyCycleCfg);
        jobParam.setStartDate(dateList.get(0));
        jobParam.setEndDate(dateList.get(1));
        jobParam.setFirstTime(firstTime);
        jobParam.setFirstTimeCorn(MonitorJobUtil.getCronString(firstTime));
        jobParam.setRecycleNum(recycleNum);
        return jobParam;
    }

    /**
     * 验证当前时间是否在周期时间内
     * @param cfgDayInterval 间隔天数
     * @param cfgDateStart 开始时间
     * @param dtNowFormat 当前时间
     * @return 下次定时任务时间距当前或开始时间间隔天数
     */
    private int checkJobIntervalDay(int cfgDayInterval, Date cfgDateStart, Date dtNowFormat) {
        //任务开始时间在当前时间之后，到开始时间立刻执行
        if(cfgDateStart.getTime() >= dtNowFormat.getTime()){
            return 0;
        }
        //当前时间已经在开始时间之后，判断和开始时间间隔天数和周期天数差
        int days = (int) ((dtNowFormat.getTime() - cfgDateStart.getTime()) / (24 * 3600 * 1000));
        int mod = days % cfgDayInterval;
        return mod == 0 ? 0 : cfgDayInterval - mod;
    }

    /**
     * 获取定时任务第一次执行时间
     *
     * @param dayNum         当前时间和第一次执行时间间隔天数
     * @return 第一次执行时间
     */
    private Date getFirstJobDate(int dayNum) {
        //记录当前时间
        Date currentDate = new Date();
        //当前时间在开始时间之前，则取配置的开始日期的第一个时间点执行
        if (dateList.get(0).getTime() >= currentDate.getTime()){
            String dtStartStr = DateUtil.dateToDateString(dateList.get(0), DateUtil.yyyy_MM_dd_EN);
            return DateUtil.getDate(dtStartStr + " "
                    + surveyTimes.get(0).format(DateTimeFormatter.ofPattern("HH:mm:ss")), DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        }

        LocalTime currentTime = LocalTime.parse(DateUtil.dateToDateString(currentDate, DateUtil.HH_mm_ss_EN)
                , DateTimeFormatter.ofPattern("HH:mm:ss"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Date nextDate = null;
        //dayNum == 0，取当天有效时间点，若是无取下一个周期第一个时间点
        if (dayNum == 0){
            //循环比较配置策略时间点，选择一个可执行时间值
            for (LocalTime selectTime : surveyTimes) {
                if (selectTime.isAfter(currentTime)) {
                    //获取下一个时间周期的第一个时间
                    String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " "
                            + selectTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
                    break;
                }
            }
            //当前没有获取到下一次时间值 表示当天时间点都已经无效
            if(nextDate == null) {
                //获取下一个时间周期的第一个时间
                nextDate = getNextPeriodFirstDate(calendar, cycleInterval, surveyTimes.get(0).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        }else {
            //获取下一个时间周期的第一个时间
            nextDate = getNextPeriodFirstDate(calendar, dayNum, surveyTimes.get(0).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
        return nextDate;
    }

    /**
     * 更新定时任务
     * @return 下次执行时间
     */
    public String updateJobCron(Long controlBoxId, String serialNo){
        try {
            Date currentDate = new Date();
            LocalTime currentTime = LocalTime.parse(DateUtil.dateToDateString(currentDate, DateUtil.HH_mm_ss_EN)
                    , DateTimeFormatter.ofPattern(DateUtil.HH_mm_ss_EN));
            //循环比较第一个时间点后时间点，选择下一个可执行时间值
            Date nextDate = null;
            for (LocalTime selectTime : surveyTimes) {
                if (selectTime.isAfter(currentTime)) {
                    //获取下一个时间周期的第一个时间
                    String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " "
                            + selectTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
                    break;
                }
            }
            //当前没有获取到下一次时间值 表示当天时间点都已经执行过
            if (nextDate == null) {
                //获取下一个时间周期的第一个时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                nextDate = getNextPeriodFirstDate(calendar, cycleInterval, surveyTimes.get(0).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
            //下一次执行时间超过结束时间，删除定时任务，否则更新定时任务时间
            if (nextDate.getTime() > dateList.get(1).getTime()) {
                surveyJobService.removeJob(MonitorJobConst.JOB_PREFIX + serialNo,
                        MonitorJobConst.TRIGGER_PREFIX + serialNo);
                // 2023/6/26 记录状态
                controlBoxService.updateSurvey(controlBoxId, 0);
                System.out.println(serialNo + " 周期任务已完成");
                return "周期任务已完成";
            }
            //设置下一次的执行时间
            boolean modified = surveyJobService.modifyJob(MonitorJobConst.TRIGGER_PREFIX + serialNo,
                    MonitorJobUtil.getCronString(nextDate), nextDate);
            log.info("控制器 {} 定时任务修改结果：{}", serialNo, modified);
            return DateUtil.dateToDateString(nextDate, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        } catch (Exception e) {
            log.error("定时任务修改异常：{}", e.getMessage());
            return "下次测量时间: 定时任务修改失败";
        }
    }

    /**
     * 获取下一个周期中的第一个监测时间
     *
     * @param calendar 当前时间
     * @param addDayNum    当前时间距下一个时间相差天数
     * @param firstTimeStr 第一次执行时间点字符
     * @return 下一次任务执行时间
     */
    private Date getNextPeriodFirstDate(Calendar calendar, int addDayNum, String firstTimeStr) {
        calendar.add(Calendar.DATE, addDayNum);
        String nextTimeStr = DateUtil.dateToDateString(calendar.getTime(),DateUtil.yyyy_MM_dd_EN) + " " + firstTimeStr;
        return DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 验证是否启动测量任务
     * @param startTime startTime
     */
    private void monitor(Date startTime) {
        //判定是否启动一次测量，并获取下次定时执行时间
        JobParam jobParam = checkStart(startTime, currentSurveyCycleCfg);
        if (jobParam == null){
            return;
        }
        //清除任务防止异常任务重复
        surveyJobService.removeJob(MonitorJobConst.JOB_PREFIX + controlBoxAo.getSerialNo()
                ,MonitorJobConst.TRIGGER_PREFIX + controlBoxAo.getSerialNo());
        //启动定时任务
        surveyJobService.addJob(MonitorJobConst.JOB_PREFIX + controlBoxAo.getSerialNo()
                ,MonitorJobConst.TRIGGER_PREFIX + controlBoxAo.getSerialNo()
                , RobotSurveyJob.class, jobParam);
    }

    /**
     * 开始测量
     * @param groupCycle 是否编组测量的循环
     */
    private void startSurvey(boolean groupCycle) {
        if (groupCycle){
            currentGroupIndex++;
            openDeviceRetryTime = 1;
        }
        //编组测量前睡眠
        if (hasGroupSurvey && groupSleepTimes.get(currentGroupIndex) > 0) {
            log.info("编组{}测量睡眠{}s", currentGroupIndex+1, groupSleepTimes.get(currentGroupIndex));
            //编组测量先判断是否需要睡眠等待
            try {
                Thread.sleep(groupSleepTimes.get(currentGroupIndex) * 1000L);
            } catch (InterruptedException e) {
                log.info("编组{}测量睡眠异常: {}", currentGroupIndex, e.getMessage());
            }
        }
        if (hasGroupSurvey){
            //确认有成功获取到有效的编组配置，更新编组序号为0的测点为测量点和预测量点
            this.surveyCfgPoints = this.surveyCfgPointGroup.get(currentGroupIndex);
            this.preSurveyCfgPoints = this.preSurveyCfgPointGroup.get(currentGroupIndex);
            this.needPreSurvey = !this.preSurveyCfgPoints.isEmpty();
        }
        //获得测点初值
        surveyPoints0 = getPts0FromDb(surveyCfgPoints);

        finalOk = false;
        surveyFinished = false;
        preSurveyOk = true;
        startSurveying = true;
        if (controlBoxBo != null){
            controlBoxBo.setProcessCommandStatus(0);
        }

        if (!groupCycle){
            //编组循环不清除测量信息和测点睡眠记录
            surveyBackInfos.clear();
            surveyPointIds.clear();
            //获得测量周期数
            recycleNum = surveyDataBiz.getLastRecycleNumByMissionId(controlBoxAo.getMissionId()) + 1;
        }
        //消息列表
        String msg = "";
        if (surveyAtOnce) {
            String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] >>> 临时加测 <<<";
            msg += surveyInfo;
            surveyBackInfos.add(surveyInfo);
        }
        if (hasGroupSurvey) {
            if (!msg.isEmpty()) {
                msg += "\r\n";
            }
            surveyBackInfos.add("[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]测量编组数:"+surveyCfgPointGroup.size()+"--编组" + (currentGroupIndex + 1) + "开始测量>>");
            msg += "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]测量编组数:"+surveyCfgPointGroup.size()+"--编组" + (currentGroupIndex + 1) + "开始测量>>";
        }
        if (!msg.isEmpty()) {
            qwMsgService.sendSurveyProcessMsg(msg, controlBoxAo.getMissionId());
        }
        onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, "测量开始", null));
        //预测量判断
        if (needPreSurvey) {
            this.preSurveying(false);
        } else {
            //开始测量
            this.surveying(false);
        }
    }

    /**
     * 开始整个测组测量
     * @param isRepeat 是否是重测整个测点组：默认false
     */
    private void surveying(boolean isRepeat) {
        //初始化参数
        currentChIndex = 0;
        currentRepeatIndexCover = 0;
        currentRepeatIndexRunOver = 0;
        currentSurveyPointIndex = 1;

        //清空之前测量结果
        surveyResults.clear();
        surveyCalcResults.clear();
        surveyCalcResults2Db.clear();
        //开始测量，失败重测测组次数归零
        if (!isRepeat) {
            currentRepeatIndexFail = 0;
        }

        //以开机作为开始测量的起始，后面根据异步执行结果，开始测量过程
        if (deviceBiz == null){
            try {
                Thread.sleep(5 * 1000);
                log.info("surveying waiting");
            } catch (InterruptedException e) {
                log.info("surveying wait InterruptedException");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("surveying openDevice sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("surveying InterruptedException");
                }
            }
            if (deviceBiz == null) {
                recordInfo("测量开机失败: 控制器离线");
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        deviceBiz.openDevice(false);
        log.info("测量开机,测量准备开始...");
        qwMsgService.sendSurveyProcessMsg("测量开机,测量准备开始...", controlBoxAo.getMissionId());
        //记录测量信息
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 测量开始...";
        //是重测
        if (isRepeat) {
            surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 重测开始["
                    + currentRepeatIndexFail + "/" + limitParams.getRepeatNumFail() + "]...";
        }

        surveyBackInfos.add(surveyInfo);
    }

    /**
     * 开始整个测组测量
     * @param isRepeat 是否是重测整个测点组：默认false
     */
    private void preSurveying(boolean isRepeat) {
        preSurveying = true;

        //初始化参数
        currentRepeatIndexCover = 0;
        currentRepeatIndexRunOver = 0;
        currentSurveyPointIndex = 1;

        //清空之前测量结果
        preSurveyResults.clear();

        //开始测量，失败重测测组次数归零
        if (!isRepeat) {
            currentRepeatIndexFail = 0;
        }

        //以开机作为开始测量的起始，后面根据异步执行结果，开始测量过程
        if (deviceBiz == null){
            try {
                log.info("preSurveying waiting");
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                log.info("preSurveying wait InterruptedException");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("preSurveying openDevice sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("preSurveying InterruptedException");
                }
            }
            if (deviceBiz == null) {
                recordInfo("预测量开机失败: 控制器离线");
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        deviceBiz.openDevice(false);

        //记录测量信息
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 预测量关键点开始...";
        //是重测
        if (isRepeat) {
            surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 重测关键点开始["
                    + currentPreSurveyIndex + "/" + preSurveyNum + "]...";
        }

        qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
        surveyBackInfos.add(surveyInfo);
    }

    /**
     * 开始整个测组测量
     * @param pauseTime 暂停时间后开始测量:默认为0
     */
    private void surveyOnce(int pauseTime) {
        //预处理，获取驱动仪器测量的初始信息
        //指定测回测量结果列表
        List<SurveyResult> chResults = surveyResults.stream().filter(it -> it.getChIndex() == currentChIndex).collect(Collectors.toList());
        //待测点序号
        int pos = currentSurveyPointIndex;
        //待测点
        SurveyCfgPoint cfgPt = surveyCfgPoints.get(pos);
        Optional<SurveyPoint> optional = surveyPoints0.stream().filter(it -> it.getId() == cfgPt.getId()).findAny();
        SurveyPoint pt0 = optional.orElse(new SurveyPoint());
        SurveyResult result = new SurveyResult();
        //首次测量时pt0的Name没有赋值，用cfgPt
        result.setId(pt0.getId());
        result.setPtName(pt0.getName());
        result.setAsFixed(pt0.isAsFixed());
        result.setChIndex(currentChIndex);
        result.setFirst(pt0.isFirst());

        //赋值初始坐标
        result.setX0(pt0.isFirst() ? 0.0 : pt0.getX());
        result.setY0(pt0.isFirst() ? 0.0 : pt0.getY());
        result.setZ0(pt0.isFirst() ? 0.0 : pt0.getZ());
        //赋值测量时间
        result.setGetTime(new Date());

        if (chResults.isEmpty()) {
            //正镜观测上半测回第1点
            result.setFace1(true); //正镜
            result.setChIndex(currentChIndex);
        } else if (chResults.size() == surveyCfgPoints.size()) {
            //倒镜观测下半测回第1点
            result.setPtName(pt0.getName());
            result.setFace1(false);
            result.setChIndex(currentChIndex);
        } else {
            SurveyResult prevResult = chResults.get(chResults.size() - 1);
            result.setFace1(prevResult.isFace1());
        }

        //预置测量结果
        //获取预测量结果
        Optional<SurveyResult> preResult = preSurveyResults.stream()
                .filter(res -> res.getPtName().equals(result.getPtName())
                        && res.isFace1() == result.isFace1()).findAny();
        if (preResult.isPresent()) {
            //复制预测结果
            SurveyResult copy = DzBeanUtils.propertiesCopy(preResult.get(), SurveyResult.class);
            copy.setChIndex(result.getChIndex());
            copy.setGetTime(new Date());
            surveyResults.add(copy);
            deviceBiz.usePreSurveyResult(chResults.size() + 1, copy.getChIndex(), false);
            return;
        }
        surveyResults.add(result);

        //2获得isFace1,ha,va
        boolean isFace1 = result.isFace1();
        this.face1 = isFace1;
//        double hi = cfgPt.getHi();
        double ht = cfgPt.getHt();
        double ha = isFace1 ? cfgPt.getHa() : cfgPt.getHa() + Math.PI;
        ha = ha < Math.PI * 2 ? ha : ha - Math.PI * 2;
        double va = isFace1 ? cfgPt.getVa() : Math.PI * 2 - cfgPt.getVa();

        //测量
        double searchHa = limitParams.getRangHa();
        double searchVa = limitParams.getRangVa();
        //修改：测点正反测回都暂停
        if(cfgPt.getSleepTime() > 0){
            pauseTime += cfgPt.getSleepTime();
        }
        if (deviceBiz == null){
            try {
                log.info("checkMeasureAndGetFullResult waiting...");
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                log.info("checkMeasureAndGetFullResult wait InterruptedException");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("surveyOnce checkMeasureAndGetFullResult sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("checkMeasureAndGetFullResult InterruptedException");
                }
            }
            if (deviceBiz == null) {
                recordInfo("测点测量: 控制器离线");
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        deviceBiz.checkMeasureAndGetFullResult(searchHa, searchVa, ha, va, ht, pauseTime, false);
    }

    /**
     * 开始单次测量--预测量关键点
     * @param pauseTime 暂停时间后开始测量
     */
    private void preSurveyOnce(int pauseTime){
        //预处理，获取驱动仪器测量的初始信息
        int pos = currentSurveyPointIndex; //待测点序号
        SurveyCfgPoint cfgPt = preSurveyCfgPoints.get(pos); //待测点
        Optional<SurveyPoint> optional = surveyPoints0.stream()
                .filter(item -> item.getId() == cfgPt.getId()).findAny();
        SurveyResult result = new SurveyResult();
        optional.ifPresent(surveyPoint -> {
            //首次测量时pt0的Name没有赋值，用cfgPt
            result.setId(surveyPoint.getId());
            result.setPtName(surveyPoint.getName());
            result.setAsFixed(surveyPoint.isAsFixed());
            result.setChIndex(currentChIndex);
            result.setFirst(surveyPoint.isFirst());
            //赋值初始坐标
            result.setX0(surveyPoint.isFirst() ? 0.0 : surveyPoint.getX());
            result.setY0(surveyPoint.isFirst() ? 0.0 : surveyPoint.getY());
            result.setZ0(surveyPoint.isFirst() ? 0.0 : surveyPoint.getZ());
            result.setGetTime(new Date());
        });

        //预置测量结果
        preSurveyResults.add(result);
        result.setFace1(preSurveyResults.size() % 2 != 0);
        //2获得isFace1,ha,va
        boolean isFace1 = result.isFace1();
        setFace1(isFace1);
        double ht = cfgPt.getHt();
        double ha = isFace1 ? cfgPt.getHa() : cfgPt.getHa() + Math.PI;
        ha = ha < Math.PI * 2 ? ha : ha - Math.PI * 2;
        double va = isFace1 ? cfgPt.getVa() : Math.PI * 2 - cfgPt.getVa();
        //测量
        double searchHa = limitParams.getRangHa();
        double searchVa = limitParams.getRangVa();
        if(cfgPt.getSleepTime() > 0){
            pauseTime += cfgPt.getSleepTime();
        }
        if (deviceBiz == null){
            try {
                Thread.sleep(5 * 1000);
                log.info("preSurveyOnce checkMeasureAndGetFullResult waiting ...");
            } catch (InterruptedException e) {
                log.info("preSurveyOnce checkMeasureAndGetFullResult wait exception");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("preSurveyOnce checkMeasureAndGetFullResult sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("checkMeasureAndGetFullResult exception");
                }
            }
            if (deviceBiz == null) {
                recordInfo("测点预测量失败: 控制器离线");
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        deviceBiz.checkMeasureAndGetFullResult(searchHa, searchVa, ha, va, ht, pauseTime, false);
    }

    /**
     * 停止测量前的准备工作:收起仪器镜头
     */
    private void prepareStop(boolean directInvoke) {
        //收起望远镜
        double ha = surveyCfgPoints.get(1).getHa();
        double va = Math.PI;
        if (deviceBiz == null){
            try {
                Thread.sleep(5 * 1000);
                log.info("prepareStop waiting");
            } catch (InterruptedException e) {
                log.info("prepareStop wait exception");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("prepareStop checkMeasureAndGetFullResult sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("prepareStop exception");
                }
            }
            if (deviceBiz == null) {
                String surveyInfo = "停机失败: 控制器离线";
                recordInfo("停机失败: 控制器离线");
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        deviceBiz.prepareStop(ha, va, limitParams.isTurnOffWhenOk(), directInvoke);
    }

    /**
     * 获得仪器基本信息：仪器名,仪器号,电源类型及电量情况
     **/
    private void getDevicePowerInfo(boolean directInvoke) {
        if (deviceBiz == null){
            try {
                Thread.sleep(5 * 1000);
                log.info("getDevicePowerInfo waiting");
            } catch (InterruptedException e) {
                log.info("getDevicePowerInfo wait exception");
            }
            while (sleepCountDownLatch != null && sleepCountDownLatch.getCount() == 1){
                log.info("getDevicePowerInfo checkMeasureAndGetFullResult sleep...");
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("getDevicePowerInfo exception");
                }
            }
            if (deviceBiz == null) {
                recordInfo("获取设备信息失败: 控制器离线");
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null));
                return;
            }
        }
        this.deviceBiz.getDevicePowerInfo(directInvoke);
    }
    //endregion

    //region 事件处理函数
    /**
     * 处理上线事件
     */
    public void currentBoxOnOffLine()
    {
        boolean isOnline = Objects.equals(controlBoxAo.getStatus(), "在线");
        this.onLineChanged(isOnline);
    }

    /**
     * 在线状态切换后的处理
     * @param isOnLine 在线状态
     */
    private void onLineChanged(boolean isOnLine) {
        //上线、下线会导致CurrentBoxBo重新生成，需要重新赋值挂接的仪器信息
        refreshCurrentBoxBo();
        //当处于在测状态时，处理控制器掉线问题
        if (Objects.equals(controlBoxAo.getSurveyStatus(), "在测")) {
            ctrlBoxOffLineProcess(isOnLine);
        }
    }

    //设置控制离线休眠时间(休眠时间内上线可继续测量，单位秒)
    private final int enableOfflineTime = 60;
    private CountDownLatch sleepCountDownLatch;
    /**
     * 处于测量状态时，处理控制器掉线情况
     * @param isOnLine 在线状态
     */
    private void ctrlBoxOffLineProcess(boolean isOnLine) {
        //上线--计算掉线时长，决定是否继续测量
        if (isOnLine) {
            if (!DateUtil.dateToDateString(offlineTime, DateUtil.yyyy_MM_dd_EN).startsWith("2022")) {
                long offTime = System.currentTimeMillis() - offlineTime.getTime() ;
                //掉线3分钟内，继续测量
                if (offTime < (enableOfflineTime * 1000 - 3000)) {
                    if (preSurveying){
                        int pos = preSurveyResults.size() - 1;
                        preSurveyResults.remove(pos);
                    } else {
                        int pos = surveyResults.size() - 1;
                        surveyResults.remove(pos);
                    }
                    currentRepeatIndexCover = 0;
                    currentRepeatIndexRunOver = 0;
                    offlineTime = DateUtil.getDate("2022-01-01 00:00:00", DateUtil.yyyy_MM_dd_HH_mm_ss_EN);

                    if (sleepCountDownLatch != null) {
                        sleepCountDownLatch.countDown();
                    }

                    if (preSurveying) {
                        preSurveyOnce(0);
                    } else {
                        surveyOnce(0);
                    }
                }
                //超过3分钟后再上线，停止本次测量
//                else {
//                    String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), "yyyy-MM-dd HH:mm:ss") + "] 控制器离线已 "+ String.format("%.2f", offTime / 1000 / 60.0) + " 分钟(> "+enableOfflineTime+"),本次测量终止!";
//                    surveyBackInfos.add(surveyInfo);
//                    prepareStopSurveying("待测", true, false);
//                    if (multiSurveyOperateCompleted != null) {
//                        multiSurveyOperateCompleted.accept(new MultiOperateCompleteResult(true, CommonUtil.SYS_GRC_ERR, "OFFLINE_RECONNECT_TIMEOUT"));
//                    }
//                }
            }
        }
        //掉线--记录掉线时间，中断测量
        else {
            offlineTime = new Date();
            deviceBiz.offLineOperate();
            ThreadPoolUtil.getPool().execute(() -> {
                try {
                    sleepCountDownLatch = new CountDownLatch(1);
                    sleepCountDownLatch.await(enableOfflineTime, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.info("ctrlBoxOffLineProcess sleepCountDownLatch interrupted");
                }
                if (DateUtil.dateToDateString(offlineTime, DateUtil.yyyy_MM_dd_EN).startsWith("2022")) {
                    log.info("离线睡眠主动解除，继续测量");
                    return;
                }
                if (!hasGroupSurvey){
                    if (!surveyAtOnce){
                        qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                    }
                } else {
                    if (hasSurveySuccess){
                        List<PointDataXyzh> list = getCurrentSurveyResult();
                        PointDataXyzh dataXyzh = list.get(0);
                        createPushTaskJob(dataXyzh.getGetTime());
                        pushToCorrectDb(dataXyzh);
                        qwMsgService.sendSurveyResultMsg(list, controlBoxAo.getMissionId(), -1);
                    } else {
                        // 2024/11/26 漏测处理,判断是否加测
                        if (!surveyAtOnce){
                            qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                        }
                    }
                }
                String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), "yyyy-MM-dd HH:mm:ss") + "] 控制器离线已超过"+enableOfflineTime+"s,本次测量终止!";
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                prepareStopSurveying("待测", false, true);
                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, "离线", null, false));
                if (multiSurveyOperateCompleted != null) {
                    multiSurveyOperateCompleted.accept(new MultiOperateCompleteResult(true, CommonUtil.SYS_GRC_ERR, "OFFLINE_RECONNECT_TIMEOUT"));
                }
            });
        }
    }

    /**
     * @description 测量结果处理
     * @author jing.fang
     * @date 2023/3/16 14:00
     * @param result 返回结果对象
     * @return: void
     **/
    private void handleSurveyOperateResult(OperateCompleteResult result) {
        RtCode rtCode = result.getRtCode();
        String commandName = result.getCommandName();
        List<String> results = result.getResults();
        // 2025/1/16 记录测量时间，防止离线后重新上线控制器异常导致一直在测
        surveyTime = new Date();

        //指定调用无后续关联操作时直接返回
        if (result.isDirectCall()) {
            //温度气压采集器测量回调
            if ("Survey".equals(commandName)) {
                //激发任务完成事件
                this.onMeteActionCompleted(result);
                return;
            }
            //激发任务完成事件
            this.onActionCompleted(result);
            return;
        }
        //温度气压采集器测量回调
        if ("Survey".equals(commandName)) {
            //测量完成采集
            deviceMeteOnOperateCompleted(rtCode, results);
            return;
        }
        //测量总过程结果处理
        if ("SYS_GRC_OK".equals(rtCode.getName())) {
            switch (commandName) {
                //先开机，获得仪器信息，然后测量
                case "OpenDevice":
//                    this.getDevicePowerInfo(false);
//                    break;
                    if (preSurveying) {
                        this.preSurveyOnce(0);
                    } else {
                        this.surveyOnce(0);
                    }
                    break;
                case "GetDevicePowerInfo":
                    if (preSurveying) {
                        this.preSurveyOnce(0);
                    } else {
                        this.surveyOnce(0);
                    }
                    //仪器从关机状态到开机可能需要十几秒的时间，期间不允许“停止测量”
                    this.controlBoxAo.setSurveyStatus("在测");
                    break;
                //单次测量任务成功
                case "CheckMeasureAndGetFullResult":
                    //重新设定当前遮挡失败重测次数为0
                    this.currentRepeatIndexCover = 0;
                    if (preSurveying) {
                        this.processPreSurveyResultOnce(results);
                    } else {
                        this.processSurveyResultOnce(results);
                    }
                    break;
                //使用预测关键点数值
                case "UsePreSurveyResult":
                    int resultCount = Integer.parseInt(results.get(0));
                    int chIndex = Integer.parseInt(results.get(1));
                    this.prepareNextSurveyNew(resultCount, chIndex);
                    break;
                //准备关机停测
                case "PrepareStop":
                    //激发任务完成事件
                    this.onActionCompleted(result);
                    // 2023/5/30 调用位置改变
                    handleActionCompleted(result);
                    break;
                default:
            }
        } else {
            processExceptionOnSurvey(rtCode, commandName);
        }
        //激发测量总过程子任务事件: 任务完成需要单独处理
        if (!"PrepareStop".equals(result.getCommandName())) {
            /* 2023/5/30 关机异常成功失败时都要处理数据
             * PrepareStop异常处理中已经调用了handleActionCompleted，
             * 将此处的调用放到PrepareStop成功时调用
             */
            handleSubSurveyOperateResult(result);
        }
    }

    /**
     * 温度气压采集完成
     * @param rtCode  rtCode
     * @param results results
     */
    private void deviceMeteOnOperateCompleted(RtCode rtCode, List<String> results) {
        String surveyInfo = "";
        String serialNo = deviceMeteBiz.getControlBoxMete() == null ? "未知设备" : deviceMeteBiz.getControlBoxMete().getSerialNo();
        if (CommonUtil.SYS_GRC_OK == rtCode)
        {
            surveyInfo = serialNo + " 气象参数测量成功！" + "--";
            surveyInfo += "温度(℃)：" + results.get(0) + " 气压(KPa)：" + results.get(2) + " 湿度(%)：" +
                    results.get(1);
            surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] " + surveyInfo;
            surveyBackInfos.add(surveyInfo);

            int pos = meteInfo.indexOf(',');
            meteInfo = meteInfo.substring(0,pos)+"," + results.get(0) + "," + results.get(1) + "," + results.get(2);
        } else {
            String[] meteInfos = meteInfo.split(",");
            surveyInfo = serialNo + " 气象参数测量失败！" + "启用初始设置数据--";
            surveyInfo += "温度(℃)：" + meteInfos[1] + " 气压(KPa)：" + meteInfos[3] + " 湿度(%)：" +
                    meteInfos[2];
            surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] " + surveyInfo;
            surveyBackInfos.add(surveyInfo);
        }
        //后续处理
        boolean isHalfCh = limitParams.getChNum() == 0;
        processSurveyResultAllWithMeteCorrect(isHalfCh);
    }

    /**
     * 总测量完成事件
     * @param result result
     */
    private void handleActionCompleted(OperateCompleteResult result) {
        Date getTime = new Date();
        // 2024/3/7 多站联测单站结束处理逻辑,待完善
        if (isMultiStation() ) {
            if (multiSurveyOperateCompleted != null) {
                MultiOperateCompleteResult completeResult = new MultiOperateCompleteResult(
                        true,
                        result.getRtCode(),
                        result.getCommandName()
                );
                multiSurveyOperateCompleted.accept(completeResult);
            }
        } else {
            //2023/5/27
            //保存结果,只要验证成功即可，不需要等关机一定成功
            if(finalOk){
                List<Long> surveyCfgPointIds = new ArrayList<>();
                this.surveyCfgPointGroup.forEach(surveyCfgPoint -> surveyCfgPoint.forEach(item -> surveyCfgPointIds.add(item.getId())));
                PointDataXyzhDto dataDto = dataXyzhBiz.saveRobotResultOnSuccess(finalResults, surveyData, false, isSurveyAtOnce(), hasGroupSurvey, surveyCfgPointIds, surveyCfgPointGroup.size() <= 1 ? -1 : currentGroupIndex);
                List<PointDataXyzh> dataXyzhs = dataDto.getDataList();
                PointDataXyzh dataXyzh = !dataXyzhs.isEmpty() ? dataXyzhs.get(0) : null;
                if (!dataXyzhs.isEmpty()) {
                    getTime = dataXyzh.getGetTime();
                }
                // 2023/6/9 新增推送任务，和铁路局对接
                if (!surveyAtOnce){
                    if (!hasGroupSurvey || currentGroupIndex == surveyCfgPointGroup.size() - 1){
                        createPushTaskJob(getTime);
                    }
                }else{
                    //临时加测时需要页面回显测量结果,
                    try {
                        result.setRtCode(CommonUtil.SYS_GRC_OK);
                        WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_DATA.getCode(),
                                "surveyAtOnce_" + this.controlBoxAo.getSerialNo(), result));
                    } catch (IOException e) {
                        log.warn("surveyAtOnce socket发送失败，原因：{}", e.getMessage());
                    }
                }

                // region 2025/06/20 生成漏测点信息并发送
                String missInfo = createMissSurveyPtInfo(dataXyzhs);
                if (missInfo != null) {
                    qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum, missInfo);
                }
                // endregion 2024/06/20 生成漏测点信息并发送

                // region 2024/11/26 测量成功发送结果到微信
                qwMsgService.sendSurveyResultMsg(dataXyzhs, controlBoxAo.getMissionId(), -1);
                // endregion 2024/11/26 测量成功发送结果到微信

                //超限报警发送声光报警指令
                if(dataDto.isHasAlarm()){
                    ChannelHandlerUtil.sendSoundAlarmCode(this.soundAlarmCfg, 3);
                }

                if (!hasGroupSurvey || currentGroupIndex == surveyCfgPointGroup.size() - 1){
                    // region 2024/11/20 数据同步推送任务
                    pushToCorrectDb(dataXyzh);
                    // endregion 2024/11/20 数据同步推送任务
                }
                finalOk = false;
            }else {
                dataXyzhBiz.saveRobotData(getTime, controlBoxAo.getMissionId(), surveyData);

                // 2024/11/26 异常处理
                if (!hasGroupSurvey){
                    if (!surveyAtOnce){
                        qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                    }
                } else {
                    if (currentGroupIndex == surveyCfgPointGroup.size() - 1){
                        if (hasSurveySuccess){
                            createPushTaskJob(getTime);
                            List<PointDataXyzh> list = getCurrentSurveyResult();
                            pushToCorrectDb(list.get(0));
                            qwMsgService.sendSurveyResultMsg(list, controlBoxAo.getMissionId(), -1);
                        } else {
                            // 2024/11/26 漏测处理,判断是否加测
                            if (!surveyAtOnce){
                                qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                            }
                        }
                    }
                }
            }
        }

        if (!hasGroupSurvey || currentGroupIndex== surveyCfgPointGroup.size()-1){
            surveyAtOnce = false;
            controlBoxAo.setSurveyStatus("待测");
        }
        //输出测量反馈信息
        if (!surveyBackInfos.isEmpty()) {
            String logPath = FileUtil.BASE_PATH + File.separatorChar
                    + "UserLog" + File.separatorChar
                    + controlBoxAo.getProjectName() + "_"
                    + controlBoxAo.getMissionName() + "_"
                    + DateUtil.dateToDateString(getTime, "yyyyMMdd_HHmm") + ".log";
            //保存测量过程信息
            FileUtil.exportUserLog(surveyBackInfos, logPath);
        }
    }

    /**
     * 总测量过程子过程事件
     * @param result result
     */
    private void handleSubSurveyOperateResult(OperateCompleteResult result) {
        // 2024/3/7 多测站时通知multiBiz处理
        if (isMultiStation() && multiSurveyOperateCompleted != null){
            // 2024/3/7 子过程处理逻辑
            MultiOperateCompleteResult completeResult = new MultiOperateCompleteResult();
            completeResult.setActionComplete(false);
            completeResult.setCommandName(result.getCommandName());
            completeResult.setRtCode(result.getRtCode());
            multiSurveyOperateCompleted.accept(completeResult);
        }
        if ("SYS_GRC_OK".equals(result.getRtCode().getName()) && "GetDevicePowerInfo".equals(result.getCommandName())){
            //更新AO中deviceInfo字段
            String[] oldInfos = this.controlBoxAo.getDeviceInfo().split(" ");
            String deviceInfo = oldInfos[0] + " " + oldInfos[1] + " " +
                    " 电量:" + result.getResults().get(0) + "% 电源：";
            deviceInfo += ("1".equals(result.getResults().get(1)) ? "外置" : "内置");
            this.controlBoxAo.setDeviceInfo(deviceInfo);
        }
        //激发子过程完成事件
        this.onSubActionCompleted(result);
    }
    //endregion 事件处理函数

    /**
     * 生成漏测点信息
     * @param dataXyzhs 测量结果
     * @return 漏测点信息
     */
    private String createMissSurveyPtInfo(List<PointDataXyzh> dataXyzhList) {
        StringBuilder missInfo = null;
        List<Long> ptIds = dataXyzhList.stream().map(PointDataXyzh::getPid).collect(Collectors.toList());
        for (int i = 1; i < surveyCfgPoints.size(); i++) {
            SurveyCfgPoint cfgPoint = surveyCfgPoints.get(i);
            if (!ptIds.contains(cfgPoint.getId())) {
                if (missInfo == null) {
                    missInfo = new StringBuilder(cfgPoint.getName());
                } else {
                    missInfo.append(",").append(cfgPoint.getName());
                }
            }
        }
        if (missInfo != null) {
            return missInfo.toString();
        }
        return null;
    }

    /**
     * 查询本次测量数据
     */
    private List<PointDataXyzh> getCurrentSurveyResult(){
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        List<Long> pidList = surveyCfgPointsAll.stream().map(SurveyCfgPoint::getId).collect(Collectors.toList());
        if (pidList.isEmpty()){
            return Collections.emptyList();
        }
        wrapper.eq(PointDataXyzh::getRecycleNum, recycleNum)
                .in(PointDataXyzh::getPid, pidList)
                .select(PointDataXyzh::getName,
                        PointDataXyzh::getPid,
                        PointDataXyzh::getGetTime,
                        PointDataXyzh::getGetTimePrev,
                        PointDataXyzh::getTotalX,
                        PointDataXyzh::getTotalY,
                        PointDataXyzh::getTotalZ,
                        PointDataXyzh::getTotalP,
                        PointDataXyzh::getDeltX,
                        PointDataXyzh::getDeltY,
                        PointDataXyzh::getDeltZ,
                        PointDataXyzh::getDeltP)
                .orderByDesc(PointDataXyzh::getCreateTime);
        return dataXyzhService.list(wrapper);
    }

    /**
     * 更新测量漏传记录表
     */
    private void saveRobotSurveyRecord(int recycleNum, Date uploadAlarmTime){
        try {
            log.info("{} 更新测量漏测漏传记录: {}...", controlBoxAo.getSerialNo(), uploadAlarmTime);
            RobotSurveyRecord record = new RobotSurveyRecord();
            record.setMissionId(controlBoxAo.getMissionId());
            record.setMissionName(controlBoxAo.getMissionName());
            record.setSerialNo(controlBoxAo.getSerialNo());
            record.setRecycleNum(recycleNum);
            record.setSurveyFinish(1);
            record.setSurveyAlarmTime(new Date());
            record.setUploadFinish(0);
            record.setUploadAlarmTime(uploadAlarmTime);
            robotSurveyRecordService.save(record);
            log.info("{} 更新测量漏测漏传记录结束", controlBoxAo.getSerialNo());
        } catch (Exception e) {
            log.info("{} 更新测量漏测漏传记录表异常: {}", controlBoxAo.getSerialNo(), e.getMessage());
        }
    }

    /**
     * 数据推送(其他)处理
     */
    private void pushToCorrectDb(PointDataXyzh dataXyzh) {
        //验证是否创建推送任务
        PushTaskOther taskOther = getPushOtherTask();
        if (taskOther == null || dataXyzh == null) {
            return;
        }
        List<Long> ptIds = getSurveyPtIds();
        if (ptIds.isEmpty()) {
            return;
        }
        //判断推送类型：本期数据 1 还是上期数据 0
        if(taskOther.getPushCurrentData() == 1) {
            //推送本期数据没有延迟时立即推送，设置延时创建定时任务推送
            if (taskOther.getDelayUploadTime() <=0) {
                //立即推送
                doPushCurrentDataList(ptIds, taskOther.getPushAlarmInfo());
            } else {
                //创建定时任务
                createCurrentPushOtherJob(ptIds, taskOther);
            }
        } else {
            if(recycleNum <= 1){
                return;
            }
            //推送上期。查找数据库是否存在上期数据
            pushLastDataList(ptIds, taskOther, dataXyzh);
        }
    }

    /**
     * 创建本期数据推送定时任务
     *
     * @param ptIds      ptIds
     * @param taskOther  taskOther
     */
    private void createCurrentPushOtherJob(List<Long> ptIds, PushTaskOther taskOther) {
        CurrentPushOtherJobParam param = new CurrentPushOtherJobParam();
        Date current = new Date();
        //获取延时推送时长
        int delayUploadTime = taskOther.getDelayUploadTime() > 0 ? taskOther.getDelayUploadTime() : 10;
        param.setFirstTime(new Date(current.getTime() + delayUploadTime * 60000L));
        param.setFirstTimeCorn(MonitorJobUtil.getCronString(param.getFirstTime()));
        param.setStartDate(current);
        param.setEndDate(new Date(current.getTime() + (delayUploadTime + 5) * 60000L));
        param.setRecycleNum(recycleNum);
        param.setMissionId(controlBoxAo.getMissionId());
        param.setPtIdStr(StringUtils.join(ptIds, ','));
        param.setPushAlarmInfo(taskOther.getPushAlarmInfo() ? "是" : "否");
        surveyJobService.addCurrentPushOtherJob(MonitorJobConst.PUSh_JOB_OTHER_PREFIX + controlBoxAo.getMissionId() + '_' + recycleNum,
                MonitorJobConst.PUSh_TRIGGER_OTHER_PREFIX + controlBoxAo.getMissionId() + '_' + recycleNum,
                CurrentPushOtherJob.class, param);
        log.info("{}本期数据推送定时任务设置成功:{}", controlBoxAo.getMissionName(), param.getFirstTime());
    }

    private void pushLastDataList(List<Long> ptIds, PushTaskOther taskOther, PointDataXyzh dataXyzh){
        doPushLastDataList(recycleNum, ptIds, taskOther, dataXyzh,
                dataXyzhService, dataXyzhCorrectService, infoService, infoCorrectService);
    }

    /**
     * 推送上期数据到同步表
     *
     * @param recycleNum recycleNum
     * @param ptIds      ptIds
     * @param taskOther  taskOther
     * @param dataXyzh   dataXyzh
     */
    public static void doPushLastDataList(int recycleNum, List<Long> ptIds, PushTaskOther taskOther, PointDataXyzh dataXyzh,
                                          IPointDataXyzhService xyzhService, IPointDataXyzhCorrectService xyzhCorrectService,
                                          IAlarmInfoService alarmInfoService, IAlarmInfoCorrectService alarmInfoCorrectService) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataXyzh::getRecycleNum, recycleNum - 1)
                .in(PointDataXyzh::getPid, ptIds)
                .orderByAsc(PointDataXyzh::getId);
        List<PointDataXyzh> list = xyzhService.list(wrapper);
        LambdaQueryWrapper<AlarmInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmInfo::getRecycleNum, recycleNum - 1)
                .in(AlarmInfo::getPtId, ptIds)
                .orderByAsc(AlarmInfo::getId);
        List<AlarmInfo> infoList = alarmInfoService.list(queryWrapper);
        //生成新的观测时间
        Date newGetTime = taskOther.getUseNextTime() == 1 ? dataXyzh.getGetTime()
                : new Date(list.get(0).getGetTime().getTime() + taskOther.getLastDelayTime()*60*1000);
        Date newGetTimePrev = taskOther.getUseNextTime() == 1 ? dataXyzh.getGetTimePrev()
                : new Date(list.get(0).getGetTimePrev().getTime() + taskOther.getLastDelayTime()*60*1000);

        if (!list.isEmpty()) {
            list.forEach(data -> {
                data.setGetTime(newGetTime);
                data.setGetTimePrev(newGetTimePrev);
                data.setCreateTime(newGetTime);
            });
            xyzhCorrectService.saveBatch(DzBeanUtils.listCopy(list, PointDataXyzhCorrect.class));
        }
        if (!infoList.isEmpty()) {
            infoList.forEach(data -> {
                data.setAlarmTime(newGetTime);
                data.setCreateTime(newGetTime);
            });
            alarmInfoCorrectService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfoCorrect.class));
        }
    }

    /**
     * 推送本期数据到同步表
     *
     * @param ptIds         ptIds
     * @param pushAlarmInfo pushAlarmInfo
     */
    private void doPushCurrentDataList(List<Long> ptIds, Boolean pushAlarmInfo) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataXyzh::getRecycleNum, recycleNum)
                .in(PointDataXyzh::getPid, ptIds)
                .orderByAsc(PointDataXyzh::getId);
        List<PointDataXyzh> list = dataXyzhService.list(wrapper);
        LambdaQueryWrapper<AlarmInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmInfo::getRecycleNum, recycleNum)
                .in(AlarmInfo::getPtId, ptIds)
                .orderByAsc(AlarmInfo::getId);
        List<AlarmInfo> infoList = infoService.list(queryWrapper);
        if (!list.isEmpty()) {
            dataXyzhCorrectService.saveBatch(DzBeanUtils.listCopy(list, PointDataXyzhCorrect.class));
        }
        if (!infoList.isEmpty() && pushAlarmInfo) {
            infoCorrectService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfoCorrect.class));
        }
        log.info("{}推送本期数据到同步表完成",controlBoxAo.getMissionName());
    }

    /**
     * 提取配置信息中测点信息
     */
    public List<Long> getSurveyPtIds() {
        return dataXyzhBiz.getPointIdWithMission(controlBoxAo.getMissionId());
    }

    /**
     * 获取托推送任务对象
     * @return PushTask
     */
    private PushTaskOther getPushOtherTask() {
        LambdaQueryWrapper<PushTaskOther> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskOther::getMissionId, controlBoxAo.getMissionId());
        return pushTaskOtherService.getOne(wrapper);
    }

    /**
     * 创建推送定时任务
     */
    private Date createPushTaskJob(Date getTime){
        //获取推送任务对象，判断是否已经开启数据推送
        PushTask pushTask = getPushTask();
        if (pushTask == null ||  1 != pushTask.getStatus()){
            return null;
        }
        //删除上一次的定时任务
        surveyJobService.removeJob(MonitorJobConst.PUSh_JOB_PREFIX + controlBoxAo.getSerialNo(),
                MonitorJobConst.PUSh_TRIGGER_PREFIX + controlBoxAo.getSerialNo());
        //monitorItemId|stCfg|recycleNum|rawDatas|calDatas|ajReport
        //非临时加测，且测量成功后，开启定时任务自动推送数据
        PushJobParam param = new PushJobParam();
        Date current = new Date();
        //获取延时推送时长
        int delayUploadTime = pushTask.getDelayUploadTime() > 3 ? pushTask.getDelayUploadTime() : 3;
        param.setFirstTime(new Date(current.getTime() + delayUploadTime * 60000L));
        param.setFirstTimeCorn(MonitorJobUtil.getCronString(param.getFirstTime()));
        param.setStartDate(current);
        param.setEndDate(new Date(current.getTime() + (delayUploadTime + 5) * 60000L));
        param.setSerialNo(controlBoxAo.getSerialNo());
        param.setMissionId(controlBoxAo.getMissionId());
        param.setRecycleNum(recycleNum);
        param.setThirdPartType(pushTask.getThirdPartType());
        surveyJobService.addPushJob(MonitorJobConst.PUSh_JOB_PREFIX + controlBoxAo.getSerialNo(),
                MonitorJobConst.PUSh_TRIGGER_PREFIX + controlBoxAo.getSerialNo(),
                MonitorPushJob.class, param);
        log.info("{}定时任务设置成功:{}", controlBoxAo.getSerialNo(), param.getFirstTime());
        // 2024/11/26 生成漏测漏传记录
        Date uploadTime = new Date(getTime.getTime() + (delayUploadTime + 2) * 60000L);
        saveRobotSurveyRecord(recycleNum, uploadTime);
        // 2023/6/19 更新测量记录表,比上真实传时间晚两分钟
        return uploadTime;
    }

    /**
     * 获取托推送任务对象
     * @return PushTask
     */
    private PushTask getPushTask() {
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, controlBoxAo.getMissionId());
        List<PushTask> tasks = pushTaskService.list(wrapper);
        return !tasks.isEmpty() ? tasks.get(0) : null;
    }

    //region 测量过程函数
    /**
     * 计算下一测量点序号
     * @param chResultsCount 当前测回测量记录数
     */
    private void calcNextSurveyPointIndex(int chResultsCount) {
        //正镜观测时下一点序号
        if (chResultsCount < surveyCfgPoints.size() - 1) {
            currentSurveyPointIndex++;
        }
        //上半测回第1点归零测量和下半测回第1点开始测量
        else if (chResultsCount == surveyCfgPoints.size() - 1 || chResultsCount == surveyCfgPoints.size()) {
            currentSurveyPointIndex = 1;
        }
        //下半测回倒序测量第2点
        else if (chResultsCount == surveyCfgPoints.size() + 1) {
            currentSurveyPointIndex = surveyCfgPoints.size() - 1;
        }
        //正常倒镜时下一点序号
        else if (chResultsCount > surveyCfgPoints.size() + 1) {
            currentSurveyPointIndex--;
        }
    }

    /**
     * 处理单次测量结果
     **/
    private void processSurveyResultOnce(List<String> results) {
        //完善测量结果
        int pos = surveyResults.size() - 1;
        SurveyResult surveyResult = surveyResults.get(pos);
        SurveyResultProcess.calcSurveyResultOnce(surveyResult, results);
        //检查测量结果
        BackMessage backMsg = new BackMessage();
        this.checkSurveyResultOnce(surveyResult, limitParams, backMsg);

        int num = (int) surveyResults.stream().filter(it -> it.getChIndex() == surveyResult.getChIndex()).count();
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]";
        //单次测量检验合格
        if (backMsg.isOk()) {
            currentRepeatIndexRunOver = 0;
            surveyInfo += backMsg.getMsg();
            surveyBackInfos.add(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

            //准备下一点测量
            prepareNextSurveyNew(num, surveyResult.getChIndex());
        }
        //测量不合格，根据设定的重测次数重测
        else {
            String faceStr = surveyResult.isFace1() ? "正镜" : "倒镜";
            surveyInfo += " 测量失败!---[" + surveyResult.getPtName() + " 第 " + (surveyResult.getChIndex() + 1) + " 测回," + faceStr + "]";
            surveyInfo += backMsg.getMsg();
            //当前重测测点次数少于设置时，重测
            if (currentRepeatIndexRunOver < limitParams.getRepeatNumRunOver()) {
                //删除前一次测量结果
                surveyResults.remove(pos);
                surveyInfo += " --重测[" +
                        (currentRepeatIndexRunOver + 1) + "/" + limitParams.getRepeatNumRunOver() + "]";
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

                surveyOnce(0);
                currentRepeatIndexRunOver++;
            } else {
                currentRepeatIndexRunOver = 0;
                surveyInfo += " --最后一次";
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

                //准备下一点测量
                prepareNextSurveyNew(num, surveyResult.getChIndex());
            }
        }
    }

    /**
     * 处理单次预测量结果
     **/
    private void processPreSurveyResultOnce(List<String> results) {
        //完善测量结果
        int pos = preSurveyResults.size() - 1;
        SurveyResult surveyResult = preSurveyResults.get(pos);
        SurveyResultProcess.calcSurveyResultOnce(surveyResult, results);
        //检查测量结果
        BackMessage backMsg = new BackMessage();
        this.checkSurveyResultOnce(surveyResult, limitParams, backMsg);

        int num = (int) surveyResults.stream().filter(it -> it.getChIndex() == surveyResult.getChIndex()).count();
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]";
        //单次测量检验合格
        if (backMsg.isOk()) {
            currentRepeatIndexRunOver = 0;
            surveyInfo += backMsg.getMsg();
            surveyBackInfos.add(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

            //准备下一点测量
            prepareNextPreSurvey();
        }
        //测量不合格，根据设定的重测次数重测
        else {
            String faceStr = surveyResult.isFace1() ? "正镜" : "倒镜";
            surveyInfo += " 测量失败!---[" + surveyResult.getPtName() + " 第 " + (surveyResult.getChIndex() + 1) + " 测回," + faceStr + "]";
            surveyInfo += backMsg.getMsg();
            //当前重测测点次数少于设置时，重测
            if (currentRepeatIndexRunOver < limitParams.getRepeatNumRunOver()) {
                //删除前一次测量结果
                preSurveyResults.remove(pos);
                surveyInfo += " --重测[" +
                        (currentRepeatIndexRunOver + 1) + "/" + limitParams.getRepeatNumRunOver() + "]";
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

                preSurveyOnce(0);
                currentRepeatIndexRunOver++;
            } else {
                currentRepeatIndexRunOver = 0;
                surveyInfo += " --本次预测量失败!";
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

                currentPreSurveyIndex++;

                //尚未达到最大重测次数，重测
                if (currentPreSurveyIndex <= preSurveyNum) {
                    preSurveying(true);
                } else {//达到最大重测次数，若是单站测量，结束测量；若是多测站联测，标识本站预测量失败
                    if (isMultiStation()) {
                        preSurveyOk = false;
                        prepareStopSurveying("待测",false, false);
                    } else {
                        if (!hasGroupSurvey) {
                            if (!surveyAtOnce){
                                // 2024/12/06 漏测处理,判断是否加测
                                qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                            }
                            try {
                                prepareStopSurveying("待测", true, false);
                            } catch (Exception e) {
                                log.info("异常测量结束异常:{}", e.getMessage());
                            }
                        } else {
                            if (currentGroupIndex == surveyCfgPointGroup.size() - 1) {
                                //测量完成：存在成功测量，创建推送任务，发送测量信息
                                if (hasSurveySuccess){
                                    List<PointDataXyzh> list = getCurrentSurveyResult();
                                    pushToCorrectDb(list.get(0));
                                    if (!surveyAtOnce){
                                        createPushTaskJob(new Date());
                                    }
                                    qwMsgService.sendSurveyResultMsg(list, controlBoxAo.getMissionId(), -1);
                                    try {
                                        prepareStopSurveying("待测", true, false);
                                    } catch (Exception e) {
                                        log.info("编组测量结束异常:{}", e.getMessage());
                                    }
                                } else {
                                    if (!surveyAtOnce){
                                        // 2024/12/06 漏测处理,判断是否加测
                                        qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                                    }
                                }
                            } else {
                                //继续下一组测量
                                String info = currentGroupIndex != surveyCfgPointGroup.size() - 1
                                        ? "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，准备切换下一组测量"
                                        : "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，测量结束";
                                surveyBackInfos.add(info);
                                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                                onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, "测量完成", null));
                                startSurvey(true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查单次测量数据
     * @param surveyResult surveyResult
     * @param limitParams limitParams
     * @param backMsg backMsg
     */
    private void checkSurveyResultOnce(SurveyResult surveyResult, LimitParams limitParams, BackMessage backMsg) {
        //检查T、L、X、Y、Z是否超限
        double dx = surveyResult.getX() - surveyResult.getX0();
        double dy = surveyResult.getY() - surveyResult.getY0();
        double dz = surveyResult.getZ() - surveyResult.getZ0();
        boolean isOkTL = Math.abs(surveyResult.getT()) < limitParams.getLimitT() &&
                Math.abs(surveyResult.getL()) < limitParams.getLimitL();
        boolean isOkXYZ = Math.abs(dx) < limitParams.getLimitX() &&
                Math.abs(dy) < limitParams.getLimitY() &&
                Math.abs(dz) < limitParams.getLimitZ();
        String strTL = "T(″):" + String.format("%.2f", Angle.rad2Deg(surveyResult.getT(), true) * 3600) +
                ";L(″):" + String.format("%.2f", Angle.rad2Deg(surveyResult.getL(), true) * 3600);
        String strDXYZ = "△X(m):" + String.format("%.4f", dx) +
                ";△Y(m):" + String.format("%.4f", dy) +
                ";△Z(m):" + String.format("%.4f", dz);

        String surveyInfo = "";
        if (isOkTL && isOkXYZ) {
            surveyResult.setSuccess(true);
            backMsg.setOk(true);
            String faceStr = surveyResult.isFace1() ? "正镜" : "倒镜";
            surveyInfo += " 测量成功!---[" + surveyResult.getPtName() + " ," + faceStr + "]";
        } else {
            surveyInfo += " --";
            if (!isOkTL) {
                surveyInfo += "[仪器倾斜补偿超限]--" + strTL + ";";
            }

            if (!isOkXYZ) {
                surveyInfo += "[坐标偏差超限]--" + strDXYZ + ";";
            }
        }

        backMsg.setMsg(surveyInfo);
    }

    /**
     * 准备下一点测量(包含0.5测回和完整测回的情况)
     * @param chResultsCount 当前测回测量记录数
     * @param chIndex 当前测回序号
     */
    private void prepareNextSurveyNew(int chResultsCount, int chIndex) {
        //0.5测回
        if (limitParams.getChNum() == 0) {
            prepareNextSurveyHalfCh(chResultsCount);
        }
        //其他完整的测回
        else{
            prepareNextSurvey(chResultsCount, chIndex);
        }
    }

    /**
     * 准备下一点预测量
     */
    private void prepareNextPreSurvey(){
        //当前预测量结果数
        int k = preSurveyResults.size();
        //当前预测量成果完成
        if (k == (preSurveyCfgPoints.size() - 1) * 2) {
            preSurveying = false;
            String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]  预测关键点结束!";
            surveyBackInfos.add(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

            //开始正式测量
            surveying(false);
            return;
        }

        SurveyResult result = preSurveyResults.get(k - 1);
        //盘右完成，开始下一点测量，否则进行盘右测量
        if (!result.isFace1()) {
            currentSurveyPointIndex++;
        }
        preSurveyOnce(0);
    }

    /**
     * 准备下一点测量
     * @param chResultsCount 当前测回测量记录数
     * @param chIndex 当前测回序号
     */
    private void prepareNextSurvey(int chResultsCount, int chIndex) {
        //完成一测回
        if (chResultsCount == (surveyCfgPoints.size() * 2)) {
            //未完成所有测回
            if (currentChIndex < limitParams.getChNum() - 1) {
                //当前测回序数+1
                currentChIndex++;
                //开始下一测回测量
                surveyOnce(0);
            }
            //完成所有测回--数据质量检查及处理,返回测量数据
            else {
                // 2024/3/7 数据处理已经修改
                processSurveyResultAllWithMeteSurvey(false);
            }
        }
        //测回内测量
        else {
            //计算下一测量点序号
            this.calcNextSurveyPointIndex(chResultsCount);
            //继续测量
            surveyOnce(0);
        }
    }

    /**
     * 气象信息采集+数据后处理
     * @param isHalfCh 是否半测回测量
     */
    private void processSurveyResultAllWithMeteSurvey(boolean isHalfCh){
        //手动获取温度气压值，或后处理方法为“周期坐标差分法”时，不用自动采集
        if (meteInfo.contains("manual") || processMethod == 1) {
            processSurveyResultAllWithMeteCorrect(isHalfCh);
        }
        //自动采集温度气压
        else {
            String[] meteInfos = meteInfo.split(",");
            String serialNo = meteInfos[0];
            currentBoxMete = null;
            Optional<ControlBoxMete> optional = ControlBoxHandler.getOnlineMeteBoxes()
                    .stream().filter(item -> serialNo.equals(item.getSerialNo()))
                    .findAny();
            optional.ifPresent(meteBox -> currentBoxMete = meteBox);

            //掉线，无法测量
            if (currentBoxMete == null || !currentBoxMete.isOnLine()) {

                ControlBox controlBox = controlBoxService.getById(controlBoxAo.getId());
                String surveyInfo = (controlBox != null ? controlBox.getName() : serialNo) + " 离线无法测量！ 启用初始设置数据--";
                surveyInfo += "温度(℃)：" + meteInfos[1] + " 气压(KPa)：" + meteInfos[2] + " 湿度(%)：" +
                        meteInfos[3];
                surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN)  + "] " + surveyInfo;
                surveyBackInfos.add(surveyInfo);
                processSurveyResultAllWithMeteCorrect(isHalfCh);
                return;
            }

            // 开始采集温度气压数据
            deviceMeteBiz.refreshCurrentBoxMete(currentBoxMete);
            deviceMeteBiz.survey(false);
        }
    }

    /**
     * 气象改正+全部数据后处理
     * @param isHalfCh 是否半测回测量
     */
    private void processSurveyResultAllWithMeteCorrect(boolean isHalfCh){
        //气象改正处理
        List<SurveyResult> correctSurveyResults = correctSurveyResultAllByMete(surveyResults, meteInfo);

        StringBuilder sb = new StringBuilder();
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 测量结束!";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        surveyInfo = "\r\n--------- 数据质量检核  ---------";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo).append("\r\n");
        if (isHalfCh) {
            finalOk = processSurveyResultAllHalfCh(surveyCfgPoints, limitParams,
                    correctSurveyResults, surveyCalcResults, surveyCalcResults2Db, sb);
            surveyData = getSurveyDataHalfCh(controlBoxAo.getMissionId(), recycleNum, surveyResults, surveyCalcResults2Db);
        } else {
            finalOk = processSurveyResultAll(surveyCfgPoints, limitParams,
                    correctSurveyResults, surveyCalcResults, surveyCalcResults2Db, sb);
            surveyData = getSurveyData(controlBoxAo.getMissionId(), recycleNum, surveyResults, surveyCalcResults2Db);
        }
        if (hasGroupSurvey){
            String info = currentGroupIndex != surveyCfgPointGroup.size() - 1
                    ? "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，准备切换下一组测量"
                    : "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，测量结束";
            surveyBackInfos.add(info);
            sb.append("\r\n").append(info);
            onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, "测量完成", null));
        }
        qwMsgService.sendSurveyProcessMsg(sb.toString(), controlBoxAo.getMissionId());
        //一次完整的测量数据处理成功
        if (finalOk) {
            if(!hasGroupSurvey){
                try {
                    prepareStopSurveying("待测", true, false);
                } catch (Exception e) {
                    log.info("测量完成结束异常:{}", e.getMessage());
                }
            } else {
                if (currentGroupIndex == surveyCfgPointGroup.size() - 1) {
                    try {
                        prepareStopSurveying("待测", true, false);
                    } catch (Exception e) {
                        log.info("编组测量测量结束异常:{}", e.getMessage());
                    }
                } else {
                    //保存本次结果
                    List<Long> surveyCfgPointIds = new ArrayList<>();
                    this.surveyCfgPointGroup.forEach(surveyCfgPoint -> surveyCfgPoint.forEach(item -> surveyCfgPointIds.add(item.getId())));
                    PointDataXyzhDto dataDto = dataXyzhBiz.saveRobotResultOnSuccess(finalResults, surveyData, false, isSurveyAtOnce(), hasGroupSurvey, surveyCfgPointIds, currentGroupIndex);
                    List<PointDataXyzh> dataXyzhs = dataDto.getDataList();

                    // region 2025/06/20 生成漏测点信息并发送
                    String missInfo = createMissSurveyPtInfo(dataXyzhs);
                    if (missInfo != null) {
                        qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum, missInfo);
                    }
                    // endregion 2024/06/20 生成漏测点信息并发送

                    //超限报警发送声光报警指令
                    if(dataDto.isHasAlarm()){
                        ChannelHandlerUtil.sendSoundAlarmCode(this.soundAlarmCfg, 3);
                    }

                    //输出测量反馈信息
                    if (!surveyBackInfos.isEmpty()) {
                        Date getTime = surveyDataBiz.getLatestTime(recycleNum, controlBoxAo.getMissionId());
                        getTime = getTime == null ? new Date() : getTime;
                        String logPath = FileUtil.BASE_PATH + File.separatorChar
                                + "UserLog" + File.separatorChar
                                + controlBoxAo.getProjectName() + "_"
                                + controlBoxAo.getMissionName() + "_"
                                + DateUtil.dateToDateString(getTime, "yyyyMMdd_HHmm") + ".log";
                        //保存测量过程信息
                        FileUtil.exportUserLog(surveyBackInfos, logPath);
                    }
                    hasSurveySuccess = true;
                    startSurvey(true);
                }
            }
        }
        //一次完整的测量数据处理失败
        else {
            //需要全部重测
            if (currentRepeatIndexFail < limitParams.getRepeatNumFail()) {
                surveyInfo = "";
                surveyInfo += "全部重测[" + currentRepeatIndexFail + "/" + limitParams.getRepeatNumFail() + "]";
                currentRepeatIndexFail++;
                surveyBackInfos.add(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());

                surveying(true);
            }
            //准备停止测量
            else {
                if(!hasGroupSurvey){
                    if (!surveyAtOnce) {
                        // 2024/11/26 漏测处理,判断是否加测
                        qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                    }
                    try {
                        prepareStopSurveying("待测", true, false);
                    } catch (Exception e) {
                        log.info("测量结束异常:{}", e.getMessage());
                    }
                } else {
                    if (currentGroupIndex == surveyCfgPointGroup.size() - 1) {
                        try {
                            prepareStopSurveying("待测", true, false);
                        } catch (Exception e) {
                            log.info("处理测量结束异常:{}", e.getMessage());
                        }
                    } else {
                        startSurvey(true);
                    }
                }
            }
        }
    }

    /**
     * 专门用于“0.5测回”测量数据处理的下一点测量
     * @param chResultsCount 测量记录数
     */
    private void prepareNextSurveyHalfCh(int chResultsCount)
    {
        //完成所有点测量
        if (chResultsCount == surveyCfgPoints.size()) {
            processSurveyResultAllWithMeteSurvey(true);
        }
        //测量下一点
        else {
            //计算下一测量点序号
            //正镜观测时下一点序号
            if (chResultsCount < surveyCfgPoints.size() - 1) {
                currentSurveyPointIndex++;
            }
            //上半测回第1点归零测量点序号
            else if (chResultsCount == surveyCfgPoints.size() - 1) {
                currentSurveyPointIndex = 1;
            }

            //继续测量
            surveyOnce(0);
        }
    }

    /**
     * 准备停止测量
     * @param surveyStatus 停止测量后的测量状态(停测/待测)
     * @param realStop 是否真实关闭设备:默认true
     * @param directInvoke 是否直接调用返回
     */
    private void prepareStopSurveying(String surveyStatus, boolean realStop, boolean directInvoke) {
        controlBoxAo.setSurveyStatus(surveyStatus);
        this.startSurveying = false;
        this.surveyFinished = true;
        //关闭设备
        if (realStop) {
            this.prepareStop(directInvoke);
        }
    }

    /**
     * 测量过程异常处理
     * @param rtCode rtCode
     * @param commandName commandName
     */
    private void processExceptionOnSurvey(RtCode rtCode, String commandName) {
        String surveyInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]";
        //处于测量过程中
        if (!surveyResults.isEmpty()) {
            int pos = surveyResults.size() - 1;
            SurveyResult result = surveyResults.get(pos);
            long num = surveyResults.stream().filter(it -> it.getChIndex() == result.getChIndex()).count();

            String faceStr = result.isFace1() ? "正镜" : "倒镜";
            surveyInfo = "[" + result.getPtName() + " 第 " + (result.getChIndex() + 1) + " 测回," + faceStr + "," +
                    rtCode.getName() + "：" + rtCode.getNote() + "]";
        }

        //控制器掉线
        if (Objects.equals(rtCode.getName(), "SYS_GRC_OFFLINE")) {
            surveyInfo = " 控制器掉线!---"+surveyInfo;
            log.info(surveyInfo);
            recordInfo(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
            this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OFFLINE, commandName, null));
        }
        //取消测量，收起仪器镜头
        else if (Objects.equals(rtCode.getName(), "SYS_GRC_CANCEL")) {
            surveyInfo = " 测量中断!---" + surveyInfo;
            log.info("测量中断!---{}",surveyInfo);
            //准备停测
            recordInfo(surveyInfo);
            qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
            prepareStopSurveying(realStop ? "停测" : "待测", false, true);
        }
        //其他异常
        else {
            //测量过程异常处理--真正测量产生的异常
            if ("CheckMeasureAndGetFullResult".equals(commandName)) {
                surveyInfo = this.processExceptionOnSurveyReal(rtCode);
                recordInfo(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                log.info("测量过程异常--{}",surveyInfo);
            } else if ("OpenDevice".equals(commandName)) {
                if (openDeviceRetryTime > 0){
                    openDeviceRetryTime--;
                    log.info("开机异常--再次重新开机");
                    recordInfo(surveyInfo);
                    qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                    this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null));
                    //以开机作为开始测量的起始，开始失败允许整体重试一次
                    deviceBiz.openDevice(false);
                    //记录测量信息
                    return;
                }

                surveyInfo += " 开机失败!请检查仪器连接情况";
                log.info("开机异常--{}",surveyInfo);
                recordInfo(surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                //激发任务完成事件
                if(isMultiStation()){
                    // 2023/5/26 这里开机失败设置为了停测 ,实际不要停测，恢复到待测即可
                    prepareStopSurveying("待测",false, true);
                    if (multiSurveyOperateCompleted!=null) {
                        // 2024/3/11 开机异常时处理
                        multiSurveyOperateCompleted.accept(new MultiOperateCompleteResult(
                                true, CommonUtil.SYS_GRC_ERR, commandName
                        ));
                    }
                } else {
                    if (hasGroupSurvey){
                        String info = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量异常结束，测量结束";
                        surveyBackInfos.add(info);
                        qwMsgService.sendSurveyProcessMsg(info, controlBoxAo.getMissionId());
                        onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, "测量完成", null));

                        if (currentGroupIndex == surveyCfgPointGroup.size() - 1){
                            //测量完成：存在成功测量，创建推送任务，发送测量信息
                            if (hasSurveySuccess){
                                List<PointDataXyzh> list = getCurrentSurveyResult();
                                pushToCorrectDb(list.get(0));
                                if (!surveyAtOnce){
                                    createPushTaskJob(new Date());
                                }
                                qwMsgService.sendSurveyResultMsg(list, controlBoxAo.getMissionId(), -1);
                            } else {
                                if (!surveyAtOnce){
                                    // 2024/12/06 漏测处理,判断是否加测
                                    qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                                }
                            }
                            prepareStopSurveying("待测",false, true);
                        } else {
                            startSurvey(true);
                        }
                    } else {
                        if (!surveyAtOnce) {
                            // 2024/11/26 漏测处理,判断是否加测
                            qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                        }
                        prepareStopSurveying("待测",false, true);
                    }
                }
                this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null));
            } else if ("PrepareStop".equals(commandName)) {
                surveyInfo += " 停机失败!";
                log.info("停机异常--{}",surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                //激发任务完成事件
                handleActionCompleted((new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null)));
                this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null));
                startSurveying = false;
            } else {
                surveyInfo += " 其他错误!--"+ commandName;
                log.info("其他异常--{}",surveyInfo);
                qwMsgService.sendSurveyProcessMsg(surveyInfo, controlBoxAo.getMissionId());
                recordInfo(" 其他错误!--"+ commandName);
                this.onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null));
            }
        }
    }

    /**
     * 追加记录信息
     **/
    private void recordInfo(String surveyInfo){
        //记录测量信息
        if (StringUtils.isNotEmpty(surveyInfo)) {
            surveyInfo = "[" +DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]" + surveyInfo;
            surveyBackInfos.add(surveyInfo);
        }
    }

    /**
     * 测量过程异常处理--真正测量产生的异常
     * @param rtCode rtCode
     */
    private String processExceptionOnSurveyReal(RtCode rtCode) {
        //根据是否预测关键点状态，赋值测量结果列表
        List<SurveyResult> surveyResultList = isPreSurveying() ? preSurveyResults : surveyResults;
        String surveyInfo = "";
        int pos = surveyResultList.size() - 1;
        SurveyResult result = surveyResultList.get(pos);
        int num = (int)surveyResultList.stream().filter(it -> it.getChIndex() == result.getChIndex()).count();

        String faceStr = result.isFace1() ? "正镜" : "倒镜";
        surveyInfo += " 测量失败!---[" + result.getPtName() + " 第 " + (result.getChIndex() + 1) + " 测回," + faceStr + "," +
                rtCode.getName() + "：" + rtCode.getName() + "]";
        //当前重测测点次数少于设置时，重测
        if (currentRepeatIndexCover < limitParams.getRepeatNumCover()) {
            surveyResultList.remove(pos);
            surveyInfo += " --" + limitParams.getPauseTimeCover() + "秒后重测[" +
                    (currentRepeatIndexCover + 1) + "/" + limitParams.getRepeatNumCover() + "]";
            if (preSurveying) {
                this.preSurveyOnce(limitParams.getPauseTimeCover());
            } else {
                this.surveyOnce(limitParams.getPauseTimeCover());
            }
            currentRepeatIndexCover++;
        }
        //达到重测次数,测量失败
        else {
            currentRepeatIndexCover = 0;
            surveyInfo += preSurveying ? " --本次预测量失败!" : " --最后一次";
            surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "]" + surveyInfo;
            surveyBackInfos.add(surveyInfo);
            surveyInfo = "";//最后一次测量的反馈信息直接添加的列表，不再返回--避免最后一次测量的消息在计算消息之后

            if (preSurveying) {
                if (hasGroupSurvey && currentGroupIndex != surveyCfgPointGroup.size() - 1) {
                    String info = currentGroupIndex != surveyCfgPointGroup.size() - 1
                            ? "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，准备切换下一组测量"
                            : "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 编组" + (currentGroupIndex + 1) + "测量完成，测量结束";
                    surveyBackInfos.add(info);
                    onActionCompleted(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, "测量完成", null));
                    startSurvey(true);
                    return surveyInfo;
                }
                try {
                    prepareStopSurveying("待测", true, false);
                } catch (Exception e) {
                    log.info("预测量结束异常:{}", e.getMessage());
                }

                if (!surveyAtOnce){
                    // 2024/12/06 漏测处理,判断是否加测
                    qwMsgService.handleSurveyFail(controlBoxAo.getMissionId(), controlBoxAo.getSerialNo(), recycleNum);
                }
                return surveyInfo;
            }
            //准备下一点测量
            prepareNextSurveyNew(num, result.getChIndex());
        }

        return surveyInfo;
    }
    //endregion 测量过程函数

    //region 气象更正处理
    private List<SurveyResult> correctSurveyResultAllByMete(List<SurveyResult> surveyResults, String meteInfo) {
        //后处理方法为“周期坐标差分法”时，不用改正
        if (processMethod == 1)
        {
            return surveyResults;
        }

        List<SurveyResult> results = new ArrayList<>();
        double deltD = 0.0; //斜距气象改正值(ppm)
        String[] meteArr = meteInfo.split(",");
        double t = Double.parseDouble(meteArr[1]); //温度(℃)
        double p = Double.parseDouble(meteArr[2]); //气压(Kpa)
        double w = Double.parseDouble(meteArr[3]); //相对湿度(去除百分号)

        if ( 1 == controlBoxAo.getDeviceType()){
            deltD = correctSurveyResultAllByMeteLeica(t, p, w);
        }

        for (SurveyResult result : surveyResults) {
            if (result == null) {
                continue;
            }
            SurveyResult newResult = DzBeanUtils.propertiesCopy(result, SurveyResult.class);
            newResult.setSd(newResult.getSd() + deltD*newResult.getSd() / 1000.0);
            results.add(newResult);
        }
        return results;
    }

    /**
     * 计算徕卡全站仪的距离气象改正值(ppm)
     * @param t 温度
     * @param p 气压
     * @param w 相对湿度
     */
    private double correctSurveyResultAllByMeteLeica(double t, double p, double w)
    {
        double a = 1 / 273.15;
        double x = 7.5 * t / (237.3 + t) + 0.7857;
        return (286.38 - (0.29535 * p / (1 + a * t) - 0.0004126 * w * Math.pow(10, x) / (1 + a * t)))*1.0e-6;
    }
    //endregion

    //region 测量数据后处理
    /**
     * 处理所测测量结果
     *
     * @param surveyCfgPoints      配置测量点列表
     * @param limitParams          测量限差参数列表
     * @param surveyResults        测量结果
     * @param surveyCalcResults    测量计算结果
     * @param surveyCalcResults2Db 需要存入数据库的测量计算结果
     * @param sb
     */
    private boolean processSurveyResultAll(List<SurveyCfgPoint> surveyCfgPoints,
                                           LimitParams limitParams, List<SurveyResult> surveyResults,
                                           List<SurveyCalcResult> surveyCalcResults, List<SurveyCalcResult> surveyCalcResults2Db, StringBuilder sb) {
        //1.获取测量计算成果列表
        //获取测回序数列表
        List<Integer> chIdxs = surveyResults.stream().map(SurveyResult::getChIndex).distinct().collect(Collectors.toList());
        //各测回内计算
        for (int chIdx : chIdxs) {
            SurveyResultProcess.calcSurveyResultCh(surveyCalcResults, surveyResults, chIdx);
        }

        //各测回间计算
        SurveyResultProcess.calcSurveyResultAll(surveyCalcResults);
        //添加新的结果列表
        surveyCalcResults2Db.addAll(surveyCalcResults);

        //2.测回内检核
        BackMessage backMsg;
        String surveyInfo = "1.测回内数据检核:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        for (int chIdx : chIdxs) {
            backMsg = new BackMessage();
            this.checkSurveyResultCh(surveyCalcResults, limitParams, chIdx, backMsg);
            surveyBackInfos.add(backMsg.getMsg());
            sb.append(backMsg.getMsg());
        }

        //3.测回间检核
        surveyInfo = "\r\n2.测回间数据检核:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        backMsg = new BackMessage();
        this.checkSurveyResultAll(surveyCalcResults, limitParams, backMsg);
        surveyBackInfos.add(backMsg.getMsg());
        sb.append(backMsg.getMsg());

        surveyInfo = "\r\n3.数据整体检核:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        //4.数据整体检核
        if(processMethod != 2) {
            backMsg = new BackMessage();
            this.checkResultAll(surveyCalcResults, surveyCfgPoints, limitParams, backMsg);
            surveyBackInfos.add(backMsg.getMsg());
            sb.append(backMsg.getMsg());
        } else {
            surveyBackInfos.add("...合格");
            sb.append("...合格");
        }

        //数据整体检查不合格，返回
        if (!backMsg.isOk()) {
            return false;
        }

        //参与多站联测，获取测站信息后返回
        if (multiStation){
            //获取测站信息
            SurveyStation station = new SurveyStation();
            List<Point3d> ptList = new ArrayList<>();
            getPtListAndStationForAdjust(surveyCfgPoints, surveyCalcResults,
                    ptList, station);
            surveyStation = station;

            surveyInfo = "\r\n成功获取测站信息，等待整网平差...";
            surveyBackInfos.add(surveyInfo);
            sb.append(surveyInfo);

            return backMsg.isOk();
        }

        //5.数据后处理计算
        surveyInfo = "\r\n4.数据后处理计算:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        backMsg = new BackMessage();

        switch (processMethod){
            case 0:
                //经典测量平差方法
                processCalcResultsNew(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 1:
                //旋转参数法
                processCalcResults(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 3:
                //极坐标法
                processCalcResultsPolar(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 2:
                //实测坐标法
                processCalcResultsRaw(surveyCfgPoints, surveyCalcResults);
                backMsg.setOk(true);
                backMsg.setMsg("...成功");
                break;
        }

//        //经典测量平差方法
//        if (processMethod == 0) {
//            processCalcResultsNew(surveyCfgPoints, surveyCalcResults, backMsg);
//        } else if (processMethod == 1)
//        //周期坐标差分法
//        {
//            processCalcResults(surveyCfgPoints, surveyCalcResults, backMsg);
//        }
//        //不平差，直接使用实测数据
//        else {
//            processCalcResultsRaw(surveyCfgPoints, surveyCalcResults);
//            backMsg.setOk(true);
//            backMsg.setMsg("...成功");
//        }
        surveyBackInfos.add(backMsg.getMsg());
        sb.append(backMsg.getMsg());
        return backMsg.isOk();
    }

    private boolean processSurveyResultAllHalfCh(List<SurveyCfgPoint> surveyCfgPoints, LimitParams limitParams,
                                                 List<SurveyResult> surveyResults, List<SurveyCalcResult> surveyCalcResults, List<SurveyCalcResult> surveyCalcResults2Db, StringBuilder sb)
    {
        //获取计算成果列表
        List<SurveyResult> selResult = surveyResults.stream()
                .filter(item -> item.isFace1() && item.isSuccess())
                .collect(Collectors.toList());
        for (SurveyResult r1 : selResult) {
            SurveyCalcResult newResult = new SurveyCalcResult();
            newResult.setId(r1.getId());
            newResult.setPtName(r1.getPtName());
            newResult.setAsFixed(r1.isAsFixed());
            newResult.setHa1(r1.getHa());
            newResult.setVa1(r1.getVa());
            newResult.setSd1(r1.getSd());
            newResult.setHa(r1.getHa());
            newResult.setVa(r1.getVa());
            newResult.setSd(r1.getSd());
            newResult.setX(r1.getX());
            newResult.setY(r1.getY());
            newResult.setZ(r1.getZ());

            surveyCalcResults.add(newResult);
        }
        surveyCalcResults2Db.addAll(surveyCalcResults);

        //1.测量成果归零差检查
        BackMessage backMsg = new BackMessage();
        String surveyInfo = "1.归零差检查:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        checkSurveyResultHalfCh(surveyCalcResults, limitParams, backMsg);
        surveyBackInfos.add(backMsg.getMsg());
        sb.append(backMsg.getMsg());

        //2.数据整体检核
        surveyInfo = "\r\n2.数据整体检核:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);
        if(processMethod != 2){
            backMsg = new BackMessage();
            checkResultAll(surveyCalcResults, surveyCfgPoints, limitParams,  backMsg);
            surveyBackInfos.add(backMsg.getMsg());
            sb.append(backMsg.getMsg());
            backMsg.setMsg(backMsg.getMsg());
        }else {
            surveyBackInfos.add("...合格");
            sb.append("...合格");
        }

        //数据整体检查不合格，返回
        if (!backMsg.isOk()) {
            return false;
        }

        //参与多站联测，获取测站信息后返回
        if (multiStation){
            //获取测站信息
            SurveyStation station = new SurveyStation();
            List<Point3d> ptList = new ArrayList<>();
            getPtListAndStationForAdjust(surveyCfgPoints, surveyCalcResults,
                    ptList, station);
            surveyStation = station;

            surveyInfo = "\r\n成功获取测站信息，等待整网平差...";
            surveyBackInfos.add(surveyInfo);
            sb.append(surveyInfo);

            return backMsg.isOk();
        }

        //3.数据后处理计算
        backMsg = new BackMessage();
        surveyInfo = "\r\n3.数据后处理计算:";
        surveyBackInfos.add(surveyInfo);
        sb.append(surveyInfo);

        switch (processMethod){
            case 0:
                //经典测量平差方法
                processCalcResultsNew(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 1:
                //旋转参数法
                processCalcResults(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 3:
                //极坐标法
                processCalcResultsPolar(surveyCfgPoints, surveyCalcResults, backMsg);
                break;
            case 2:
                //实测坐标法
                processCalcResultsRaw(surveyCfgPoints, surveyCalcResults);
                backMsg.setOk(true);
                backMsg.setMsg("...成功");
                break;
        }
//        //经典测量平差方法
//        if (processMethod == 0) {
//            processCalcResultsNew(surveyCfgPoints, surveyCalcResults, backMsg);
//        } else if (processMethod == 1)
//        //周期坐标差分法
//        {
//            processCalcResults(surveyCfgPoints, surveyCalcResults, backMsg);
//        } else
//        //不平差，直接使用实测数据
//        {
//            processCalcResultsRaw(surveyCfgPoints, surveyCalcResults);
//            backMsg.setOk(true);
//            backMsg.setMsg("...成功");
//        }

        surveyBackInfos.add(backMsg.getMsg());
        sb.append(backMsg.getMsg());
        return backMsg.isOk();
    }

    /**
     * 数据后处理
     * @param surveyCfgPoints surveyCfgPoints
     * @param surveyCalcResults surveyCalcResults
     * @param backMsg backMsg
     */
    private void processCalcResults(List<SurveyCfgPoint> surveyCfgPoints, List<SurveyCalcResult> surveyCalcResults, BackMessage backMsg) {
        List<SurveyPoint> surveyPoints0 = new ArrayList<>();
        List<SurveyPoint> surveyPts = getPtsFromCalcResults(surveyCalcResults);
        List<SurveyPoint> fixPts = surveyPts.stream().filter(SurveyPoint::isAsFixed).collect(Collectors.toList());
        //fixPts一定有值，不用判空
        for (SurveyPoint fixPt : fixPts) {
            SurveyPoint fixPt0 = new SurveyPoint(fixPt.getId(), fixPt.getName(), fixPt.isAsFixed());
            //用测站配置的坐标值赋值（一定有对应点，不用判空）
            Optional<SurveyCfgPoint> optional = surveyCfgPoints.stream().filter(it -> it.getId() == fixPt.getId()).findAny();
            if (optional.isPresent()){
                fixPt0.setX(optional.get().getX());
                fixPt0.setY(optional.get().getY());
                fixPt0.setZ(optional.get().getZ());
            }
            surveyPoints0.add(fixPt0);
        }

        // 2024/3/13 测站存在未赋值null情况。修改不管是否稳定，测站都赋值
        SurveyCfgPoint theSt = surveyCfgPoints.get(0);
        SurveyPoint station = new SurveyPoint();
        station.setId(theSt.getId());
        station.setName(theSt.getName());
        station.setX(theSt.getX());
        station.setY(theSt.getY());
        station.setZ(theSt.getZ());

        List<SurveyPoint> thePts = SurveyResultProcess.calcTransPoints(surveyPoints0, surveyPts, station);
        backMsg.setOk(!thePts.isEmpty());
        backMsg.setMsg(!thePts.isEmpty() ? "...成功!" : "...失败!");;

        finalResults = new ArrayList<>();
        for (SurveyPoint pt : thePts) {
            String result = pt.getId() + "," + pt.getName() + ",";
            result += pt.getX() + "," + pt.getY() + "," + pt.getZ() + ",";
            result += pt.getHa() + "," + pt.getVa() + "," + pt.getSd();
            finalResults.add(result);
        }
    }

    /**
     * 数据后处理(新算法)-单站
     */
    private void processCalcResultsNew(List<SurveyCfgPoint> surveyCfgPoints, List<SurveyCalcResult> surveyCalcResults, BackMessage backMsg) {
        SurveyStation station = new SurveyStation();
        List<Point3d> ptList = new ArrayList<>();
        getPtListAndStationForAdjust(surveyCfgPoints, surveyCalcResults, ptList, station);

        double[] m02 = new double[]{1.0};
        List<SurveyStation> stations = new ArrayList<>();
        stations.add(station);
        //计算未知点概略坐标
        boolean isOk = SurveyResultProcessNewClassicUpdate.calcPtXYZ0(stations);
        //若不成功，退出平差计算
        if (!isOk) {
            backMsg.setOk(false);
            backMsg.setMsg("已知数据不足以推算概略坐标--无法平差计算!");
            //平差报告
            adjReport = SurveyResultProcessNewClassicUpdate.showResult(backMsg.isOk(), backMsg.getMsg(), ptList, stations, m02[0]);
            return;
        }
        SurveyResultProcessNewClassicUpdate.adjust(ptList, stations, m02);

        backMsg.setOk(!ptList.isEmpty());
        backMsg.setMsg(!ptList.isEmpty() ? "...成功!" : "...失败!");
        //平差报告
        adjReport = SurveyResultProcessNewClassicUpdate.showResult(backMsg.isOk(), backMsg.getMsg(), ptList, stations, m02[0]);

        finalResults = new ArrayList<>();
        String result = "";
        for (Point3d pt : ptList) {
            result = pt.getId() + "," + pt.getName() + ",";
            result += pt.getX() + "," + pt.getY() + "," + pt.getZ() + ",";
            String rawInfo = " 0,0,0";//Ha、Va、Sd数值,多站情况下无需赋值，默认为0
            //单站情况下，赋值测站到测点联线的Ha、Va、Sd数值
            if (stations.size() == 1) {
                SurveyLine line= stations.get(0).getSurveyLines().stream()
                        .filter(item -> item.getPoint2().getId() == pt.getId()).findAny()
                        .orElse(null);
                if (line != null) {
                    rawInfo = line.getHa() + "," + line.getVa() + "," + line.getSd();
                }
            }
            result += rawInfo;
            finalResults.add(result);
        }
    }

    /**
     * 数据后处理-极坐标法
     *
     * @param surveyCfgPoints surveyCfgPoints
     * @param surveyCalcResults surveyCalcResults
     * @param backMsg backMsg
     */
    private void processCalcResultsPolar(List<SurveyCfgPoint> surveyCfgPoints, List<SurveyCalcResult> surveyCalcResults, BackMessage backMsg) {
        SurveyStation station = new SurveyStation();
        List<Point3d> ptList = new ArrayList<>();
        getPtListAndStationForAdjust(surveyCfgPoints, surveyCalcResults, ptList, station);

        List<SurveyStation> stations = new ArrayList<>();
        stations.add(station);
        //计算未知点概略坐标
        boolean isOk = SurveyResultProcessNewClassicUpdate.calcPtXYZ0(stations);
        backMsg.setOk(isOk);
        backMsg.setMsg(isOk ? "...成功!" : "...失败!--已知数据不足");

        if(!backMsg.isOk()){
            return;
        }

        //赋值XYZ,概略坐标就是最后的极坐标计算值，后续无平差计算
        for (Point3d pt : ptList) {
            pt.setX(pt.getX0());
            pt.setY(pt.getY0());
            pt.setZ(pt.getZ0());
        }

        finalResults = new ArrayList<>();
        String result = "";
        for (Point3d pt: ptList) {
            result = pt.getId() + "," + pt.getName() + ",";
            result += pt.getX() + "," + pt.getY() + "," + pt.getZ() + ",";
            String rawInfo = "0,0,0";//Ha、Va、Sd数值,多站情况下无需赋值，默认为0
            //单站情况下，赋值测站到测点联线的Ha、Va、Sd数值
            if (stations.size() == 1) {
                Optional<SurveyLine> optional = stations.get(0).getSurveyLines().stream().filter(it -> it.getPoint2().getId() == pt.getId()).findAny();
                if (optional.isPresent()) {
                    SurveyLine line = optional.get();
                    rawInfo = line.getHa() + "," + line.getVa() + "," + line.getSd();
                }
            }
            result += rawInfo;
            finalResults.add(result);
        }
    }

    /**
     * 用实测坐标作为最终监测成果
     * @param surveyCfgPoints surveyCfgPoints
     * @param surveyCalcResults surveyCalcResults
     */
    private void processCalcResultsRaw(List<SurveyCfgPoint> surveyCfgPoints, List<SurveyCalcResult> surveyCalcResults) {
        List<SurveyPoint> surveyPts = getPtsFromCalcResults(surveyCalcResults);
        SurveyCfgPoint theSt = surveyCfgPoints.get(0);
        SurveyPoint station = new SurveyPoint();
        station.setId(theSt.getId());
        station.setName(theSt.getName());
        station.setX(theSt.getX());
        station.setY(theSt.getY());
        station.setZ(theSt.getZ());

        List<SurveyPoint> surveyPtsNew = new ArrayList<>();
        surveyPtsNew.add(station);//加入测站坐标
        surveyPtsNew.addAll(surveyPts);

        finalResults = new ArrayList<>();
        for (SurveyPoint point : surveyPtsNew) {
            String result = point.getId() + "," + point.getName() + ",";
            result += point.getX() + "," + point.getY() + "," + point.getZ() + ",";
            result += point.getHa() + "," + point.getVa() + "," + point.getSd();
            finalResults.add(result);
        }
    }

    /**
     * 获取测点列表和测站信息
     */
    private void getPtListAndStationForAdjust(List<SurveyCfgPoint> surveyCfgPoints,
                                              List<SurveyCalcResult> surveyCalcResults, List<Point3d> ptList,SurveyStation station){
        List<SurveyPoint> surveyPts = getPtsFromCalcResults(surveyCalcResults);
        //从测量数据中获取平差计算所需测点列表和测站列表
        //测站
        Point3d theSt = new Point3d(surveyCfgPoints.get(0).getId(),surveyCfgPoints.get(0).getName());
        ptList.add(theSt);
        //观测点
        for (SurveyPoint pt : surveyPts) {
            Point3d thePt = new Point3d(pt.getId(), pt.getName());
            thePt.setAsFixed(pt.isAsFixed());
            //固定点的坐标值要为初始值--20250304Fixed
            if (thePt.isAsFixed())
            {
                Optional<SurveyCfgPoint> optional = surveyCfgPoints.stream().filter(item -> item.getId() == thePt.getId()).findAny();
                if (optional.isPresent()) {
                    SurveyCfgPoint thePt0 = optional.get();
                    //固定点一定有初始值，不用判空
                    thePt.setX(thePt0.getX());
                    thePt.setY(thePt0.getY());
                    thePt.setZ(thePt0.getZ());
                    //固定点的概略坐标值和最终值不变
                    thePt.setX0(thePt.getX());
                    thePt.setY0(thePt.getY());
                    thePt.setZ0(thePt.getZ());
                }
            }//end if
            ptList.add(thePt);
        }

        //测站
        station.setStation(theSt);
        station.setHi(surveyCfgPoints.get(0).getHi());
        //获取测线列表
        List<SurveyLine> lines = new ArrayList<>();
        for (SurveyPoint pt : surveyPts)
        {
            SurveyLine line = new SurveyLine();
            line.setPoint1(theSt);
            line.setPoint2(ptList.stream().filter(item -> item.getId() == pt.getId()).findFirst().orElse(null));
            line.setHa0(pt.getHa());
            line.setHa(pt.getHa());
            line.setVa(pt.getVa());
            line.setVa0(pt.getVa());
            line.setSd0(pt.getSd());
            line.setSd(pt.getSd());
            line.setHt(surveyCfgPoints.stream().filter(item -> item.getId() == pt.getId()).findFirst()
                    .orElse(new SurveyCfgPoint()).getHt());
            lines.add(line);
        }

        station.setSurveyLines(lines);
    }

    /**
     * 整体检查计算成果是否满足数据后处理要求
     * @param surveyCalcResults 测量计算成果列表
     * @param surveyCfgPoints 配置测量点列表
     * @param limitParams 限差类
     * @param msg msg
     */
    private void checkResultAll(List<SurveyCalcResult> surveyCalcResults, List<SurveyCfgPoint> surveyCfgPoints,
                                LimitParams limitParams, BackMessage msg) {
        boolean isOk;
        //数据量是否满足要求(至少要有两个不同点的测量数据)
        int ptNum= (int) surveyCalcResults.stream().map(SurveyCalcResult::getId).distinct().count();
        isOk = ptNum > 2;
        String surveyInfo = "";
        if (isOk) {
            //不同固定点个数是否满足要求
            int fixedNum = (int) surveyCalcResults.stream().filter(SurveyCalcResult::isAsFixed)
                    .map(SurveyCalcResult::getId).distinct().count();
            boolean stationStable = surveyCfgPoints.get(0).isStable();
            long needFixedNum = surveyCfgPoints.stream().filter(SurveyCfgPoint::isAsFixed).count() - limitParams.getCPointFailedNum();
            //若测量配置的固定点数小于3个（根据测站是否稳定，设置需要的固定点最小个数）
            if (needFixedNum < 3)
            {
                needFixedNum = stationStable ? 2 : 1;
            }
            isOk = fixedNum >= needFixedNum;
            if (isOk) {
                surveyInfo += "...合格!";
            } else {
                surveyInfo += "...实测固定点数量不足!---[至少要 "+needFixedNum+" 点,实测 "+fixedNum+" 点]";
            }
        } else {
            surveyInfo += "...有效计算数据不足!";
        }

        msg.setOk(isOk);
        msg.setMsg(surveyInfo);
    }

    /**
     * 测回内结果检验
     * @param surveyCalcResults 计算结果列表
     * @param limitParams 限差类
     * @param chIndex 测回数序号
     * @param msg 返回消息
     */
    private void checkSurveyResultCh(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, int chIndex, BackMessage msg) {
        //对应测回测量结果
        List<SurveyCalcResult> chResults = surveyCalcResults.stream().filter(it -> it.getChIndex() == chIndex).collect(Collectors.toList());
        //判空
        if (chResults.isEmpty()) {
            msg.setMsg("...第 " + (chIndex + 1) + " 测回无有效数据!");
            return;
        }

        //计算半测回归零差(先判断是否有归零值，即结果列表中第一个点与最后一个点是否是同一个点)
        int pos = chResults.size() - 1;
        boolean hasZero = chResults.get(0).getId() == chResults.get(pos).getId();

        //有归零差，先进行归零差检查(合格后再进行2C和i角检查;若不合格，返回)，否则，进行2C和i角检查
        BackMessage backMsg = new BackMessage();
        if (hasZero) {
            //标识归零点，后续测回间检查时删除
            chResults.get(pos).setAsZero(true);
            this.checkSurveyResultChZero(surveyCalcResults, limitParams, chIndex, backMsg);
            //归零差检查合格后，进行2C和i角检查
            if (backMsg.isOk()) {
                this.checkSurveyResultCh2Ci(surveyCalcResults, limitParams, chIndex, backMsg);
            }
        } else {
            checkSurveyResultCh2Ci(surveyCalcResults, limitParams, chIndex, backMsg);
        }

        if (backMsg.isOk()) {
            backMsg.setMsg("...第 " + (chIndex + 1) + " 测回--合格!");
        }

        msg.setOk(backMsg.isOk());
        msg.setMsg(backMsg.getMsg());
    }

    /// <summary>
    /// 半测回结果检验--归零差检查
    /// </summary>
    /// <param name="surveyCalcResults">测量计算成果列表(已经过滤掉测量不成功的数据)</param>
    /// <param name="limitParams">限差类</param>
    /// <param name="msg">返回消息</param>
    /// <returns></returns>
    private void checkSurveyResultHalfCh(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, BackMessage msg)
    {
        if(surveyCalcResults.isEmpty()){
            msg.setMsg("...测回内无有效数据!");
            return;
        }
        //计算半测回归零差(先判断是否有归零值，即结果列表中第一个点与最后一个点是否是同一个点)
        int pos = surveyCalcResults.size() - 1;
        boolean hasZero = surveyCalcResults.get(0).getId() == surveyCalcResults.get(pos).getId();

        //有归零差，先进行归零差检查(合格后再进行2C和i角检查;若不合格，返回)，否则，进行2C和i角检查
        BackMessage backMsg = new BackMessage();
        if (hasZero) {
            double zeroHa = surveyCalcResults.get(0).getHa() - surveyCalcResults.get(pos).getHa();
            double zeroVa = surveyCalcResults.get(0).getVa() - surveyCalcResults.get(pos).getVa();
            boolean isOkZeroHa = Math.abs(zeroHa) < limitParams.getDHa0();
            boolean isOkZeroVa = Math.abs(zeroVa) < limitParams.getDVa0();
            String strZeroHa = "盘左水平向归零差(″):" + String.format("%.1f", Angle.rad2Deg(zeroHa, true) * 3600);
            String strZeroVa = "盘左竖直角归零差(″):" + String.format("%.1f", Angle.rad2Deg(zeroVa, true) * 3600);

            boolean isOk = isOkZeroHa && isOkZeroVa;
            String surveyInfo = "";
            //归零差合格
            if (isOk) {
                surveyInfo = "...归零差合格";
                //把归零测量结果平均后赋值到第1点测量结果中,并删除最后一次测量结果
                surveyCalcResults.get(0).setHa((surveyCalcResults.get(0).getHa() + surveyCalcResults.get(pos).getHa()) / 2);
                surveyCalcResults.get(0).setVa((surveyCalcResults.get(0).getVa() + surveyCalcResults.get(pos).getVa()) / 2);
                surveyCalcResults.get(0).setSd((surveyCalcResults.get(0).getSd() + surveyCalcResults.get(pos).getSd()) / 2);
                surveyCalcResults.remove(pos);
            }
            //归零差不合格
            else {
                surveyInfo += "...归零差超限!\r\n";
                if (!isOkZeroHa) {
                    surveyInfo += "...[水平角归零差超限]--" + strZeroHa + "\r\n";
                }

                if (!isOkZeroVa) {
                    surveyInfo += "...[竖直角归零差超限]--" + strZeroVa + "\r\n";
                }
                //删除最后的回车换行符
                surveyInfo = surveyInfo.substring(0, surveyInfo.length() - 2);

                //删除所有计算数据
                surveyCalcResults.clear();
            }
            msg.setOk(isOk);
            msg.setMsg(surveyInfo);
        } else {
            msg.setOk(true);
            msg.setMsg("...归零差合格");
        }
    }

    /**
     * 测回内结果检验-归零差检查
     * @param surveyCalcResults surveyCalcResults
     * @param limitParams limitParams
     * @param chIndex chIndex
     * @param msg msg
     */
    private void checkSurveyResultChZero(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, int chIndex, BackMessage msg) {
        //对应测回测量结果
        List<SurveyCalcResult> chResults = surveyCalcResults.stream().filter(it -> it.getChIndex() == chIndex).collect(Collectors.toList());
        //判断归零差是否超限
        boolean isOkZeroHa = true;
        boolean isOkZeroVa = true;
        String strZeroHa = "";
        String strZeroVa = "";

        int pos = chResults.size() - 1;
        double zeroHa1 = chResults.get(0).getHa1() - chResults.get(pos).getHa1();
        double zeroHa2 = chResults.get(0).getHa2() - chResults.get(pos).getHa2();
        double zeroVa1 = chResults.get(0).getVa1() - chResults.get(pos).getVa1();
        double zeroVa2 = chResults.get(0).getVa2() - chResults.get(pos).getVa2();
        isOkZeroHa = Math.abs(zeroHa1) < limitParams.getDHa0() &&
                Math.abs(zeroHa2) < limitParams.getDHa0();
        isOkZeroVa = Math.abs(zeroVa1) < limitParams.getDVa0() &&
                Math.abs(zeroVa2) < limitParams.getDVa0();
        strZeroHa = "盘左水平向归零差(″):" + String.format("%.1f", Angle.rad2Deg(zeroHa1, true) * 3600) +
                ";盘右水平向归零差(″):" + String.format("%.2f", Angle.rad2Deg(zeroHa2, true) * 3600);
        strZeroVa = "盘左竖直角归零差(″):" + String.format("%.2f", Angle.rad2Deg(zeroVa1, true) * 3600) +
                ";盘右竖直角归零差(″):" + String.format("%.2f", Angle.rad2Deg(zeroVa2, true) * 3600);

        boolean isOk = isOkZeroHa && isOkZeroVa;
        String surveyInfo = "";
        //归零差不合格
        if (!isOk) {
            surveyInfo += "...第 " + (chIndex + 1) + " 测回成果超限!\r\n";
            if (!isOkZeroHa) {
                surveyInfo += "...[水平角归零差超限]--" + strZeroHa + "\r\n";
            }

            if (!isOkZeroVa) {
                surveyInfo += "...[竖直角归零差超限]--" + strZeroVa + "\r\n";
            }
            //删除最后的回车换行符
            surveyInfo = surveyInfo.substring(0, surveyInfo.length() - 2);

            //删除本测回所有计算数据
            List<SurveyCalcResult> filterList = surveyCalcResults.stream().filter(it -> it.getChIndex() != chIndex).collect(Collectors.toList());
            surveyCalcResults.clear();
            if (!filterList.isEmpty()){
                surveyCalcResults.addAll(filterList);
            }
        }

        msg.setOk(isOk);
        msg.setMsg(surveyInfo);
    }

    /**
     * 测回内结果检验-2C和i角
     * @param surveyCalcResults surveyCalcResults
     * @param limitParams limitParams
     * @param chIndex chIndex
     * @param msg msg
     */
    private void checkSurveyResultCh2Ci(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, int chIndex, BackMessage msg) {
        //对应测回测量结果
        List<SurveyCalcResult> chResults = surveyCalcResults.stream().filter(it -> it.getChIndex() == chIndex).collect(Collectors.toList());
        //判断2C,i角是否超限
        for (SurveyCalcResult result : chResults) {
            if (Math.abs(result.getD2C()) <= limitParams.getD2C()) {
                result.setCheckOk2C(true);
            }
            if (Math.abs(result.getDi()) <= limitParams.getDi()) {
                result.setCheckOki(true);
            }
            result.setCheckOkCh(result.isCheckOk2C() && result.isCheckOki());
        }

        //检查2c和i角情况
        boolean isOk = true;
        String surveyInfo = "";
        for (int i=0; i < chResults.size();i++) {
            SurveyCalcResult result = chResults.get(i);
            String tempInfo = "";
            //2c超限
            if (!result.isCheckOk2C()) {
                isOk = false;
                tempInfo += "[2C互差超限]--2C=" + String.format("%.1f", Angle.rad2Deg(result.getD2C(),true) * 3600) + "″";
            }

            //i角超限
            if (!result.isCheckOki()) {
                isOk = false;
                tempInfo += "[i角互差超限]--i=" + String.format("%.1f", Angle.rad2Deg(result.getDi(), true) * 3600) + "″";
            }

            //有超限情况
            if (!tempInfo.isEmpty()) {
                surveyInfo += "...测点 " + result.getPtName() + ":" + tempInfo;
                surveyInfo += i == chResults.size() - 1 ? "" : "\r\n";
            }
        }

        //是否有2C、i角超限情况
        if (!isOk) {
            surveyInfo = "...第 " + (chIndex + 1) + " 测回有超限情况:\r\n" + surveyInfo;
            //删除本测回2C或i角超限数据
            List<SurveyCalcResult> filterList = surveyCalcResults.stream().filter(it -> !(it.getChIndex() == chIndex && !it.isCheckOkCh())).collect(Collectors.toList());
            surveyCalcResults.clear();
            if (!filterList.isEmpty()){
                surveyCalcResults.addAll(filterList);
            }
        }

        msg.setOk(isOk);
        msg.setMsg(surveyInfo);
    }

    /**
     * 测回间结果检验
     * @param surveyCalcResults 计算结果列表
     * @param limitParams 限差类
     * @param msg 返回消息
     */
    private void checkSurveyResultAll(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, BackMessage msg) {
        //删除归零点
        List<SurveyCalcResult> filterList = surveyCalcResults.stream().filter(it -> !it.isAsZero()).collect(Collectors.toList());
        surveyCalcResults.clear();
        surveyCalcResults.addAll(filterList);

        //判空
        if (surveyCalcResults.isEmpty()) {
            msg.setMsg("...无有效计算数据!");
            return;
        }

        this.checkSurveyResultAllCalc(surveyCalcResults, limitParams);
        this.checkSurveyResultAllProcess(surveyCalcResults, limitParams, msg);
    }

    /**
     * 测回间结果检验-计算
     * @param surveyCalcResults surveyCalcResults
     * @param limitParams limitParams
     */
    private void checkSurveyResultAllCalc(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams) {
        //获取pIds列表(唯一)
        List<Long> pIds = surveyCalcResults.stream().map(SurveyCalcResult::getId).distinct().collect(Collectors.toList());

        //逐点检查测回内检查合格点的水平方向HaAve、竖直角VaAve、斜距SdAve互差是否超限
        for (long pId : pIds) {
            List<SurveyCalcResult> results = surveyCalcResults.stream().filter(it -> it.getId() == pId && it.isCheckOkCh()).collect(Collectors.toList());
            //无数据，下一点
            if (results.isEmpty()) {
                continue;
            }
            //只有1测回，互差为0,直接合格
            if (results.size() == 1) {
                SurveyCalcResult r = results.get(0);
                r.setCheckOkHaAve(true);
                r.setCheckOkVaAve(true);
                r.setCheckOkSdAve(true);
                r.setCheckOkAll(true);

                r.setCheckOkHaAveInfo("");
                r.setCheckOkVaAveInfo("");
                r.setCheckOkSdAveInfo("");
            } else {
                //遍历多个测回，计算测回间Ha、Va和Sd是否超限
                for (int i = 0; i < results.size() - 1; i++) {
                    String checkHaAveInfo = "";
                    String checkVaAveInfo = "";
                    String checkSdAveInfo = "";
                    SurveyCalcResult r1 = results.get(i);
                    boolean isOkHa = true;
                    boolean isOkVa = true;
                    boolean isOkSd = true;
                    for (int j = 1; j < results.size(); j++) {
                        SurveyCalcResult r2 = results.get(j);
                        double dHaAve = r2.getHaAve() - r1.getHaAve();
                        double dVaAve = r2.getVaAve() - r1.getVaAve();
                        double dSdAve = r2.getSdAve() - r1.getSdAve();
                        if (Math.abs(dHaAve) > limitParams.getDHa()) {
                            checkHaAveInfo += "第 " + (r1.getChIndex() + 1) + " 测回 与第 " + (r2.getChIndex() + 1) + " 测回 互差:" +
                                    String.format("%.1f" ,Angle.rad2Deg(dHaAve, true) * 3600) + "″";
                            isOkHa = false;
                        }

                        if (Math.abs(dVaAve) > limitParams.getDVa()) {
                            checkVaAveInfo += "第 " + (r1.getChIndex() + 1) + " 测回 与 第 " + (r2.getChIndex() + 1) + " 测回 互差:" +
                                    String.format("%.1f" ,Angle.rad2Deg(dVaAve,true) * 3600) + "″";
                            isOkVa = false;
                        }

                        if (Math.abs(dSdAve) > limitParams.getDSd()) {
                            checkSdAveInfo += "第" + (r1.getChIndex() + 1) + "测回 与 第" + (r2.getChIndex() + 1) + "测回 互差:" +
                                    String.format("%.1f", (dSdAve * 1000)) + "mm;";
                            isOkSd = false;
                        }

                        r2.setCheckOkHaAve(isOkHa);
                        r2.setCheckOkVaAve(isOkVa);
                        r2.setCheckOkSdAve(isOkSd);
                        r2.setCheckOkAll(isOkHa && isOkVa && isOkSd);

                        r2.setCheckOkHaAveInfo(checkHaAveInfo);
                        r2.setCheckOkVaAveInfo(checkVaAveInfo);
                        r2.setCheckOkSdAveInfo(checkSdAveInfo);
                    }

                    r1.setCheckOkHaAve(isOkHa);
                    r1.setCheckOkVaAve(isOkVa);
                    r1.setCheckOkSdAve(isOkSd);
                    r1.setCheckOkAll(isOkHa && isOkVa && isOkSd);

                    r1.setCheckOkHaAveInfo(checkHaAveInfo);
                    r1.setCheckOkVaAveInfo(checkVaAveInfo);
                    r1.setCheckOkSdAveInfo(checkSdAveInfo);
                }
            }
        }
    }

    /**
     * 测回间结果检验--处理
     * @param surveyCalcResults 计算结果列表
     * @param limitParams 限差类
     * @param msg 返回消息
     */
    private void checkSurveyResultAllProcess(List<SurveyCalcResult> surveyCalcResults, LimitParams limitParams, BackMessage msg) {
        //处理
        List<SurveyCalcResult> okResults = surveyCalcResults.stream().filter(SurveyCalcResult::isCheckOkAll).collect(Collectors.toList());
        List<SurveyCalcResult> falseDHaResults = surveyCalcResults.stream().filter(it -> !it.isCheckOkHaAve()).collect(Collectors.toList());
        List<SurveyCalcResult> falseDVaResults = surveyCalcResults.stream().filter(it -> !it.isCheckOkVaAve()).collect(Collectors.toList());
        List<SurveyCalcResult> falseDSdResults = surveyCalcResults.stream().filter(it -> !it.isCheckOkSdAve()).collect(Collectors.toList());

        boolean isOk = okResults.size() == surveyCalcResults.size();
        boolean isOkDHa = falseDHaResults.isEmpty();
        boolean isOkDVa = falseDVaResults.isEmpty();
        boolean isOkDSd = falseDSdResults.isEmpty();

        String surveyInfo = "";
        if (isOk) {
            surveyInfo += "...合格!";
        } else {
            if (!isOkDHa) {
                String tempInfo = "";
                for (SurveyCalcResult result : falseDHaResults) {
                    tempInfo += result.getCheckOkHaAveInfo() + "\r\n";
                }

                surveyInfo += "...[水平向互差超限]\r\n" + tempInfo;
            }

            if (!isOkDVa) {
                String tempInfo = "";
                for (SurveyCalcResult result : falseDVaResults) {
                    tempInfo += result.getCheckOkVaAveInfo() + "\r\n";
                }

                surveyInfo += "...[竖直角互差超限]\r\n" + tempInfo;
            }

            if (!isOkDSd) {
                String tempInfo = "";
                for (SurveyCalcResult result : falseDSdResults) {
                    tempInfo += result.getCheckOkSdAveInfo() + "\r\n";
                }

                surveyInfo += "...[斜距互差超限]\r\n" + tempInfo;
            }

            // 删除最后的回车换行符
            surveyInfo = surveyInfo.substring(0, surveyInfo.length() - 2);
            //最终只选择所有检查合格的测量成果参与平差计算(即删除不合格的成果)
            List<SurveyCalcResult> filterList = surveyCalcResults.stream().filter(SurveyCalcResult::isCheckOkAll).collect(Collectors.toList());
            surveyCalcResults.clear();
            surveyCalcResults.addAll(filterList);
        }

        msg.setOk(isOk);
        msg.setMsg(surveyInfo);
    }

    /**
     * 从当前计算成果中获得本期测量点列表
     * @param calcResults calcResults
     */
    private List<SurveyPoint> getPtsFromCalcResults(List<SurveyCalcResult> calcResults) {
        List<SurveyPoint> surveyPts = new ArrayList<>();
        List<Long> pIds = calcResults.stream().map(SurveyCalcResult::getId).distinct().collect(Collectors.toList());
        for (long pId : pIds) {
            Optional<SurveyCalcResult> optional = calcResults.stream().filter(it -> it.getId() == pId).findAny();
            if (!optional.isPresent()){
                continue;
            }
            SurveyCalcResult result = optional.get();
            SurveyPoint surveyPt = new SurveyPoint();
            surveyPt.setId(pId);
            surveyPt.setName(result.getPtName());
            surveyPt.setAsFixed(result.isAsFixed());
            surveyPt.setHa(result.getHa());
            surveyPt.setVa(result.getVa());
            surveyPt.setSd(result.getSd());
            surveyPt.setX(result.getX());
            surveyPt.setY(result.getY());
            surveyPt.setZ(result.getZ());
            surveyPts.add(surveyPt);
        }

        return surveyPts;
    }

    /**
     * 获取测量过程结果字符串(周期数|测量原始数据|测量计算数据)
     * @param missionId missionId
     * @param recycleNum recycleNum
     * @param surveyResults surveyResults
     * @param surveyCalcResults surveyCalcResults
     */
    private String getSurveyData(Long missionId, int recycleNum, List<SurveyResult> surveyResults, List<SurveyCalcResult> surveyCalcResults) {
        //从surveyResults中获取测量原始数据字符串
        String rawDatas = getSurveyRawDatas(surveyResults);

        //从surveyCalcResults中获得计算数据
        StringBuilder calcDatas = new StringBuilder("点名,水平角;盘左,盘右,2C;均值;测回均值;");
        for (SurveyCalcResult r : surveyCalcResults) {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getHa1()).append(",")
                    .append(r.getHa2()).append(",")
                    .append(r.getD2C()).append(",")
                    .append(r.getHaAve()).append(",")
                    .append(r.getHa()).append(";");
        }

        calcDatas.append("点名,竖直角;盘左,盘右,i值,均值,测回均值;");
        for (SurveyCalcResult r : surveyCalcResults) {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getVa1()).append(",")
                    .append(r.getVa2()).append(",")
                    .append(r.getDi()).append(",")
                    .append(r.getVaAve()).append(",")
                    .append(r.getVa()).append(";");
        }

        calcDatas.append("点名,斜距;盘左,盘右,均值,测回均值;");
        for (SurveyCalcResult r : surveyCalcResults) {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getSd1()).append(",")
                    .append(r.getSd2()).append(",")
                    .append(r.getSdAve()).append(",")
                    .append(r.getSd()).append(";");
        }

        calcDatas.append("end;");
        calcDatas.append("MeasureTime:").append(DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN));

        SurveyCfgPoint st = surveyCfgPoints.get(0);
        return missionId + "|" + st.getName() + "|" + recycleNum + "|" + rawDatas + "|" + calcDatas + "|" + adjReport;
    }

    /// <summary>
    /// 获取测量过程结果字符串(周期数|测量原始数据|测量计算数据)--0.5测回
    /// </summary>
    /// <param name="monitorItemId">监测任务Id</param>
    /// <param name="recycleNum">周期数</param>
    /// <param name="surveyResults">测量原始值列表</param>
    /// <param name="surveyCalcResults">测量计算值列表</param>
    /// <returns></returns>
    private String getSurveyDataHalfCh(Long monitorItemId, int recycleNum, List<SurveyResult> surveyResults,
                                       List<SurveyCalcResult> surveyCalcResults)
    {
        //从surveyResults中获取测量原始数据字符串
        String rawDatas = getSurveyRawDatas(surveyResults);

        //从surveyCalcResults中获得计算数据
        StringBuilder calcDatas = new StringBuilder("点名,水平角盘左,盘右,2C;均值;测回均值;");
        for (SurveyCalcResult r : surveyCalcResults)
        {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getHa1()).append(",null,null,")
                    .append(r.getHa()).append(",")
                    .append(r.getHa()).append(";");
        }

        calcDatas.append("点名,竖直角盘左,盘右,i值,均值,测回均值;");
        for (SurveyCalcResult r : surveyCalcResults)
        {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getVa1()).append(",null,null,")
                    .append(r.getVa()).append(",")
                    .append(r.getVa()).append(";");
        }

        calcDatas.append("点名,斜距;盘左,盘右,均值,测回均值；");
        for (SurveyCalcResult r : surveyCalcResults)
        {
            calcDatas.append(r.getPtName()).append(",")
                    .append(r.getSd1()).append(",null,")
                    .append(r.getSd()).append(",")
                    .append(r.getSd()).append(";");
        }

        calcDatas.append("end;");
        calcDatas.append("MeasureTime:").append(DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN));

        SurveyCfgPoint st = surveyCfgPoints.get(0);
        return monitorItemId + "|" + st.getName() + "|" + recycleNum + "|" + rawDatas + "|" + calcDatas + "|" + adjReport;
    }

    /**
     * 获取测量原始数据字符串
     * @param surveyResults surveyResults
     * @return 获取测量原始数据字符串
     */
    private String getSurveyRawDatas(List<SurveyResult> surveyResults) {
        //从surveyResults中获得原始测量数据
        StringBuilder rawDatas = new StringBuilder("Start;");
        SurveyCfgPoint st = surveyCfgPoints.get(0);

        rawDatas.append("Station,")
                .append(st.getName())
                .append(",")
                .append(st.getX())
                .append(",")
                .append(st.getY())
                .append(",")
                .append(st.getZ())
                .append(",")
                .append(st.getHi())
                .append(";");
        for (SurveyResult r : surveyResults){
            rawDatas.append("Measure,")
                    .append(r.getPtName()).append(",")
                    .append(r.getX()).append(",")
                    .append(r.getY()).append(",")
                    .append(r.getZ()).append(",")
                    .append(r.getHa()).append(",")
                    .append(r.getVa()).append(",")
                    .append(r.getSd()).append(",")
                    .append(r.getChIndex() + 1).append(",")
                    .append(r.isFace1() ? 1 : 2).append(",")
                    .append(r.isSuccess() ? 0 : 99).append(",")
                    .append(DateUtil.dateToDateString(r.getGetTime(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN)).append(",")
                    .append(r.getHt())
                    .append(";");
        }

        rawDatas.append("End;");
        rawDatas.append("MeasureTime:").append(DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN));
        return rawDatas.toString();
    }
    //endregion 测量数据后处理

}
