package com.dzkj.dataSwap.bean.monitor_data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 19:06
 * @description 测点上报
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MonitorData {

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 监测数据生成时间
     */
    private Date dataTime;
    /**
     * 测量当时的环境信息
     * temperature	Int  N
     * pressure	Int  N
     */
    private JSONObject env;
    /**
     * 结果数据
     */
    private JSONArray resultData;
    /**
     * 原始数据
     */
    private JSONArray oriData;
    /**
     * 数据在提供方的唯一性标识
     * 所有上传测点id的拼接
     */
    private String uid;

}
