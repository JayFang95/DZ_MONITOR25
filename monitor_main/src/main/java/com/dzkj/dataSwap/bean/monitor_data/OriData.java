package com.dzkj.dataSwap.bean.monitor_data;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 19:08
 * @description 原始数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class OriData {

    /**
     * 测点代码，该测点代码必须在监测点上报信息中存在
     */
    private String pCode;
    /**
     * 本测点数据测量时间
     */
    private Date dataTime;
    /*
     * 目前只有位移监测支持原始数据上报，把ORI_OFFSET_INFO中的项目填充到此处即可。
     */
    /**
     * 矢量坐标的距离，单位0.01mm
     */
    private int distance;
    /**
     * 矢量坐标的水平角度，单位0.01s
     */
    private int hAngle;
    /**
     * 矢量坐标的垂直角度，单位0.01s
     */
    private int vAngle;
    /**
     * 测回数，base1
     */
    private int loop;
    /**
     * 1左盘 2右盘
     */
    private int dir;

}
