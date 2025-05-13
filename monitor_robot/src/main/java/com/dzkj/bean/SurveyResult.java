package com.dzkj.bean;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 测量结果类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class SurveyResult {

    private long id;

    /**
     * 点名
     */
    private String ptName;

    /**
     * 棱镜高
     */
    private double ht;

    /**
     * 是否固定
     */
    private boolean asFixed;

    /**
     * 测量是否成功
     */
    private boolean success;

    /**
     * 测回数序号
     */
    private int chIndex;

    /**
     * 是否正镜
     */
    private boolean isFace1;

    /**
     * 电子气泡倾斜补偿值-横轴(弧度)
     */
    private double t;

    /**
     * 电子气泡倾斜补偿值-竖轴(弧度)
     */
    private double l;

    /**
     * 水平角(弧度)
     */
    private double ha;

    /**
     * 竖直角(弧度)
     */
    private double va;

    /**
     * 斜距(m)
     */
    private double sd;

    /**
     * 坐标X(m)
     */
    private double x;

    /**
     * 坐标Y(m)
     */
    private double y;

    /**
     * 坐标Z(m)
     */
    private double z;

    /**
     * 起始坐标X(m)
     */
    private double x0;

    /**
     * 起始坐标Y(m)
     */
    private double y0;

    /**
     * 起始坐标Z(m)
     */
    private double z0;

    /**
     * 首次测量
     */
    private boolean first;

    /**
     * 采集时间
     */
    private Date getTime;

}
