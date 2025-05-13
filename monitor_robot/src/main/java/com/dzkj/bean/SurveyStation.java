package com.dzkj.bean;

import lombok.Data;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 9:45
 * @description 测站
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class SurveyStation {

    /**
     * 测站点
     */
    private Point3d station;

    /**
     * 观测线路
     */
    private List<SurveyLine> surveyLines;

    /**
     * 仪器高
     */
    private double hi;
}
