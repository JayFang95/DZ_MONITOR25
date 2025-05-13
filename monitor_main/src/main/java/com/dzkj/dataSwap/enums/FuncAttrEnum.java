package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/12/28 14:22
 * @description 监测点业务分类枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum FuncAttrEnum {

    //接口枚举
    OFFSET_INFO("位移测量", 1),
    ANGLE_INFO("角度测量", 2),
    WATER_INFO("水位测量", 3),
    QUAKE_INFO("振动测量", 4),
    AXIS_INFO("轴力测量", 5),
    DISTANCE_INFO("距离测量", 6),
    TRACK_GEO_INFO("轨道几何尺寸-直线", 10);

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

    FuncAttrEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static int getCodeByName(String name){
        for (FuncAttrEnum categoryEnum : FuncAttrEnum.values()) {
            if(categoryEnum.getName().equals(name)){
                return categoryEnum.getCode();
            }
        }
        return 1;
    }

}
