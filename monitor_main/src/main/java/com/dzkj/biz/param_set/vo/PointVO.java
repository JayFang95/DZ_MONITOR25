package com.dzkj.biz.param_set.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点VO
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PointVO {

    private Long id;

    /**
     * 所属编组id
     */
    private Long ptGroupId;

    /**
     * 测点名
     */
    private String name;

    /**
     * 测点类型
     */
    private String type;

    /**
     * 是否停测
     */
    private Boolean stop;

    /**
     * 方位角
     */
    private double azimuth;

    /**
     * 停测原因
     */
    private String reason;

    /**
     * 创建人
     */
    private String creator;

    private Long createId;

    /**
     * 排序序号
     */
    private int seq;

    /**
     * 监测任务类型
     */
    private String missionType;

    private Boolean enableRule;

    /**
     * 报警修正区间值：
     * 单次负值小；单次负值大；单次正值小；单次正值大
     * 累计负值小；累计负值大；累计正值小；累计正值大
     */
    private double minNegDelt;
    private double maxNegDelt;
    private double minPosDelt;
    private double maxPosDelt;
    private double minNegTotal;
    private double maxNegTotal;
    private double minPosTotal;
    private double maxPosTotal;

}
