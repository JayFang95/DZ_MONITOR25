package com.dzkj.biz.data.vo;

import lombok.Data;
import lombok.ToString;

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
public class PtDataChartCondition {
    // 选择id集合
    private List<Long> selectIds;
    // 任务类型
    private Boolean isXyz;
}
