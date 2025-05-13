package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description TPS开机枚举
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum ComTpsStartUpMode {

    /**
     * TPS1200不支持
     */
    COM_TPS_STARTUP_LOCAL(0),
    /**
     * RPC可用,在线模式
     */
    COM_TPS_STARTUP_REMOTE(1);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    ComTpsStartUpMode(int code) {
        this.code = code;
    }

}
