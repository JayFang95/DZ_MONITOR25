package com.dzkj.robot.job;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 15:22
 * @description 多站联测监测任务传递参数对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MultiJobParam {

    /**
     * 任务对应多站id
    **/
    private Long multiStationId;
    private Long missionId;
    /**
     * 多站参数
     **/
    private String params;
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
    /**
     * 当前采集策略信息
     **/
    private String cycleInfo;
    private String serialNo;

}
