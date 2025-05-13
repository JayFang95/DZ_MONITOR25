package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 限差参数类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class LimitParams {

    /**
     * 测完是否自动关机
     */
    private boolean turnOffWhenOk = true;

    /**
     * 测回数
     */
    private int chNum = 1;

    /**
     * 电子气泡偏差限差_横轴偏差(弧度)
     */
    private double limitT = 180;

    /**
     * 电子气泡偏差限差_竖轴偏差(弧度)
     */
    private double limitL = 180;

    /**
     * 自动搜索范围_水平角(弧度)
     */
    private double rangHa = 3;

    /**
     * 自动搜索范围_竖直角(弧度)
     */
    private double rangVa = 3;

    /**
     * 目标遮挡暂停时间
     */
    private int pauseTimeCover = 6;

    /**
     * 目标遮挡重测点次数
     */
    private int repeatNumCover = 3;

    /**
     * 坐标偏量限差_X m
     */
    private double limitX = 0.1;

    /**
     * 坐标偏量限差_Y m
     */
    private double limitY = 0.1;

    /**
     * 坐标偏量限差_Z m
     */
    private double limitZ = 0.1;

    /**
     * 坐标偏差过大重测点次数
     */
    private int repeatNumRunOver = 1;

    /**
     * 测完后数据处理失败重测点组次数
     */
    private int repeatNumFail = 0;

    /**
    * 允许控制点测量失败不超过点数
    */
    private int cPointFailedNum = 0;

    /**
     * 测回内水平角归零差(弧度)
     */
    private double dHa0 = 60;

    /**
     * 测回内竖直角归零差(弧度)
     */
    private double dVa0 = 60;

    /**
     * 2C互差(弧度)
     */
    private double d2C = 60;

    /**
     * i角互差(弧度)
     */
    private double di = 60;

    /**
     * 测回间水平角互差(弧度)
     */
    private double dHa = 60;

    /**
     * 测回间竖直角互差(弧度)
     */
    private double dVa = 60;

    /**
     * 测回间斜距互差m
     */
    private double dSd = 0.05;

}
