package com.dzkj.dataSwap.bean.init_data;

import com.alibaba.fastjson.JSONArray;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/2 14:06
 * @description 监测初始数据上报
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class UploadInitData {

    /**
     * 建设项目编号
     */
    private String projectCode;

    /**
     * 多个监测点初始化信息
     * pCode: String 测点代码,在一个项目中，测点代码必须唯一
     * offsetInit: JSONArray 位移初始数据
     *      distance: int 矢量坐标的距离，单位0.01mm
     *      hAngle: int 矢量坐标的水平角度，单位0.01s
     *      vAngle: int 矢量坐标的垂直角度，单位0.01s
     *      x: int 水平横坐标，单位0.01mm，垂直轨道方向。
     *      y: int 水平顺向坐标，单位0.01mm，沿着轨道方向。
     *      z: int 高程，单位0.01mm
     */
    private JSONArray pointInitInfo;

    /**
     *默认false。true代表覆盖该项目之前上报的所有监测点信息，反之把当下的监测点信息附加到之前上报的中。
     */
    private boolean isReplace;

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public JSONArray getPointInitInfo() {
        return pointInitInfo;
    }

    public void setPointInitInfo(JSONArray pointInitInfo) {
        this.pointInitInfo = pointInitInfo;
    }

    public boolean getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(boolean replace) {
        isReplace = replace;
    }

}
