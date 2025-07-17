package com.dzkj.dataSwap.bean.ctce_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/8 下午1:38
 * @description 中铁四局测量数据接口对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MeasureData {

    /**
     *测点编号
     */
    @JsonProperty("MONITORCODE")
    private String MONITORCODE;
    /**
     *大地高
     */
    @JsonProperty("DATAH")
    private String DATAH;
    /**
     *x坐标
     */
    @JsonProperty("GXDATA")
    private String GXDATA;
    /**
     *y坐标
     */
    @JsonProperty("GYDATA")
    private String GYDATA;
    /**
     *x方向累计位移
     */
    @JsonProperty("DELTAX")
    private String DELTAX;
    /**
     *y方向累计位移
     */
    @JsonProperty("DELTAY")
    private String DELTAY;
    /**
     *h方向累计位移
     */
    @JsonProperty("DELTAH")
    private String DELTAH;
    /**
     *x坐标平面位移速率
     */
    @JsonProperty("DATAXSPEED")
    private String DATAXSPEED;
    /**
     *y坐标平面位移速率
     */
    @JsonProperty("DATAYSPEED")
    private String DATAYSPEED;
    /**
     *h坐标平面位移速率
     */
    @JsonProperty("DATAHSPEED")
    private String DATAHSPEED;
    /**
     *经度
     */
    @JsonProperty("LONGITUDE")
    private String LONGITUDE;
    /**
     *纬度
     */
    @JsonProperty("LATITUDE")
    private String LATITUDE;
    /**
     *统计时间
     */
    @JsonProperty("STATISTICSDATE")
    private String STATISTICSDATE;

}

