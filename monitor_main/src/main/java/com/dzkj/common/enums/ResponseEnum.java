package com.dzkj.common.enums;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/5
 * @description 返回常数枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum ResponseEnum {

    SUCCESS(200, "操作成功"),
    FAIL(500, "服务暂不可用，请稍后再试"),
    LOGIN_PARAM_EMPTY(1000, "用户名或者密码为空"),
    LOGIN_PARAM_ERROR(1001, "用户名或者密码错误"),
    CAPTCHA_PARAM_EXPIRE(1002, "验证码已失效"),
    CAPTCHA_PARAM_ERROR(1003, "验证码错误"),
    PHONE_PARAM_ERROR(1004, "手机号匹配错误"),
    USER_ERROR(1002, "用户不存在"),
    RESET_PWD_ERROR(1003, "密码重置失败"),
    DELETE_ERROR(2001, "数据删除异常"),
    UPDATE_ERROR(2002, "数据更新异常"),
    SAVE_ERROR(2003, "数据保存异常"),
    EQUIP_OCCUPY_ERROR(3001, "设备占用中，无法创建"),
    ACCESS_DENIED_ERROR(4001, "权限不足,不允许访问"),
    COMPANY_DENIED_ERROR(5001, "公司服务已终止,请联系管理员"),
    PARAMETER_ERROR(1102, "输入参数有误"),
    FILE_EXPORT_ERROR(1202, "文件导出异常");


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

    ResponseEnum(int i, String s) {
        this.code = i;
        this.message = s;
    }
}
