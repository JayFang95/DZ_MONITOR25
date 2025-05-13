package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 测量计算结果类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class SurveyCalcResult {

    private long id;

    /**
     * 点名
     */
    private String ptName;

    /**
     * 是否固定
     */
    private boolean asFixed;
    /**
     * 是否归零点
    **/
    private boolean asZero;

    /**
     * 测回数序号
     */
    private int chIndex;

    /**
     * 2C互差是否合格
     */
    private boolean checkOk2C;

    /**
     * i角互差是否合格
     */
    private boolean checkOki;

    /**
     * 测回内检查是否合格
     */
    private boolean checkOkCh;

    /**
     * 测回间Ha是否合格
     */
    private boolean checkOkHaAve;

    /**
     * 测回间Ha检查超限情况信息。若合格，为""
     */
    private String checkOkHaAveInfo;

    /**
     * 测回间Va是否合格
     */
    private boolean checkOkVaAve;

    /**
     * 测回间Va检查超限情况信息。若合格，为""
     */
    private String checkOkVaAveInfo;

    /**
     * 测回间Sd是否合格
     */
    private boolean checkOkSdAve;

    /**
     * 测回间Sd检查超限情况信息。若合格，为""
     */
    private String checkOkSdAveInfo;

    /**
     * 测回间检查是否合格
     */
    private boolean checkOkAll;

    /**
     * 水平角(弧度)_正镜
     */
    private double ha1;

    /**
     * 水平角(弧度)_倒镜
     */
    private double ha2;

    /**
     * 水平角(弧度)_均值
     */
    private double haAve;

    /**
     * 水平角(弧度)_测回均值
     */
    private double ha;

    /**
     * 2C互差(弧度)
     */
    private double d2C;

    /**
     * 竖直角(弧度)_正镜
     */
    private double va1;

    /**
     * 竖直角(弧度)_倒镜
     */
    private double va2;

    /**
     * 竖直角(弧度)_均值
     */
    private double vaAve;

    /**
     * 竖直角(弧度)_测回均值
     */
    private double va;

    /**
     * i角互差(弧度)
     */
    private double di;

    /**
     * 斜距(m)_正镜
     */
    private double sd1;

    /**
     * 斜距(m)_倒镜
     */
    private double sd2;

    /**
     * 斜距(m)_均值
     */
    private double sdAve;

    /**
     * 斜距(m)_测回均值
     */
    private double sd;

    /**
     * X(m)_测回均值
     */
    private double x;

    /**
     * Y(m)_测回均值
     */
    private double y;

    /**
     * Z(m)_测回均值
     */
    private double z;

}
