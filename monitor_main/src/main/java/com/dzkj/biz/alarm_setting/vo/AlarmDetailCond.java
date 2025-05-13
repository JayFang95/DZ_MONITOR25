package com.dzkj.biz.alarm_setting.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AlarmDetailCond implements Serializable {

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 测点id
     */
    private Long pid;

    /**
     * 周期数
     */
    private Integer recycleNum;



}
