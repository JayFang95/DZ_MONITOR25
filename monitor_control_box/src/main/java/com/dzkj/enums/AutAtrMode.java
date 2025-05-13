package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 目标自动识别模式--目标识别的可能模式
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum AutAtrMode {

    /**
     * 按指定的hz(水平角)和Va(竖直角)定位目标
     */
    AUT_POSITION(0),
    /**
     * 按环境下的hz(水平角)和Va(竖直角)定位目标
     */
    AUT_TARGET(1);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    AutAtrMode(int code) {
        this.code = code;
    }

}
