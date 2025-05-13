package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description TPS停机枚举
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum ComTpsStopMode {

    /**
     * 关机
     */
    COM_TPS_STOP_SHUT_DOWN(0),
    /**
     * 休眠
     */
    COM_TPS_STOP_SLEEP(1);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    ComTpsStopMode(int code) {
        this.code = code;
    }

}
