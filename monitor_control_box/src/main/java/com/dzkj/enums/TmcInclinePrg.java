package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 倾斜传感器测量编程
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum TmcInclinePrg {

    /**
     * 使用传感器(apriori sigma)
     */
    TMC_MEA_INC(0),
    /**
     * 自动选择(由系统在0,2两种模式中自行选择)
     */
    TMC_AUTO_INC(1),
    /**
     * 适应平面(apriori sigma)
     */
    TMC_PLANE_INC(2);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    TmcInclinePrg(int code) {
        this.code = code;
    }

}
