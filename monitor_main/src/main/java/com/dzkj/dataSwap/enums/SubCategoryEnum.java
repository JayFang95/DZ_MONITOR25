package com.dzkj.dataSwap.enums;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/12/28 14:22
 * @description 监测目标小分类枚举
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public enum SubCategoryEnum {

    //接口枚举
    GD("轨道", 1),
    QD("桥墩", 2),
    LJ("路基", 3),
    SD_JG("隧道结构", 4),
    KJQ("框架桥", 5),
    LZ("立柱 ", 6),
    JK("基坑 ", 7),
    DQ_QD("挡墙墙顶 ", 8),
    HD("涵洞 ", 9),
    QL("桥梁 ", 10),
    ZT("站台 ", 11),
    ZF("站房 ", 12),
    DD("地道 ", 13),
    TQ("天桥 ", 14),
    JG_LF("结构裂缝 ", 15),
    GG("钢轨 ", 16),
    GD_JH_CC("轨道几何尺寸 ", 17),
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

    SubCategoryEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static int getCodeByName(String name){
        for (SubCategoryEnum categoryEnum : SubCategoryEnum.values()) {
            if(categoryEnum.getName().equals(name)){
                return categoryEnum.getCode();
            }
        }
        return 1;
    }

}
