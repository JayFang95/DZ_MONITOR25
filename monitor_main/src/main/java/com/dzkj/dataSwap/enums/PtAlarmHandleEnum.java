package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/12/28 14:22
 * @description 监测点报警上传处理枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum PtAlarmHandleEnum {

    //接口枚举
    NO_UPLOAD("不上传 ", 1),
    CURRENT_UPLOAD("上传本次数据 ", 2),
    LAST_UPLOAD("上传上次未报警数据", 3);

    private String name;
    private int code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    PtAlarmHandleEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static int getCodeByName(String name){
        for (PtAlarmHandleEnum categoryEnum : PtAlarmHandleEnum.values()) {
            if(categoryEnum.getName().equals(name)){
                return categoryEnum.getCode();
            }
        }
        return 1;
    }

}
