package com.dzkj.robot.job;

import com.dzkj.common.util.DateUtil;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.job.common.MonitorJobUtil;
import com.dzkj.robot.survey.SurveyBiz;
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

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 12:30
 * @description 自动监测任务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class RobotSurveyJobBak implements Job {

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

    private String timeStr;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Long controlBoxId = jobDataMap.getLong("controlBoxId");
        Long missionId = jobDataMap.getLong("missionId");
        String serialNo = jobDataMap.getString("serialNo");
        String cycleInfo = jobDataMap.getString("cycleInfo");
        String timeInfo = jobDataMap.getString("timeInfo");
        //设置下一次的采集时间，更新定时任务
        String nextDateTimeInfo = updateJobCron(controlBoxId, serialNo, cycleInfo, timeInfo);
        //执行本次采集任务
        SurveyBiz surveyBiz = ControlBoxHandler.getBoxSurveyBiz(controlBoxId);
        if (surveyBiz != null){
            log.info("{} 开始执行采集任务, 下次采集时间: {}"
                    , surveyBiz.getControlBoxAo().getSerialNo(), nextDateTimeInfo);
            surveyBiz.startTask();
            surveyBiz.setSurveyDateTimeInfo(nextDateTimeInfo);
        } else {
            // 2024/11/26 漏测处理,判断是否加测
            qwMsgService.handleSurveyFail(missionId, serialNo, -1);
        }
    }

    /**
     * @param controlBoxId 控制器id
     * @param serialNo     控制器编号
     * @param cycleInfo    采集策略: 开始日期，结束日期，间隔天数，时间点
     * @description 更新定时任务执行计划
     * @author jing.fang
     * @date 2023/3/11 19:06
     **/
    private String updateJobCron(Long controlBoxId, String serialNo, String cycleInfo, String timeInfo) {
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
        for (int i = 3; i < split.length; i++) {
            if (split[i].equals(timeStr)) {
               if (i == split.length - 1){
                  timeStr = split[0];
               } else {
                  timeStr = split[i + 1];
                  //获取下一个时间周期的第一个时间
                  String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " " + split[i + 1];
                  nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
                  break;
               }
            }
//            selectTime = LocalTime.parse(split[i], DateTimeFormatter.ofPattern(DateUtil.HH_mm_ss_EN));
//            if (selectTime.isAfter(currentTime)) {
//                timeStr = split[i];
//                //获取下一个时间周期的第一个时间
//                String nextTimeStr = DateUtil.dateToDateString(currentDate, DateUtil.yyyy_MM_dd_EN) + " " + split[i];
//                nextDate = DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
//                break;
//            }
        }
        System.out.println(timeStr);
        //当前没有获取到下一次时间值 表示当天时间点都已经执行过
        if (nextDate == null) {
            //获取下一个时间周期的第一个时间
            nextDate = getNextPeriodFirstDate(split, calendar);
        }
        //下一次执行时间超过结束时间，删除定时任务，否则更新定时任务时间
        if (endDate != null && nextDate.getTime() > endDate.getTime()) {
            jobService.removeJob(MonitorJobConst.JOB_PREFIX + serialNo,
                    MonitorJobConst.TRIGGER_PREFIX + serialNo);
            // 2023/6/26 记录状态
            controlBoxService.updateSurvey(controlBoxId, 0);
            return "周期任务已完成";
        }
        //设置下一次的执行时间
        jobService.modifyJob(MonitorJobConst.TRIGGER_PREFIX + serialNo,
                MonitorJobUtil.getCronString(nextDate), nextDate);
        return DateUtil.dateToDateString(nextDate, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 获取下一个周期中的第一个监测时间
     **/
    private Date getNextPeriodFirstDate(String[] split, Calendar calendar) {
        calendar.add(Calendar.DATE, Integer.parseInt(split[2]));
        String nextTimeStr = DateUtil.dateToDateString(calendar.getTime(),DateUtil.yyyy_MM_dd_EN) + " " + split[4];
        return DateUtil.getDate(nextTimeStr, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
    }

}
