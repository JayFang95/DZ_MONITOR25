package com.dzkj.common.enums;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/27 14:00
 * @description 上下线常量
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum SocketMsgConst {

    /**
     * 账号上下线信息
     */
    LOGIN(0, "当前账号正在异地登录"),
    LOGOUT(0, "异地登录账号退出系统"),
    ONLINE(5, "公司其他用户上线通知"),
    OUTLINE(5, "公司其他用户下线通知"),

    // 控制器上下线信息
    CONTROL_ON(1001, "ONLINE"),
    CONTROL_OUT(1002, "OUTLINE"),
    // 控制器采集数据信息
    CONTROL_DATA(2001, "COMPLETE_BACK"),
    CONTROL_DATA_SUB(2002, "SUB_BACK"),
    MULTI_CONTROL_DATA(2011, "COMPLETE_BACK"),
    MULTI_CONTROL_DATA_SUB(2012, "SUB_BACK");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    SocketMsgConst(int code, String msg){
        this.code = code;
        this.message = msg;
    }

}
