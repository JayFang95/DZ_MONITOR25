package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 测量点类，含有点名、角度、距离、坐标、中误差属性
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class SurveyPoint {

    private long id;
    private int recycleNum;
    private String name;
    private boolean asFixed;
    private boolean first;
    private double ha;
    private double va;
    private double sd;
    private double x;
    private double y;
    private double z;
    private double mp;

    public SurveyPoint() {
    }

    public SurveyPoint(long id, String name, boolean asFixed) {
        this.id = id;
        this.name = name;
        this.asFixed = asFixed;
    }
}
