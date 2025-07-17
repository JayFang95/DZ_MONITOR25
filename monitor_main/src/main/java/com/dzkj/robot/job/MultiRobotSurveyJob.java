package com.dzkj.robot.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.common.util.DateUtil;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.job.common.MonitorJobUtil;
import com.dzkj.robot.multi.MultiStationAo;
import com.dzkj.robot.multi.MultiSurveyHandler;
import com.dzkj.robot.survey.MultiSurveyBiz;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 12:30
 * @description 自动监测任务-多站联测
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class MultiRobotSurveyJob implements Job {

    @Autowired
    private RobotSurveyJobService jobService;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IRobotSurveyRecordService recordService;
    @Autowired
    private QwMsgService qwMsgService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Long multiStationId = jobDataMap.getLong("multiStationId");
        Long missionId = jobDataMap.getLong("missionId");
        String serialNo = jobDataMap.getString("serialNos");
        String cycleInfo = jobDataMap.getString("cycleInfo");
        int recycleNum = jobDataMap.getInt("recycleNum");
        //设置下一次的采集时间，更新定时任务
        String nextDateTimeInfo = updateJobCron(multiStationId, cycleInfo);
        //执行本次采集任务
        MultiStationAo multiStation = MultiSurveyHandler.getStation(multiStationId);
        if (multiStation != null){
            log.info("{} 开始执行采集任务, 下次采集时间: {}"
                    , multiStation.getName(), nextDateTimeInfo);
            MultiSurveyBiz multiSurveyBiz = multiStation.getMultiSurveyBiz();
            if (multiSurveyBiz != null) {
                multiSurveyBiz.startMultiTask();
                multiSurveyBiz.setSurveyDateTimeInfo(nextDateTimeInfo);

                //记录本次多站定时测量任务
                String serialNos = multiSurveyBiz.getControlBoxList().stream().map(ControlBoxAo::getSerialNo).collect(Collectors.joining("|"));
                saveRecordSurveyData(multiSurveyBiz.getControlBoxList().get(0).getId(), serialNos);
            }else {
                // 2024/11/26 漏测处理,判断是否加测
                qwMsgService.handleSurveyFail(missionId, serialNo, recycleNum);
            }
        }
    }

    /**
     * 测量记录入库
     * @param controlBoxId controlBoxId
     * @param serialNo serialNo
     */
    private void saveRecordSurveyData(Long controlBoxId, String serialNo) {
        try {
            log.info("控制器 {} 测量漏测漏传记录保存", serialNo);
            RobotSurveyRecord surveyRecord = controlBoxService.getSurveyRecordInfoByControlBoxId(controlBoxId);
            int delayUploadTime = 60;
            if (surveyRecord == null){
                surveyRecord = new RobotSurveyRecord();
                surveyRecord.setSerialNo(serialNo);
                surveyRecord.setMissionId(0L);
                surveyRecord.setMissionName("");
            }
            LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PushTask::getMissionId, surveyRecord.getMissionId());
            List<PushTask> list = pushTaskService.list(wrapper);
            int delayTime = !list.isEmpty() ? list.get(0).getDelayUploadTime() : delayUploadTime;
            Date date = new Date();
            surveyRecord.setSurveyAlarmTime(new Date(date.getTime() + 20 * 60000));
            surveyRecord.setUploadAlarmTime(new Date(date.getTime() + delayTime * 60000L));
            surveyRecord.setSurveyFinish(0);
            surveyRecord.setUploadFinish(!list.isEmpty() ? list.get(0).getStatus()==1 ? 0 : 1 : 1);
            recordService.save(surveyRecord);
            log.info("控制器 {} 测量漏测漏传记录保存成功", serialNo);
        } catch (Exception e) {
            log.info("控制器 {} 测量漏测漏传记录保存异常: {}" , serialNo, e.getMessage());
        }
    }

    /**
     * controlBoxId 修改成了测站id 逻辑调整 （2024-03-01）
     * @param multiStationId 多测站id
     *                     调整后测量周期参数少了策略名称 （2024-03-01）
     * @param cycleInfo    采集策略: 开始日期，结束日期，间隔天数，时间点
     * @description 更新定时任务执行计划
     * @author jing.fang
     * @date 2023/3/11 19:06
     **/
    private String updateJobCron(Long multiStationId, String cycleInfo) {
        String[] split = cycleInfo.split(",");
        //验证是否任务结束
        Date currentDate = new Date();
        LocalTime currentTime = LocalTime.parse(DateUtil.dateToDateString(currentDate, DateUtil.HH_mm_ss_EN)
                , DateTimeFormatter.ofPattern(DateUtil.HH_mm_ss_EN));
        String endDateStr = split[1] + " 23:59:59";
        Date endDate = null;
        try {
            endDate = DateUtil.getDate(endDateStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        } catch (Exception e) {
            log.error("结束时间格式错误：{}", endDateStr );
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Date nextDate = null;
        LocalTime selectTime;
        //循环比较第一个时间点后时间点，选择下一个可执行时间值
        for (int i = 4; i < split.length; i++) {
            selectTime = LocalTime.parse(split[i], DateTimeFormatter.ofPattern(DateUtil.HH_mm_ss_EN));
            if (selectTime.isAfter(currentTime)) {
                //获取下一个时间周期的第一个时间
                String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " " + split[i];
                nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
                break;
            }
        }
        //当前没有获取到下一次时间值 表示当天时间点都已经执行过
        if (nextDate == null) {
            //获取下一个时间周期的第一个时间
            nextDate = getNextPeriodFirstDate(split, calendar);
        }
        //下一次执行时间超过结束时间，删除定时任务，否则更新定时任务时间
        if (endDate != null && nextDate.getTime() > endDate.getTime()) {
            jobService.removeJob(MonitorJobConst.JOB_PREFIX + multiStationId,
                    MonitorJobConst.TRIGGER_PREFIX + multiStationId);
            // 2023/6/26 记录状态
            //执行本次采集任务
            MultiStationAo multiStation = MultiSurveyHandler.getStation(multiStationId);
            if (multiStation != null && !multiStation.getMultiSurveyBiz().getControlBoxList().isEmpty()) {
                for (ControlBoxAo controlBox : multiStation.getMultiSurveyBiz().getControlBoxList()) {
                    controlBoxService.updateSurvey(controlBox.getId(), 0);
                }
            }
            return "周期任务已完成";
        }
        //设置下一次的执行时间
        jobService.modifyJob(MonitorJobConst.TRIGGER_PREFIX + multiStationId,
                MonitorJobUtil.getCronString(nextDate), nextDate);
        return DateUtil.dateToDateString(nextDate, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 获取下一个周期中的第一个监测时间
     **/
    private Date getNextPeriodFirstDate(String[] split, Calendar calendar) {
        calendar.add(Calendar.DATE, Integer.parseInt(split[2]));
        String nextTimeStr = DateUtil.dateToDateString(calendar.getTime(),DateUtil.yyyy_MM_dd_EN) + " " + split[3];
        return DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
    }

}
