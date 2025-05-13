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
 * @date 2021/9/7
 * @description 测点VO
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PointAppVO {

    private Long id;

    /**
     * 所属编组id
     */
    private Long ptGroupId;

    /**
     * 测点名
     */
    private String name;

    /**
     * 测点类型
     */
    private String type;

    /**
     * 是否停测
     */
    private Boolean stop;

    private Double x;

    private Double y;

    private Double z;

    private Boolean firstData;

}
