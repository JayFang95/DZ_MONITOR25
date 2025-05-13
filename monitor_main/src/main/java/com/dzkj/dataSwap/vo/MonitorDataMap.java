package com.dzkj.dataSwap.vo;

import lombok.Data;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/9 16:24
 * @description 监测数据和原始数据集合
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MonitorDataMap {

    private List<MonitorDataVO> monitorDataList;
    private List<OriOffsetInfoVO> oriDataList;
}
