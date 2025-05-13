package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

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
public class TableDataCalculate {

    // 测点名(深度)
    private String name;
    // 状态
    private String status;
    // 初始值
    private double value0;
    // 上次值
    private double preValue;
    // 本次值
    private double value;
    // 本次变化量
    private double deltValue;
    // 时间间隔
    private double deltTime;
    // 变化速率
    private double vDeltValue;
    // 上次累计变化量
    private double totalPreValue;
    // 累计变化量
    private double totalValue;
    // 观测时间
    private Date getTime;
    // 监测控制值
    private String threshold;
    // 备注
    private String note;

}
