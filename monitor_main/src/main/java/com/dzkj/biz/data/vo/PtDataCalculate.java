package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
public class PtDataCalculate {
    // 任务id
    private Long missionId;
    // 测点数据
    private Long pid;
    // 更新数据
    private List<PointDataCalculate> updateList;
    // 删除数据
    private List<Long> deleteIds;
    // 任务类型
    private Boolean isXyz;
}
