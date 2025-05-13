package com.dzkj.biz.alarm_setting.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
public class AlarmDetailVO implements Serializable {

    private Long id;

    /**
     * 是否是三维位移监测类型
     */
    private boolean xyzh;

    /**
     * 测点id
     */
    private Long pid;

    /**
     * 测点名称
     */
    private String name;

    /**
     * 是否停测
     */
    private Boolean stop;

    /**
     * 测点状态
     */
    private String status;

    /**
     * 周期数
     */
    private Integer recycleNum;

    /**
     * x本次值
     */
    private Double x;

    /**
     * y本次值
     */
    private Double y;

    /**
     * z本次值
     */
    private Double z;

    /**
     * p本次值
     */
    private Double p;

    /**
     * s本次值
     */
    private Double s;

    /**
     * t本次值
     */
    private Double t;

    /**
     * x本次变化量
     */
    private Double deltX;

    /**
     * y本次变化量
     */
    private Double deltY;

    /**
     * z本次变化量
     */
    private Double deltZ;

    /**
     * p本次变化量
     */
    private Double deltP;

    /**
     * s本次变化量
     */
    private Double deltS;

    /**
     * t本次变化量
     */
    private Double deltT;

    /**
     * x变化速率
     */
    private Double vDeltX;

    /**
     * y变化速率
     */
    private Double vDeltY;

    /**
     * z变化速率
     */
    private Double vDeltZ;

    /**
     * p变化速率
     */
    private Double vDeltP;

    /**
     * s变化速率
     */
    private Double vDeltS;

    /**
     * t变化速率
     */
    private Double vDeltT;

    /**
     * x累计变化量
     */
    private Double totalX;

    /**
     * y累计变化量
     */
    private Double totalY;

    /**
     * z累计变化量
     */
    private Double totalZ;

    /**
     * p累计变化量
     */
    private Double totalP;

    /**
     * s累计变化量
     */
    private Double totalS;

    /**
     * t累计变化量
     */
    private Double totalT;

    /**
     * 观测时间
     */
    private Date getTime;
    private String getTimeStr;

}
