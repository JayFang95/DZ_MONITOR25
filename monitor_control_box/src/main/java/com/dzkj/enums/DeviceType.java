package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 测量机器人品牌类型
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum DeviceType {

    /**
     * 空
     */
    NULL(0),
    /**
     * 莱卡
     */
    LEI_CA(1),
    /**
     * Trimble
     */
    TRIMBLE(2),
    /**
     * Sokka
     */
    SOKKA(3);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    DeviceType(int code) {
        this.code = code;
    }

}
