package com.dzkj.robot.job;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 15:22
 * @description 推送任务传递参数对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushJobParam {

    /**
     * 任务对应控制器id
    **/
    private Long missionId;
    /**
     * 测量周期
     */
    private Integer recycleNum;
    /**
     * 控制器编号
     **/
    private String serialNo;
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
     * 第三方类型
     */
    private int thirdPartType;

}
