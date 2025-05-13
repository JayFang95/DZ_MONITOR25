package com.dzkj.robot.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmInfoCorrect;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataXyzhCorrect;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/19 10:11
 * @description 任务推送(其他)执行任务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class CurrentPushOtherJob implements Job {

    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    @Autowired
    private IAlarmInfoService infoService;
    @Autowired
    private IAlarmInfoCorrectService infoCorrectService;
    @Autowired
    private RobotSurveyJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int recycleNum = jobDataMap.getInt("recycleNum");
        Long missionId = jobDataMap.getLong("missionId");
        String ptIdStr = jobDataMap.getString("ptIdStr");
        String pushAlarmInfo = jobDataMap.getString("pushAlarmInfo");
        //推送测量数据和报警信息
        List<Long> ptIds =  Arrays.stream(ptIdStr.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<PointDataXyzh> pushDataList = getPushDataList(recycleNum, ptIds);
        List<AlarmInfo> alarmInfoList = getPushAlarmInfoList(recycleNum, ptIds);
        if (!pushDataList.isEmpty()) {
            dataXyzhCorrectService.saveBatch(DzBeanUtils.listCopy(pushDataList, PointDataXyzhCorrect.class));
        }
        if (!alarmInfoList.isEmpty() && "是".equals(pushAlarmInfo)) {
            infoCorrectService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfoCorrect.class));
        }
        //删除本次定时推送任务
        jobService.removeJob(MonitorJobConst.PUSh_JOB_OTHER_PREFIX + missionId + '_' + recycleNum,
                MonitorJobConst.PUSh_TRIGGER_OTHER_PREFIX + missionId + '_' + recycleNum);
        log.info("监测任务{} 第{}期推送任务已完成", missionId, recycleNum);
    }

    /**
     * 获取所有大于添加推送任务周期的报警信息
     * @param recycleNum recycleNum
     * @param ptIds ptIds
     */
    private List<AlarmInfo> getPushAlarmInfoList(int recycleNum, List<Long> ptIds) {
        LambdaQueryWrapper<AlarmInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AlarmInfo::getRecycleNum, recycleNum)
                .in(AlarmInfo::getPtId, ptIds).orderByAsc(AlarmInfo::getId);
        return infoService.list(wrapper);
    }

    /**
     * 获取所有大于添加推送任务周期的测量数据
     * @param recycleNum recycleNum
     * @param ptIds ptIds
     */
    private List<PointDataXyzh> getPushDataList(int recycleNum, List<Long> ptIds){
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(PointDataXyzh::getRecycleNum, recycleNum)
                .in(PointDataXyzh::getPid, ptIds).orderByAsc(PointDataXyzh::getId);
        return dataXyzhService.list(wrapper);
    }

}
