package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/12/28 14:22
 * @description 监测目标大分类枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum MainCategoryEnum {

    //接口枚举
    XS_TARGET("线上目标 ", 1),
    XX_TARGET("线下目标 ", 2),
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

    MainCategoryEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static int getCodeByName(String name){
        for (MainCategoryEnum categoryEnum : MainCategoryEnum.values()) {
            if(categoryEnum.getName().equals(name)){
                return categoryEnum.getCode();
            }
        }
        return 1;
    }

}
