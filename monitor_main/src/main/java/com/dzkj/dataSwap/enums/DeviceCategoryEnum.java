package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/12/28 14:22
 * @description 监测设备分类枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum DeviceCategoryEnum {

    //接口枚举
    QZY("全站仪", 1),
    J_LI_SZY("静力水准仪", 2),
    JWY("经纬仪", 3),
    WX_DW("卫星定位", 4),
    QJY("倾角仪", 5),
    CZY("测振仪", 6),
    SWJ("水位计", 7),
    ZLJ("轴力计", 8),
    WYJ("位移计", 9),
    QFC("千分尺", 10),
    YB_KC("游标卡尺", 11),
    LF_JCY("裂缝监测仪", 12),
    MSJ("锚索计", 13),
    OTHER("其他", 99);

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

    DeviceCategoryEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static int getCodeByName(String name){
        for (DeviceCategoryEnum categoryEnum : DeviceCategoryEnum.values()) {
            if(categoryEnum.getName().equals(name)){
                return categoryEnum.getCode();
            }
        }
        return 1;
    }

}
