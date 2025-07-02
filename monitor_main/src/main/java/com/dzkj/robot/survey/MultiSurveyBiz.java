package com.dzkj.robot.survey;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.bean.Point3d;
import com.dzkj.bean.SurveyLine;
import com.dzkj.bean.SurveyStation;
import com.dzkj.biz.SurveyResultProcessNewClassicUpdate;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.data.vo.PointDataXyzhDto;
import com.dzkj.common.CommonUtil;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmInfoCorrect;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataXyzhCorrect;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.data.PushTaskOther;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.entity.survey.SurveyCycle;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.bean.MultiOperateCompleteResult;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.job.*;
import com.dzkj.robot.job.common.MonitorJobUtil;
import com.dzkj.robot.multi.MultiStationAo;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskOtherService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.survey.IRobotSurveyControlGroupService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import com.dzkj.service.survey.ISurveyCycleService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/28 17:26
 * @description 多站联测业务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Getter
@Setter
@Slf4j
public class MultiSurveyBiz {

    private boolean isSurveyAtOnce;

    private List<String> finalResults = new ArrayList<>();

    private String surveyData;

    private List<String> surveyBackInfos = new ArrayList<>();

    private MultiStationAo currentStation;

    private List<ControlBoxAo> controlBoxList = new ArrayList<>();

    private boolean finalOk;

    /**
     * 当前测量周期
     */
    private String currentSurveyCycleCfg;
    /**
     * 测量时间信息--用于测量期间状态信息显示
     */
    private String surveyDateTimeInfo;
    /**
     * 测量周期
     */
    private int recycleNum;

    /**
     * 当前整网重测数
     */
    private int currentRepeatNum;

    /**
     * 最大整网重测数
     */
    private int maxRepeatNum;

    /**
     * 其他需要返回显示的信息
     */
    private List<String> backInfos= new ArrayList<>();

    /**
     * 测量回调
     **/
    private Consumer<MultiOperateCompleteResult> surveyOperateCompleted;

    private IPointDataXyzhBiz dataXyzhBiz;
    private ISurveyCycleService surveyCycleService;
    private IRobotSurveyDataService surveyDataService;
    private RobotSurveyJobService surveyJobService;
    private IControlBoxService controlBoxService;
    private IRobotSurveyControlGroupService surveyControlGroupService;
    private IPushTaskService pushTaskService;
    private IRobotSurveyRecordService robotSurveyRecordService;
    private IPushTaskOtherService pushTaskOtherService;
    private IPointDataXyzhService dataXyzhService;
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    private IAlarmInfoService infoService;
    private IAlarmInfoCorrectService infoCorrectService;
    private QwMsgService qwMsgService;

    public MultiSurveyBiz(
            MultiStationAo multiStationAo,
            IPointDataXyzhBiz dataXyzhBiz,
            ISurveyCycleService surveyCycleService,
            IRobotSurveyDataService surveyDataService,
            RobotSurveyJobService surveyJobService,
            IControlBoxService controlBoxService,
            IPushTaskService pushTaskService,
            IRobotSurveyRecordService robotSurveyRecordService,
            IRobotSurveyControlGroupService surveyControlGroupService,
            IPushTaskOtherService pushTaskOtherService,
            IPointDataXyzhService dataXyzhService,
            IPointDataXyzhCorrectService dataXyzhCorrectService,
            IAlarmInfoService infoService,
            IAlarmInfoCorrectService infoCorrectService,
            QwMsgService qwMsgService
    ){
        this.currentStation = multiStationAo;
        this.dataXyzhBiz = dataXyzhBiz;
        this.surveyCycleService = surveyCycleService;
        this.surveyDataService = surveyDataService;
        this.surveyJobService = surveyJobService;
        this.controlBoxService = controlBoxService;
        this.pushTaskService = pushTaskService;
        this.robotSurveyRecordService = robotSurveyRecordService;
        this.surveyControlGroupService = surveyControlGroupService;
        this.pushTaskOtherService = pushTaskOtherService;
        this.dataXyzhService = dataXyzhService;
        this.dataXyzhCorrectService = dataXyzhCorrectService;
        this.infoService = infoService;
        this.infoCorrectService = infoCorrectService;
        this.qwMsgService = qwMsgService;
        init();
    }
    //region 公共方法
    /**
     * 开启测量
     */
    public void start(Date startTime){
        //清空测量结果
        this.surveyBackInfos.clear();

        //恢复测量状态
        currentStation.setSurveyStatus("待测");
        for (ControlBoxAo boxAo : controlBoxList) {
            boxAo.setSurveyStatus("待测");
        }
        //设置多站测量状态: 停机时要更新
        surveyControlGroupService.updateSurvey(currentStation.getId(), 1);
        //配置了测量策略，启动系统监测
        if (StringUtils.isNotEmpty(currentSurveyCycleCfg)) {
            //立即启动监测，后续交给系统计时器事件
            this.monitor(startTime);
        }
        // 激发开测事件
        onActionCompleted(new MultiOperateCompleteResult(false, null, null), isSurveyAtOnce);
    }

    /**
     * 临时加测
     */
    public void startAtOnce(){
        isSurveyAtOnce = true;
        //开始测量
        startSurvey(false);
    }

    /**
     * 开始执行定时任务
     */
    public void startMultiTask(){
        //仪器操作业务类尚未初始化，或在测(测量超时或临时加测情况)
        List<SurveyBiz> currentSurveyBizList = new ArrayList<>();
        for (ControlBoxAo controlBox : controlBoxList) {
            if (controlBox.getSurveyBiz() != null) {
                currentSurveyBizList.add(controlBox.getSurveyBiz());
            }
        }
        boolean canSurvey = !currentSurveyBizList.isEmpty();
        for (SurveyBiz surveyBiz : currentSurveyBizList) {
            if (surveyBiz.getDeviceBiz() == null) {
                canSurvey = false;
                break;
            }
        }
        if (!canSurvey || "在测".equals(currentStation.getSurveyStatus())) {
            String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 错过计划测量时间点!";
            surveyBackInfos.add(surveyInfo);
            for (SurveyBiz surveyBiz : currentSurveyBizList) {
                log.info("{} {} -- {}", surveyInfo, surveyBiz.getDeviceBiz() ,currentStation.getSurveyStatus());
            }
        } else {
            //开始测量
            startSurvey(false);
        }
    }

    /**
     * 获取测量过程信息
     */
    public String getSurveyStatusInfo() {
        //返回的状态信息
        String statusInfo = "";
        //尚未配置参数，返回
        if (StringUtils.isEmpty(currentStation.getParams()))
        {
            statusInfo = "--,--,下次测量时间:--";
            return statusInfo;
        }

        switch (currentStation.getSurveyStatus()) {
            //处于在测状态
            case "在测":
                statusInfo += isSurveyAtOnce ? "临时加测," : "计划测量,";
                statusInfo += recycleNum + ",";
                statusInfo += StringUtils.isEmpty(currentSurveyCycleCfg) ? "下次测量时间:--" : surveyDateTimeInfo;
                break;
            case "待测":
                //没有测量策略计划
                if (StringUtils.isEmpty(currentSurveyCycleCfg)) {
                    statusInfo += "无测量周期策略,";
                    statusInfo += "--,";
                    statusInfo += "下次测量时间:--";
                } else {
                    statusInfo += "计划测量,";
                    statusInfo += "--,";
                    statusInfo += surveyDateTimeInfo;
                }
                break;
            case "停测":
                statusInfo += "--,";
                statusInfo += "--,";
                statusInfo += "下次测量时间:--";
                break;
            default:
        }
        return statusInfo;
    }

    /**
     * 停测
     */
    public void stop(){
        finalOk = false;
        isSurveyAtOnce = false;
        currentStation.setSurveyStatus("停测");
        // 记录状态
        surveyControlGroupService.updateSurvey(currentStation.getId(), 0);
        //关联测站停测
        for (ControlBoxAo controlBox : controlBoxList) {
            if (controlBox.getSurveyBiz() == null || controlBox.getSurveyBiz().getDeviceBiz() == null){
                controlBox.setSurveyStatus("停测");
                // 2023/6/26 记录状态
                controlBoxService.updateSurvey(controlBox.getId(), 0);
                log.info("控制器已经下线或不存在");
                return ;
            }
            controlBox.getSurveyBiz().stop();
        }
        //region 2024-142-31 定时任务停止
        String serialNos = controlBoxList.stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
        //删除上一次的定时任务
        surveyJobService.removeJob(MonitorJobConst.JOB_PREFIX + currentStation.getId()
                ,MonitorJobConst.TRIGGER_PREFIX + currentStation.getId());
        surveyJobService.removeJob(MonitorJobConst.PUSh_JOB_PREFIX + serialNos,
                MonitorJobConst.PUSh_TRIGGER_PREFIX + serialNos);
        //endregion 定时任务停止
        surveyBackInfos.add("[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] >>> 停止测量 <<<");
        // 激发停测事件
        onActionCompleted(new MultiOperateCompleteResult(true, CommonUtil.SYS_GRC_CANCEL, "stop"), false);
    }

    /**
     * 配置修改后更新
     */
    public void updateInit(){
        if (StringUtils.isEmpty(currentStation.getParams())) {
            return;
        }
        //初始化测量周期策略信息
        initSurveyParams();
    }
    //endregion

    //region 测量完成事件
    /**
     * 测量过程完成事件处理
     * @param result result
     */
    private void onActionCompleted(MultiOperateCompleteResult result, boolean atOnce) {
        //发送结果到页面
        try {
            SocketMsgConst msgConst = result.isActionComplete() ? SocketMsgConst.MULTI_CONTROL_DATA_SUB : SocketMsgConst.MULTI_CONTROL_DATA;
            WebSocketServer.sendInfo(new MessageVO(
                    msgConst.getCode(),
                    (atOnce ? "surveyAtOnce" : "") + msgConst.getMessage() + "_" + currentStation.getId(),
                    result
            ));
        } catch (IOException e) {
            log.info("socket 信息发送失败:{}", e.getMessage());
        }
    }
    //endregion

    //region 私有方法
    /**
     * 开始测量
     */
    private void startSurvey(boolean isRepeat){
        //开始测量，初始化参数
        if (!isRepeat) {
            currentRepeatNum = 0;
        }

        //消息列表
        surveyBackInfos.clear();
        backInfos.clear();
        finalOk = false;
        //获取测量周期
        List<RobotSurveyData> surveyDataList = surveyDataService.getByMissionId(currentStation.getMissionId());
        recycleNum = !surveyDataList.isEmpty() ? surveyDataList.get(0).getRecycleNum() + 1 : 1;

        currentStation.setSurveyStatus("在测");
        //调用 surveyBiz 执行测量任务
        for (ControlBoxAo controlBox : controlBoxList) {
            if (controlBox.getSurveyBiz() != null && controlBox.getSurveyBiz().getDeviceBiz() != null) {
                if (isSurveyAtOnce) {
                    controlBox.getSurveyBiz().setSurveyAtOnce(true);
                }
                //多站开测时直接设置单站状态为在测
                controlBox.setSurveyStatus("在测");
                controlBox.getSurveyBiz().startAtOnce();
            }
        }

        onActionCompleted(new MultiOperateCompleteResult(false, null, null), isSurveyAtOnce);
    }

    /**
     * 验证是否启动测量定时任务
     * @param startTime 当前开始时间
     */
    private void monitor(Date startTime){
        //判定是否启动一次测量，并获取下次定时执行时间
        MultiJobParam jobParam = checkStart(startTime, currentSurveyCycleCfg);
        if(jobParam == null){
            return;
        }

        //清除任务防止异常任务重复
        surveyJobService.removeJob(MonitorJobConst.JOB_PREFIX + currentStation.getId()
                ,MonitorJobConst.TRIGGER_PREFIX + currentStation.getId());
        //启动定时任务
        surveyJobService.addMultiJob(MonitorJobConst.JOB_PREFIX + currentStation.getId()
                ,MonitorJobConst.TRIGGER_PREFIX + currentStation.getId()
                , MultiRobotSurveyJob.class, jobParam);
    }

    /**
     * 判定是否启动测量
     * @param dtNow 当前时间
     * @param surveyCycleCfg 测量周期策略中的时间点配置字符串
     */
    private MultiJobParam checkStart(Date dtNow, String surveyCycleCfg) {
        String[] cfgInfos = surveyCycleCfg.split(",");
        Date cfgDateStart = DateUtil.getDate(cfgInfos[0], DateUtil.yyyy_MM_dd_EN);
        Date cfgDateEnd = DateUtil.getDate(cfgInfos[1], DateUtil.yyyy_MM_dd_EN);
        int cfgDayInterval = Integer.parseInt(cfgInfos[2]);
        List<String> cfgTimes = Arrays.asList(cfgInfos).subList(3, cfgInfos.length);
        String dateNowStr = DateUtil.dateToDateString(dtNow, DateUtil.yyyy_MM_dd_EN);
        String timeNowStr = DateUtil.dateToDateString(dtNow, DateUtil.HH_mm_ss_EN);
        Date dtNowFormat = DateUtil.getDate(dateNowStr, DateUtil.yyyy_MM_dd_EN);
        assert dtNowFormat != null;
        assert cfgDateEnd != null;
        assert cfgDateStart != null;
        if (dtNowFormat.getTime() > cfgDateEnd.getTime()){
            this.surveyDateTimeInfo = "下次测量时间： 计划周期策略已经结束";
            return null;
        }
        //判断当前日期是否在策略计划中
        int dayNum = checkJobIntervalDay(cfgDayInterval, cfgDateStart, dtNow);
        if (dtNowFormat.getTime() >= cfgDateStart.getTime() && dayNum == 0){
            //当前时间刚好是测量时间点，马上发起测量
            if (cfgTimes.contains(timeNowStr)){
                this.startMultiTask();
            }
        }
        //获取下一次可执行时间
        Date firstTime = getFirstJobDate(cfgDateStart, cfgTimes, dayNum, cfgDayInterval);
        if(firstTime == null || firstTime.getTime() >= cfgDateEnd.getTime() + 24 * 3600 * 1000){
            return null;
        }
        //设置下次的执行时间
        this.surveyDateTimeInfo = "下次测量时间： " +  DateUtil.dateToDateString(firstTime, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        //构建定时任务执行参数,并启动定时任务
        MultiJobParam jobParam = new MultiJobParam();
        jobParam.setMultiStationId(currentStation.getId());
        jobParam.setMissionId(currentStation.getMissionId());
        jobParam.setParams(currentStation.getParams());
        String serialNos = controlBoxList.stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
        jobParam.setCycleInfo(surveyCycleCfg);
        jobParam.setSerialNo(serialNos);
        jobParam.setStartDate(cfgDateStart);
        jobParam.setEndDate(new Date(cfgDateEnd.getTime() + 24 * 3600 * 1000));
        jobParam.setFirstTime(firstTime);
        jobParam.setFirstTimeCorn(MonitorJobUtil.getCronString(firstTime));
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
     * @param cfgDateStart   任务开始时间
     * @param cfgTimes       配置时间点
     * @param dayNum         当前时间和第一次执行时间间隔天数
     * @param dayInterval   周期天数
     * @return 第一次执行时间
     */
    private Date getFirstJobDate(Date cfgDateStart, List<String> cfgTimes, int dayNum, int dayInterval) {
        //记录当前时间
        Date currentDate = new Date();
        //当前时间在开始时间之前，则取配置的开始日期的第一个时间点执行
        String dtStartStr = DateUtil.dateToDateString(cfgDateStart, DateUtil.yyyy_MM_dd_EN);
        if (cfgDateStart.getTime() >= currentDate.getTime()){
            return DateUtil.getDate(dtStartStr + " " + cfgTimes.get(0), DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        }

        LocalTime currentTime = LocalTime.parse(DateUtil.dateToDateString(currentDate, DateUtil.HH_mm_ss_EN)
                , DateTimeFormatter.ofPattern("HH:mm:ss"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Date nextDate = null;
        //dayNum == 0，取当天有效时间点，若是无取下一个周期第一个时间点
        if (dayNum == 0){
            LocalTime selectTime;
            //循环比较配置策略时间点，选择一个可执行时间值
            for (String cfgTime : cfgTimes) {
                selectTime = LocalTime.parse(cfgTime, DateTimeFormatter.ofPattern(DateUtil.HH_mm_ss_EN));
                if (selectTime.isAfter(currentTime)) {
                    //获取下一个时间周期的第一个时间
                    String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " " + cfgTime;
                    nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
                    break;
                }
            }
            //当前没有获取到下一次时间值 表示当天时间点都已经无效
            if(nextDate == null) {
                //获取下一个时间周期的第一个时间
                nextDate = getNextPeriodFirstDate(calendar, dayInterval, cfgTimes.get(0));
            }
        }else {
            //获取下一个时间周期的第一个时间
            nextDate = getNextPeriodFirstDate(calendar, dayNum, cfgTimes.get(0));
        }
        return nextDate;
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
     * 数据初始化
     */
    private void init(){
        //仪器测量回调
        this.surveyOperateCompleted = this::handleSurveyOperateResult;
        if (StringUtils.isEmpty(currentStation.getParams())) {
            return;
        }
        //初始化测量参数
        initSurveyParams();
    }

    /**
     * 初始化测量参数
     */
    private void initSurveyParams(){
        String[] msParams = currentStation.getParams().split("\\|");
        //比对最大整网重测数和当前重测数，确定是否需要重测
        maxRepeatNum = Integer.parseInt(msParams[0]);
        //初始化测量周期策略信息
        SurveyCycle surveyCycle = surveyCycleService.getById(Integer.parseInt(msParams[2]));
        if (surveyCycle == null) {
            currentSurveyCycleCfg = "";
        } else {
            currentSurveyCycleCfg = surveyCycle.getParams();
        }
    }

    /**
     * 测量过程结束回调
     * @param result result
     */
    private void handleSurveyOperateResult(MultiOperateCompleteResult result){
        boolean atOnce = isSurveyAtOnce;
        /*
         * 测站中每个步骤结束需要更新多站测量过程信息 并通知更新前端
         * 每个单站测量完成验证是否全部
         */
        //获取测量过程信息
        surveyBizSubActionCompleted();
        if (result.isActionComplete()) {
            //掉线后重新上线，并超过了设置时间，结束本次测量
            if ("OFFLINE_RECONNECT_TIMEOUT".equals(result.getCommandName())
                    && "在测".equals(currentStation.getSurveyStatus())) {
                currentStation.setSurveyStatus("待测");
            } else {
                //单测站测量完成处理
                surveyBizActionCompleted(result);
            }
        }
        //发送界面通知
        this.onActionCompleted(result, atOnce);
    }

    /**
     * 单站中单个步骤完成处理
     */
    private void surveyBizSubActionCompleted(){
        //清空上次的结果
        surveyBackInfos.clear();
        if("停测".equals(currentStation.getSurveyStatus())){
            surveyBackInfos.add("[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] 停测!");
            return;
        }
        String headInfo = isSurveyAtOnce ? ">>> 临时加测 <<<" : ">>> 计划测量 <<<";
        surveyBackInfos.add(headInfo);
        surveyBackInfos.add("[刷新时间:" + DateUtil.dateToDateString(new Date(), DateUtil.HH_mm_ss_EN) + "]");
        surveyBackInfos.add("----------------");

        for (ControlBoxAo box : controlBoxList) {
            SurveyBiz surveyBiz = box.getSurveyBiz();
            String stName = box.getStationInfo().split(",")[0];
            String surveyStatus = box.getSurveyStatus();
            surveyBackInfos.add("***  测站:" + stName + "  ***");
            if ("在测".equals(surveyStatus)) {
                String[] statusInfos = surveyBiz.getSurveyStatusInfo().split(",");
                surveyStatus += statusInfos[0].length() > 4 ? "--预测量" : "--正式测量";
                surveyBackInfos.add("测量状态:" + surveyStatus);
                String progressInfo = "测回 " + statusInfos[5] + "/" + statusInfos[6] +
                        " 测点 " + statusInfos[3] + "/" + statusInfos[4];
                surveyBackInfos.add("测量进度:" + progressInfo);
                surveyBackInfos.add("整站重测:" + statusInfos[8]);
                surveyBackInfos.add("整网重测:[" + currentRepeatNum+ "/" + maxRepeatNum + "]");
            } else {
                surveyBackInfos.add("测量状态:" + surveyStatus);
                surveyBackInfos.add("测量进度:--");
                surveyBackInfos.add("整站重测:--");
                surveyBackInfos.add("整网重测:[" + currentRepeatNum + "/" +maxRepeatNum + "]");
            }

            if (!box.getId().equals(controlBoxList.get(controlBoxList.size() - 1).getId())) {
                surveyBackInfos.add("");
            }
        }

        //显示测量中其他需要显示的信息
        if (backInfos.isEmpty()) {
            return;
        }
        surveyBackInfos.add("********");
        surveyBackInfos.addAll(backInfos);
    }
    /**
     * 单站整体测量完成处理
     */
    private void surveyBizActionCompleted(MultiOperateCompleteResult result){
        //当前多测站状态：停测，改变UI状态，不再进行后续数据处理
        if ("停测".equals(currentStation.getSurveyStatus())) {
            return;
        }

        //非正常结束测量，不做检查
        if (result.getRtCode() == null) {
            return;
        }
        checkSurveyStatus(result);
    }

    /**
     * 检查测量状态--每当测站编组中有一测站测量结束，引发该方法
     */
    private void checkSurveyStatus(MultiOperateCompleteResult result){
        //编组中有1站测量失败,则终止所有“在测”测站，暂停指定秒后,整网重测(次数由设置的重测数确定)
        String surveyInfo = "[" + DateUtil.dateToDateString(new Date(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + "] ";
        boolean needRepeatSurvey = false;
        for (ControlBoxAo boxAo : controlBoxList) {
            if (boxAo.getSurveyBiz() != null && !needRepeatSurvey) {
                needRepeatSurvey = boxAo.getSurveyBiz().isSurveyFinished() && !boxAo.getSurveyBiz().isFinalOk();
            }
        }
        //需要整网重测
        if (needRepeatSurvey) {
            boolean needRepeat = cancelAndRepeatSurvey(5);
            String strInfo = needRepeat ? "整网重测--[" + currentRepeatNum + "/" + maxRepeatNum + "]" : "重测失败--达到最大重测次数!";
            surveyInfo += strInfo;
            currentStation.setSurveyStatus(needRepeat ? currentStation.getSurveyStatus() : "待测");
            backInfos.add(surveyInfo);

            //获取测量信息
            surveyBizSubActionCompleted();
            return;
        }

        //编组中所有测站必须完成测量，否则直接返回
        long surveyingBoxNum = controlBoxList.stream().filter(item -> "在测".equals(item.getSurveyStatus())).count();
        if (surveyingBoxNum > 0){
            return;
        }

        //只处理“在测”状态多测站，防止多次平差
        if(!"在测".equals(currentStation.getSurveyStatus())) {
            return;
        }

        //平差计算
        finalOk = adjustCalc();
        //获取测量信息
        backInfos.add(finalOk ? "数据后处理成功!" : "数据后处理失败!");
        Date getTime = new Date();
        if (finalOk) {
            PointDataXyzhDto dataDto = dataXyzhBiz.saveRobotResultOnSuccess(finalResults, surveyData, true, isSurveyAtOnce(), false, new ArrayList<>(), -1);
            List<PointDataXyzh> dataXyzhs = dataDto.getDataList();
            PointDataXyzh dataXyzh = !dataXyzhs.isEmpty() ? dataXyzhs.get(0) : null;
            if (!dataXyzhs.isEmpty()) {
                getTime = dataXyzh.getGetTime();
            }
            // 2023/6/9 新增推送任务，和铁路局对接
            // 2024/3/15  多站推送任务
            Date uploadTime = null;
            if (!isSurveyAtOnce){
                createPushTaskJob(surveyData, getTime);
            }
            // region 2024/11/26 测量成功发送结果到微信
            qwMsgService.sendSurveyResultMsg(dataXyzhs, currentStation.getMissionId(), -1);
            // endregion 2024/11/26 测量成功发送结果到微信
            finalOk = false;
            if (isSurveyAtOnce){
                //临时加测时需要页面回显测量结果,
                try {
                    result.setRtCode(CommonUtil.SYS_GRC_OK);
                    WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.MULTI_CONTROL_DATA.getCode(),
                            "surveyAtOnce" + SocketMsgConst.MULTI_CONTROL_DATA.getMessage() + "_" + currentStation.getId(), result));
                } catch (IOException e) {
                    log.info("checkSurveyStatus socket发送失败: {}", e.getMessage());
                }
            }
            // region 2024/11/20 数据同步推送任务
            pushToCorrectDb(dataXyzh);
            // endregion 2024/11/20 数据同步推送任务
        } else {
            dataXyzhBiz.saveRobotData(getTime, currentStation.getMissionId(), surveyData);
            if (!isSurveyAtOnce) {
                // 2024/11/26 漏测处理,判断是否加测
                String serialNos = controlBoxList.stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
                qwMsgService.handleSurveyFail(currentStation.getMissionId(), serialNos, recycleNum);
            }
        }
        //标识各测站测量完成
        for (ControlBoxAo boxAo : controlBoxList) {
            if (boxAo.getSurveyBiz()!=null) {
                boxAo.getSurveyBiz().complete();
            }
        }

        //完成测量
        currentStation.setSurveyStatus("待测");
        isSurveyAtOnce = false;

        surveyBizSubActionCompleted();
    }

    /**
     * 更新测量漏传记录表
     */
    private void saveRobotSurveyRecord(int recycleNum, Date uploadAlarmTime){
        String serialNos = controlBoxList.stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
        try {
            log.info("{} 更新测量漏测漏传记录: {}...", serialNos, uploadAlarmTime);
            RobotSurveyRecord record = new RobotSurveyRecord();
            record.setMissionId(currentStation.getMissionId());
            record.setMissionName(currentStation.getName());
            record.setSerialNo(serialNos);
            record.setRecycleNum(recycleNum);
            record.setSurveyFinish(1);
            record.setSurveyAlarmTime(new Date());
            record.setUploadFinish(0);
            record.setUploadAlarmTime(uploadAlarmTime);
            robotSurveyRecordService.save(record);
            log.info("{} 更新测量漏测漏传记录结束", serialNos);
        } catch (Exception e) {
            log.info("{} 更新测量漏测漏传记录表异常: {}", serialNos, e.getMessage());
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
        int recycleNum = dataXyzh.getRecycleNum();
        List<Long> ptIds = getSurveyPtIds();
        if (ptIds.isEmpty()) {
            return;
        }
        //判断推送类型：本期数据 1 还是上期数据 0
        if(taskOther.getPushCurrentData() == 1) {
            //推送本期数据没有延迟时立即推送，设置延时创建定时任务推送
            if (taskOther.getDelayUploadTime() <=0) {
                //立即推送
                doPushCurrentDataList(recycleNum, ptIds);
            } else {
                //创建定时任务
                createCurrentPushOtherJob(recycleNum, ptIds, taskOther);
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
     * @param recycleNum recycleNum
     * @param ptIds      ptIds
     * @param taskOther  taskOther
     */
    private void createCurrentPushOtherJob(int recycleNum, List<Long> ptIds, PushTaskOther taskOther) {
        CurrentPushOtherJobParam param = new CurrentPushOtherJobParam();
        Date current = new Date();
        //获取延时推送时长
        int delayUploadTime = taskOther.getDelayUploadTime() > 0 ? taskOther.getDelayUploadTime() : 10;
        param.setFirstTime(new Date(current.getTime() + delayUploadTime * 60000L));
        param.setFirstTimeCorn(MonitorJobUtil.getCronString(param.getFirstTime()));
        param.setStartDate(current);
        param.setEndDate(new Date(current.getTime() + (delayUploadTime + 5) * 60000L));
        param.setRecycleNum(recycleNum);
        param.setMissionId(currentStation.getMissionId());
        param.setPtIdStr(StringUtils.join(ptIds, ','));
        param.setPushAlarmInfo(taskOther.getPushAlarmInfo() ? "是" : "否");
        surveyJobService.addCurrentPushOtherJob(MonitorJobConst.PUSh_JOB_OTHER_PREFIX + currentStation.getMissionId() + '_' + recycleNum,
                MonitorJobConst.PUSh_TRIGGER_OTHER_PREFIX + currentStation.getMissionId() + '_' + recycleNum,
                CurrentPushOtherJob.class, param);
        log.info("{}本期数据推送定时任务设置成功:{}", currentStation.getName(), param.getFirstTime());
    }

    private void pushLastDataList(List<Long> ptIds, PushTaskOther taskOther, PointDataXyzh dataXyzh){
        doPushLastDataList(dataXyzh.getRecycleNum(), ptIds, taskOther, dataXyzh,
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
     * @param recycleNum recycleNum
     * @param ptIds ptIds
     */
    private void doPushCurrentDataList(int recycleNum, List<Long> ptIds) {
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
        if (!infoList.isEmpty()) {
            infoCorrectService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfoCorrect.class));
        }
        log.info("{}推送本期数据到同步表完成",currentStation.getName());
    }

    /**
     * 提取配置信息中测点信息
     */
    public List<Long> getSurveyPtIds() {
        return dataXyzhBiz.getPointIdWithMission(currentStation.getMissionId());
    }

    /**
     * 获取托推送任务对象
     * @return PushTask
     */
    private PushTaskOther getPushOtherTask() {
        LambdaQueryWrapper<PushTaskOther> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskOther::getMissionId, currentStation.getMissionId());
        return pushTaskOtherService.getOne(wrapper);
    }

    /**
     * 创建推送定时任务
     */
    private void createPushTaskJob(String surveyData, Date getTime){
        //获取推送任务对象，判断是否已经开启数据推送
        PushTask pushTask = getPushTask();
        if (pushTask == null ||  1 != pushTask.getStatus()){
            return;
        }
        String serialNos = controlBoxList.stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
        //删除上一次的定时任务
        surveyJobService.removeJob(MonitorJobConst.PUSh_JOB_PREFIX + serialNos,
                MonitorJobConst.PUSh_TRIGGER_PREFIX + serialNos);
        //monitorItemId|stCfg|recycleNum|rawDatas|calDatas|ajReport
        String[] splitData = surveyData.split("\\|");
        int recycleNum = Integer.parseInt(splitData[2]);
        //非临时加测，且测量成功后，开启定时任务自动推送数据
        PushJobParam param = new PushJobParam();
        Date current = new Date();
        //获取延时推送时长
        int delayUploadTime = pushTask.getDelayUploadTime() > 3 ? pushTask.getDelayUploadTime() : 3;
        param.setFirstTime(new Date(current.getTime() + delayUploadTime * 60000L));
        param.setFirstTimeCorn(MonitorJobUtil.getCronString(param.getFirstTime()));
        param.setStartDate(current);
        param.setEndDate(new Date(current.getTime() + (delayUploadTime + 5) * 60000L));
        param.setSerialNo(serialNos);
        param.setMissionId(currentStation.getMissionId());
        param.setRecycleNum(recycleNum);
        surveyJobService.addPushJob(MonitorJobConst.PUSh_JOB_PREFIX + serialNos,
                MonitorJobConst.PUSh_TRIGGER_PREFIX + serialNos,
                MonitorPushJob.class, param);
        log.info("{}定时任务设置成功:{}", serialNos, param.getFirstTime());
        // 2023/6/19 更新测量记录表,比上传真实时间晚两分钟
        // 2024/11/26 生成漏测漏传记录
        Date uploadTime = new Date(getTime.getTime() + (delayUploadTime + 2) * 60000L);
        saveRobotSurveyRecord(recycleNum, uploadTime);
        // 2023/6/19 更新测量记录表,比上真实传时间晚两分钟
    }

    /**
     * 获取托推送任务对象
     * @return PushTask
     */
    private PushTask getPushTask() {
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, currentStation.getMissionId());
        List<PushTask> tasks = pushTaskService.list(wrapper);
        return !tasks.isEmpty() ? tasks.get(0) : null;
    }


    /**
     * 整网重测(编组中有1站测量失败,则终止所有“在测”测站，暂停指定秒后,整网重测)
     * @param pauseTime 暂停时间
     */
    private boolean cancelAndRepeatSurvey(int pauseTime) {
        // 终止所有“在测”测站
        for (ControlBoxAo boxAo : controlBoxList) {
            if ("在测".equals(boxAo.getSurveyStatus()) && boxAo.getSurveyBiz()!=null) {
                boxAo.getSurveyBiz().cancelSurveying();
            }
            //  2024/3/12 状态恢复
            if (boxAo.getSurveyBiz()!=null) {
                boxAo.getSurveyBiz().setPreSurveying(false);
                boxAo.getSurveyBiz().setPreSurveyOk(true);
                boxAo.getSurveyBiz().setSurveyFinished(false);
                boxAo.getSurveyBiz().setFinalOk(false);
            }
        }

        /*
         * 软件中所有异步场景，没有使用await--async异步机制确保异步执行完成,是导致下面这种尴尬的技术失误
         * 后面重构要注意"领域驱动设计DDD"的思想和4方面技术的应用：MVVM+await--async+全局事件+EFCore的ORM
         * --20240306
         */
        // 等待指定时间
        try {
            Thread.sleep(pauseTime * 1000L);
        } catch (InterruptedException e) {
            log.info("整网重测等待时间异常:{}", e.getMessage());
        }

        //超过最大整网重测数，退出
        if (currentRepeatNum >= maxRepeatNum) {
            return false;
        }

        //重新测量
        currentRepeatNum += 1;
        startSurvey(true);
        return true;
    }

    /**
     * 平差计算
     */
    private boolean adjustCalc(){
        //获得整网测点列表和测站列表
        List<SurveyStation> stations = new ArrayList<>();
        List<Point3d> ptList = new ArrayList<>();
        for (ControlBoxAo boxAo : controlBoxList) {
            SurveyStation station = boxAo.getSurveyBiz().getSurveyStation();
            stations.add(station);
            ptList.add(station.getStation());
            for (SurveyLine line : station.getSurveyLines()) {
                ptList.add(line.getPoint2());
            }
        }

        //过滤重复点(pId相同为重复)
        Set<Long> pIdSet = new HashSet<>();
        for (Point3d point3d : ptList) {
            pIdSet.add(point3d.getId());
        }

        //获取新点列表，并用新点替换Station中相同pId点
        List<Point3d> newPtList = new ArrayList<>();
        for (Long pId : pIdSet) {
            Optional<Point3d> optional = ptList.stream().filter(it -> it.getId() == pId).findAny();
            optional.ifPresent(pt -> {
                newPtList.add(pt);
                for (SurveyStation station : stations) {
                    if (station.getStation().getId() == pId) {
                        station.setStation(pt);
                    }
                    for (SurveyLine line : station.getSurveyLines()) {
                        if (line.getPoint1().getId() == pId) {
                            line.setPoint1(pt);
                        }
                        if (line.getPoint2().getId() == pId) {
                            line.setPoint2(pt);
                        }
                    }
                }
            });
        }

        //计算未知点概略坐标
        boolean isOk = SurveyResultProcessNewClassicUpdate.calcPtXYZ0(stations);
        //若不成功，退出平差计算
        if (!isOk)
        {
            // backMsg.Ok = false;
            // backMsg.Msg = "已知数据不足以推算概略坐标--无法平差计算!";
            return false;
        }

        //平差处理
        double[] dm02 = new double[]{1.0};
        //计算未知点概略坐标
        isOk = SurveyResultProcessNewClassicUpdate.adjust(newPtList, stations, dm02);
        String msg = isOk ? "平差成功" : "平差失败";
        String adjReport = SurveyResultProcessNewClassicUpdate.showResult(isOk, msg, newPtList, stations, dm02[0]);

        //获得坐标成果
        finalResults.clear();
        for (Point3d pt : newPtList) {
            String result = pt.getId() + "," + pt.getName() + ","  + pt.getX() + "," + pt.getY() + "," + pt.getZ() + ",0,0,0";
            finalResults.add(result);
        }

        //获得测量过程成果
        String monitorItemId = "";
        String recycleNum = "";
        StringBuilder rawDatas = new StringBuilder();
        StringBuilder calcDatas = new StringBuilder();
        for (ControlBoxAo boxAo : controlBoxList) {
            SurveyBiz surveyBiz = boxAo.getSurveyBiz();
            if (surveyBiz == null) {
                continue;
            }
            String[] surveyDatas = surveyBiz.getSurveyData().split("\\|");
            monitorItemId = surveyDatas[0];
            recycleNum = surveyDatas[2];
            rawDatas.append(surveyDatas[3]).append(";");
            calcDatas.append(surveyDatas[3]).append(";");
        }

        //删除最后的;号
        rawDatas = new StringBuilder(rawDatas.substring(0, rawDatas.toString().length() - 1));
        calcDatas = new StringBuilder(calcDatas.substring(0, calcDatas.toString().length() - 1));
        surveyData = monitorItemId + "|" + currentStation.getName() + "|" + recycleNum + "|" + rawDatas + "|" +
                calcDatas + "|" + adjReport;

        return isOk;
    }
    //endregion

}
