package com.dzkj.biz.param_set.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/21
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
public class ExportPoint {

    /**
     * 监测任务名称
     */
    private String itemName;
    /**
     * 编组名称
     */
    private String groupName;
    /**
     * 测点名称
     */
    private String ptName;
    /**
     * 测点类型
     */
    private String ptType;
    /**
     * 测点停测
     */
    private String ptStop;

}
