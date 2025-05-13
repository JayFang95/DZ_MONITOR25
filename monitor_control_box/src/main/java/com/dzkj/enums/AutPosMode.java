package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 定位精度
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum AutPosMode {

    /**
     * 快速定位模式
     */
    AUT_NORMAL(0),
    /**
     * 精确定位模式，注意: 需要更多的定位时间
     */
    AUT_PRECISE(1),
    /**
     * 对于TS30/TM30设备,使用上一次有效的倾角和定位限差来定位
     */
    AUT_Fast(2);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    AutPosMode(int code) {
        this.code = code;
    }

}
