package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/12
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MissionDataCondition {
    // 测点id集合
    private List<Long> pidList;
    // 开始日期
    private Date startDate;
    // 截止日期
    private Date endDate;
    // 是否超限
    private Boolean overLimit;
    // 是否是三维位移
    private Boolean isXyz;
    // 是否是水平位移分层
    private Boolean isFc;

    // 2024/11/20 数据库表来源: 1-原表  2-同步表
    private Integer dbSource;

    /**
     * 是否多周期查询
     */
    private Boolean isMultiCycle;
    /**
     * 周期数
     */
    private Integer cycleNum;
}
