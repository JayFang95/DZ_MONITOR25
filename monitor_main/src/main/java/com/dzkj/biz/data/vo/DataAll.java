package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/12
 * @description 汇总信息对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DataAll {

    // 监测任务id
    private Long missionId;
    // 监测任务名称
    private String missionName;
    // 累计最大值点名
    private String ptNametTotal;
    // 速率最大值点名
    private String ptNametVdelt;
    // 最大累计变化量
    private double maxTotalNum;
    // 最大变化速率
    private String vDeltThreshold;
    // 累计变化量阈值
    private String totalThreshold;
    // 变化速率阈值
    private double maxVdeltNum;
    // 备注
    private String note;

}
