package com.dzkj.robot.job;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 15:22
 * @description 推送任务(其他)传递参数对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class CurrentPushOtherJobParam {

    /**
     * 本期测量周期数
     **/
    private int recycleNum;
    /**
     * 本期测量周期数
     **/
    private Long missionId;
    /**
     * 测点字符串(,分隔)
     **/
    private String ptIdStr;
    /**
     * 推送报警信息
     */
    private String pushAlarmInfo;
    /**
     * 第一次任务执行时间cron值
     **/
    private String firstTimeCorn;
    /**
     * 第一次任务执行时间
     **/
    private Date firstTime;
    /**
     * 触发器开始时间
     **/
    private Date startDate;
    /**
     * 触发器结束时间
     **/
    private Date endDate;

}
