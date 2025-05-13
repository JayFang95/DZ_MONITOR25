package com.dzkj.dataSwap.utils;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/7 17:06
 * @description 角度装换工具
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class AngleUtil {

    /**
     * D.mmss字符串格式串转为秒值 int
     * @return 秒值
     */
    public static int transDmsTos(String dms){
        dms = String.format("%.4f",Double.parseDouble(dms));
        String[] split = dms.split("\\.");
        return Integer.parseInt(split[0]) * 3600
                + Integer.parseInt(split[1].substring(0, 2)) * 60
                + Integer.parseInt(split[1].substring(2, 4));

    }

}
