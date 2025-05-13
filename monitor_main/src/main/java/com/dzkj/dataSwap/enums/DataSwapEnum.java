package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 14:22
 * @description 数据交换接口行为枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum DataSwapEnum {

    //接口枚举
    SHANK_HAND("shakeHand_S", "握手消息"),
    UPLOAD_POINT_BASIC_INFO("uploadPointBasicInfo_S", "监测点信息上报"),
    UPLOAD_INIT_DATA("uploadInitData_S", "监测初始数据上报"),
    UPLOAD_MONITOR_ERR("uploadMonitorError_S", "监测延测上报"),
    UPLOAD_MONITOR_DATA("uploadMonitorData_S", "监测数据上报"),
    UPLOAD_PROCESS_DATA("uploadProcessData_S", "监测过程数据上报"),
    QUERY_MONITOR_DATA_INDEX("queryMonitorDataIndex_S", "监测数据索引查询"),
    REQUEST_MONITOR_DATA_UPLOAD("requestMonitorDataUpload_S", "监测数据请求上报");

    private String action;
    private String desc;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    DataSwapEnum(String action, String desc) {
        this.action = action;
        this.desc = desc;
    }

}
