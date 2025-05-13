package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 测量模式
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum BapMeasurePrg {

    /**
     * 不测量,取最后一次
     */
    BAP_NO_MEAS(0),
    /**
     * 不进行距离测量,只进行角度测量
     */
    BAP_NO_DIST(1),
    /**
     * 默认距离测量,由BAP_SetMeasPrg预定义
     */
    BAP_DEF_DIST(2),
    /**
     * 清空距离值
     */
    BAP_CLEAR_DIST(5),
    /**
     * 停止跟踪
     */
    BAP_STOP_TRK(6);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    BapMeasurePrg(int code) {
        this.code = code;
    }

}
