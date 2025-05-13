package com.dzkj.dataSwap.bean.init_data;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/2 15:06
 * @description 监测点初始化信息
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class PointInitInfo {

    /**
     * 测点代码
     */
    private String pCode;

    /**
     * 位移初始数据
     *      distance: int 矢量坐标的距离，单位0.01mm
     *      hAngle: int 矢量坐标的水平角度，单位0.01s
     *      vAngle: int 矢量坐标的垂直角度，单位0.01s
     *      x: int 水平横坐标，单位0.01mm，垂直轨道方向。
     *      y: int 水平顺向坐标，单位0.01mm，沿着轨道方向。
     *      z: int 高程，单位0.01mm
     */
    private JSONObject offsetInit;

    /**
     * 监测数据产生时间
     */
    private Date dataTime;
    private boolean isReplace;

    public String getPCode() {
        return pCode;
    }

    public void setPCode(String pCode) {
        this.pCode = pCode;
    }

    public JSONObject getOffsetInit() {
        return offsetInit;
    }

    public void setOffsetInit(JSONObject offsetInit) {
        this.offsetInit = offsetInit;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public boolean getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(boolean replace) {
        isReplace = replace;
    }
}
