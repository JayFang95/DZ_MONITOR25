package com.dzkj.dataSwap.bean.data_upload;

import lombok.Data;

import java.util.List;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/11 上午9:23
 * @description 全站仪接入数据对象-济南局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class TotalStationData {

    /**
     * 测站约定KEY
     */
    private String key;
    /**
     * 测站接口访问KEY
     */
    private String secondkey;
    /**
     * 测量时间	日期格式(yyyy-MM-dd HH:mm:ss)
     */
    private String meastime;
    /**
     * 签名
     * 签名生成规则如下：
     * key
     * secondkey
     * meastime
     * signature=MD5(<key>+< secondkey> +< meastime>)
     */
    private String signature;

    /**
     * 测点数据集合
     */
    private List<PointData> datalist;

}
