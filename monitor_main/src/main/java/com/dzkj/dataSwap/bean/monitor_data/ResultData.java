package com.dzkj.dataSwap.bean.monitor_data;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 19:07
 * @description 测量结果数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class ResultData {

    private String pCode;
    /**
     * 单位0.01mm，横向坐标
     */
    private int x;
    /**
     * 单位0.01mm，顺向坐标
     */
    private int y;
    /**
     * 单位0.01mm，沉降坐标
     */
    private int z;
    /**
     * 水平横向本次偏移，单位0.01mm
     */
    private int xOffset;
    /**
     * 水平顺向本次偏移，单位0.01mm
     */
    private int yOffset;
    /**
     * 沉降本次偏移，单位0.01mm
     */
    private int zOffset;
    /**
     * 水平本次偏移，单位0.01mm
     */
//    private int xyOffset;
    /**
     * 水平横向累计偏移，单位0.01mm
     */
    private int xTotal;
    /**
     * 水平顺向累计偏移，单位0.01mm
     */
    private int yTotal;
    /**
     * 沉降累计偏移，单位0.01mm
     */
    private int zTotal;
    /**
     * 水平累计偏移，单位0.01mm
     */
//    private int xyTotal;


}
