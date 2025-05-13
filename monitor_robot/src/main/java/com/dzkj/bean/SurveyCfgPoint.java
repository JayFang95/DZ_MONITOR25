package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/15 14:21
 * @description 测量配置的测量点类
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class SurveyCfgPoint {

    private Long id;

    private String name;
    /**
     * 是否测站
    **/
    private boolean station;
    /**
     * 是否稳定
     **/
    private boolean stable;

    private double x;
    private double y;
    private double z;
    private double ha;
    private double va;

    /**
     * 是否固定
    **/
    private boolean asFixed;
    /**
     * 仪器高
    **/
    private double hi;
    /**
     * 棱镜高
     **/
    private double ht;
    /**
     * 测点测量前睡眠时间：默认0秒
     */
    private int sleepTime;

}
