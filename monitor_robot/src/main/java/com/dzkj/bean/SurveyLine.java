package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 9:46
 * @description 测量线
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class SurveyLine {
    /**
     * 起点
     */
    private Point3d point1;
    /**
     * 终点
     */
    private Point3d point2;
    /**
     * 斜距(观测值)
     */
    private double sd0;
    /**
     * 水平角(观测值)
     */
    private double ha0;
    /**
     * 竖直角(观测值)
     */
    private double va0;

    /**
     * 斜距(平差值)
     */
    private double sd;

    /**
     * 水平角(平差值)
     */
    private double ha;
    /**
     *  竖直角(平差值)
     */
    private double va;
    /**
     * 棱镜高
     */
    private double ht;

    /**
     * 斜距相对精度
     */
    private double ms;

    /**
     * 水平角精度(弧度)
     */
    private double ma;

    /**
     * 竖直角精度(弧度)
     */
    private double mb;

    public SurveyLine(Point3d point1, Point3d point2, double ha,  double va, double sd, double ht) {
        this.point1 = point1;
        this.point2 = point2;
        this.sd0 = sd;
        this.ha0 = ha;
        this.va0 = va;
        this.ht = ht;
    }

    public SurveyLine() {
    }
}
