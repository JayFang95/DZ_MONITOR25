package com.dzkj.dataSwap.bean.data_upload;

import lombok.Data;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/11 上午9:24
 * @description 全站仪对接数据测点对象-济南局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PointData {

    /**
     * 测点编号
     */
    private String pointcode;
    /**
     * X坐标(单位米)
     */
    private double x;
    /**
     * Y坐标(单位米)
     */
    private double y;
    /**
     * 高程(单位米)
     */
    private double h;
}

