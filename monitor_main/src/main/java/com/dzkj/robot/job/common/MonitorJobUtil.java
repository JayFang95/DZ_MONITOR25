package com.dzkj.robot.job.common;

import com.dzkj.common.util.DateUtil;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/17 17:09
 * @description 任务工具类
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class MonitorJobUtil {

    /**
     * 获取时间对应执行cron
     */
    public static String getCronString(Date nextDate) {
        StringBuilder cronBuild = new StringBuilder();
        //获取秒 分 时 日 月值
        String dateString = DateUtil.dateToDateString(nextDate, DateUtil.yyyy_MM_dd_HH_mm_ss_EN);
        String[] split = dateString.split(" ");
        String[] dateSplit = split[0].split("-");
        String[] timeSplit = split[1].split(":");
        cronBuild.append(timeSplit[2]).append(" ")
                .append(timeSplit[1]).append(" ")
                .append(timeSplit[0]).append(" ")
                .append(dateSplit[2]).append(" ")
                .append(dateSplit[1]).append(" ?");
        return cronBuild.toString();
    }

}
