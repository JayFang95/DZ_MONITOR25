package com.dzkj.dataSwap.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/10 9:32
 * @description 监测延测上报VO
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MonitorErrorVO {

    /**
     * 建设项目编号
     */
    private String projectCode;
    /**
     * 测点设备代码
     */
    private String deviceNo;
    /**
     * 故障原因
     */
    private String reason;
    /**
     * 测量故障的时间
     */
    private Date dataTime;

}
