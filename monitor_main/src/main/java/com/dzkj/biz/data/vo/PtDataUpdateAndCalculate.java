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
public class PtDataUpdateAndCalculate {
    // 任务id
    private Long missionId;
    // 数据库主键id
    private Long id;
    // 测点数据
    private Long pid;
    // 更新数值
    private Double x;
    private Double y;
    private Double z;
    // 任务类型
    private Boolean isXyz;
}
