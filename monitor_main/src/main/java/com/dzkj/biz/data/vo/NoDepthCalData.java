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
 * @description 非水平分层任务计算对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NoDepthCalData {

    // 点名
    private String ptName;
    // 初始值
    private double num0;
    // 本次增量
    private double deltNum;
    // 本次累计
    private double totalNum;
    // 变化速率
    private double vDeltNum;
    // 监测告警值(累计和变化速率阈值)
    private String threshold;
    // 备注
    private String note;

}
