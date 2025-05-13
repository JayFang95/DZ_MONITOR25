package com.dzkj.dataSwap.bean.basic_info;

import com.alibaba.fastjson.JSONArray;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/2 14:06
 * @description 监测数据上报
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class PointBasicInfo {

    /**
     * 建设项目编号
     */
    private String projectCode;

    /**
     * 多个监测设备信息
     * deviceCatagory: int监测设备分类(1	全站仪; 其他见对接文档)
     * code: String 设备唯一性代码，在本项目中要保持设备代码的唯一性
     * pArr: JSONArray 多个监测点的信息
     *      pCode: String 测点代码,在一个项目中，测点代码必须唯一
     *      height: int 监测点高度，单位0.01mm
     *      mainCatagory: int 监测点监测目标的大分类(1	线上目标 ;2	线下目标; 99	其它)
     *      subCatagory: int 监测点监测目标的小分类(1	轨道; 其他见对接文档)
     *      railwayType: int 监测点监测铁路线的类型(1	普铁;2	高铁-有砟;3	高铁-无砟;99	其它)
     *      funcType: int 监测点的功能类型(1	监测点;2	基准点;4	连接点;8	计算点;16	测站点)
     *      kiloMark: String 监测点铁路里程(K834+954)
     *      funcAttr: int 测点测量信息类型(1	位移测量	OFFSET_INFO; 其他见对接文档)
     */
    private JSONArray deviceInfo;

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

    public JSONArray getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(JSONArray deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public boolean getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(boolean replace) {
        isReplace = replace;
    }
}
