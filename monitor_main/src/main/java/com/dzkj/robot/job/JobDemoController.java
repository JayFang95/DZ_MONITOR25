package com.dzkj.robot.job;

import com.dzkj.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 15:52
 * @description JobDemoController
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common")
public class JobDemoController {

    @Autowired
    private RobotSurveyJobService jobService;

    private long index = 1;

    @RequestMapping("job/add/{serialNo}")
    private void addJob(@PathVariable String serialNo){
        JobParam jobParam = new JobParam();
        jobParam.setControlBoxId(index++);
        jobParam.setMissionId(index++);
        jobParam.setSerialNo(serialNo);
        jobParam.setCycleInfo(serialNo + ",2023-03-17,2023-04-17,1,08:00:00,09:00:00");
        jobParam.setStartDate(new Date());
        jobParam.setEndDate(DateUtil.getDate("2023-04-01",DateUtil.yyyy_MM_dd_EN));
        jobParam.setFirstTimeCorn("0/20 * * * * ?");
        jobService.addJob(MonitorJobConst.JOB_PREFIX + serialNo
                , MonitorJobConst.TRIGGER_PREFIX + serialNo, RobotSurveyJob.class, jobParam);
        System.out.println(serialNo + "启动成功");
    }

    @RequestMapping("job/update/{serialNo}")
    private void updateJob(@PathVariable String serialNo){
        jobService.modifyJob(MonitorJobConst.TRIGGER_PREFIX + serialNo, "0/10 * * * * ?", null);
        System.out.println(serialNo + "修改成功");
    }

    @RequestMapping("job/remove/{serialNo}")
    private void removeJob(@PathVariable String serialNo){
        jobService.removeJob(MonitorJobConst.JOB_PREFIX + serialNo,
                MonitorJobConst.TRIGGER_PREFIX + serialNo );
        System.out.println(serialNo + "删除成功");
    }

}
