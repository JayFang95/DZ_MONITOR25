package com.dzkj.robot.job;

import com.dzkj.common.util.DateUtil;
import com.dzkj.robot.job.common.MonitorJobUtil;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 11:36
 * @description 测量定时任务业务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class RobotSurveyJobService {

    private static final String JOB_GROUP_NAME = "MONITOR_JOB_GROUP";
    private static final String TRIGGER_GROUP_NAME = "MONITOR_TRIGGER_GROUP";
    private static Scheduler scheduler;

    public RobotSurveyJobService(Scheduler scheduler) {
        RobotSurveyJobService.scheduler = scheduler;
    }

    /**
     * @description 添加定时任务
     * @author jing.fang
     * @date 2023/3/11 12:35
     * @param jobName 任务名称：monitor_job_仪器编号
     * @param triggerName 任务名称：monitor_trigger_仪器编号
     * @param jobClass 任务执行类
     * @param jobParam 传递参数
     **/
    public boolean addJob(String jobName, String triggerName, Class<? extends Job> jobClass
            ,JobParam jobParam){
        try {
            //构建任务详情：任务名  任务组  任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .usingJobData("controlBoxId", jobParam.getControlBoxId())
                    .usingJobData("missionId", jobParam.getMissionId())
                    .usingJobData("serialNo", jobParam.getSerialNo())
                    .usingJobData("cycleInfo", jobParam.getCycleInfo())
                    .usingJobData("timeInfo", DateUtil.dateToDateString(jobParam.getFirstTime(), "HH:mm:ss"))
                    .usingJobData("recycleNum", jobParam.getRecycleNum())
                    .build();
            //触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
            //设置触发器起始日期
            triggerBuilder.startAt(jobParam.getStartDate());
            triggerBuilder.endAt(jobParam.getEndDate());
            //设置任务执行cron
            //验证第一次时间是否小于当前时间，若小于设置当前时间15秒后执行
            String newFirstTimeCorn = checkFirstTimeCanExecute(jobParam.getFirstTime(), jobParam.getFirstTimeCorn());
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newFirstTimeCorn));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            //调度器设置jobDetail和trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @description 添加数据推送定时任务
     * @author jing.fang
     * @date 2023/3/11 12:35
     * @param jobName 任务名称：push_job_仪器编号_采集周期
     * @param triggerName 任务名称：push_trigger_仪器编号_采集周期
     * @param jobClass 任务执行类
     * @param jobParam 传递参数
     **/
    public boolean addPushJob(String jobName, String triggerName, Class<? extends Job> jobClass
            ,PushJobParam jobParam){
        try {
            //构建任务详情：任务名  任务组  任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .usingJobData("missionId", jobParam.getMissionId())
                    .usingJobData("serialNo", jobParam.getSerialNo())
                    .usingJobData("recycleNum", jobParam.getRecycleNum())
                    .usingJobData("thirdPartType", jobParam.getThirdPartType())
                    .build();
            //触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
            //设置触发器起始日期
            triggerBuilder.startAt(jobParam.getStartDate());
            triggerBuilder.endAt(jobParam.getEndDate());
            //设置任务执行cron
            //验证第一次时间是否小于当前时间，若小于设置当前时间15秒后执行
            String newFirstTimeCorn = checkFirstTimeCanExecute(jobParam.getFirstTime(), jobParam.getFirstTimeCorn());
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newFirstTimeCorn));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            //调度器设置jobDetail和trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @description 添加多站联测定时任务
     * @author jing.fang
     * @date 2023/3/11 12:35
     * @param jobName 任务名称：monitor_job_多站id
     * @param triggerName 任务名称：monitor_trigger_多站id
     * @param jobClass 任务执行类
     * @param jobParam 传递参数
     **/
    public boolean addMultiJob(String jobName, String triggerName, Class<? extends Job> jobClass
            ,MultiJobParam jobParam){
        try {
            //构建任务详情：任务名  任务组  任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .usingJobData("multiStationId", jobParam.getMultiStationId())
                    .usingJobData("cycleInfo", jobParam.getCycleInfo())
                    .build();
            //触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
            //设置触发器起始日期
            triggerBuilder.startAt(jobParam.getStartDate());
            triggerBuilder.endAt(jobParam.getEndDate());
            //设置任务执行cron
            //验证第一次时间是否小于当前时间，若小于设置当前时间15秒后执行
            String newFirstTimeCorn = checkFirstTimeCanExecute(jobParam.getFirstTime(), jobParam.getFirstTimeCorn());
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newFirstTimeCorn));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            //调度器设置jobDetail和trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @description 添加数据推送(其他)定时任务
     * @author jing.fang
     * @date 2024/11/19 12:35
     * @param jobName 任务名称：push_job_仪器编号_采集周期
     * @param triggerName 任务名称：push_trigger_仪器编号_采集周期
     * @param jobClass 任务执行类
     * @param jobParam 传递参数
     **/
    public boolean addCurrentPushOtherJob(String jobName, String triggerName, Class<? extends Job> jobClass
            ,CurrentPushOtherJobParam jobParam){
        try {
            //构建任务详情：任务名  任务组  任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, JOB_GROUP_NAME)
                    .usingJobData("recycleNum", jobParam.getRecycleNum())
                    .usingJobData("missionId", jobParam.getMissionId())
                    .usingJobData("ptIdStr", jobParam.getPtIdStr())
                    .usingJobData("pushAlarmInfo", jobParam.getPtIdStr())
                    .build();
            //触发器
            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
            //设置触发器起始日期
            triggerBuilder.startAt(jobParam.getStartDate());
            triggerBuilder.endAt(jobParam.getEndDate());
            //设置任务执行cron
            //验证第一次时间是否小于当前时间，若小于设置当前时间15秒后执行
            String newFirstTimeCorn = checkFirstTimeCanExecute(jobParam.getFirstTime(), jobParam.getFirstTimeCorn());
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newFirstTimeCorn));
            // 创建Trigger对象
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            //调度器设置jobDetail和trigger
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动
            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @description 更新定时任务执行时间
     * @author jing.fang
     * @date 2023/3/11 15:45
     * @param triggerName 任务触发器名称
     * @param newCron 新的执行时间表达式
     * @param newDate 新的执行时间，非一次任务时为null
     **/
    public boolean modifyJob(String triggerName, String newCron, Date newDate){
        try{
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                return false;
            }

            String oldCron = trigger.getCronExpression();
            if (!oldCron.equals(newCron)){
                // 触发器
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组
                triggerBuilder.withIdentity(triggerName, TRIGGER_GROUP_NAME);
                triggerBuilder.startNow();
                // 触发器时间设定
                //验证新的一次时间是否小于当前时间，若小于设置当前时间10秒后执行
                String newTimeCorn = checkFirstTimeCanExecute(newDate, newCron);
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newTimeCorn));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改任务的触发时间 方式二：先删除再重新创建新Job
                scheduler.rescheduleJob(triggerKey, trigger);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @description 删除定时任务
     * @author jing.fang
     * @date 2023/3/11 15:47
     * @param jobName 任务名称
     * @param triggerName 触发器名称
     **/
    public boolean removeJob(String jobName, String triggerName){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, TRIGGER_GROUP_NAME);
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            boolean b = scheduler.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证本次执行时间的可行性,当前时间10秒后有效
     *
     * @param executeTime  executeTime
     * @param executeTimeCorn  executeTimeCorn
     * @return newFirstTimeCron
     */
    private static String checkFirstTimeCanExecute(Date executeTime, String executeTimeCorn) {
        if (executeTime == null){
            return executeTimeCorn;
        }
        Date date = new Date(System.currentTimeMillis() + 10 * 1000);
        if (executeTime.getTime() >= date.getTime()){
            return executeTimeCorn;
        }else {
            //触发器时间设定: 秒 分 时 日 月 ?(周使用?代替)
            return MonitorJobUtil.getCronString(date);
        }
    }

}
