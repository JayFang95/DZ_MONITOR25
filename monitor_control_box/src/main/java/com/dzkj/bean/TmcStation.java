package com.dzkj.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 测站坐标
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TmcStation {

    /**
     * 测站坐标E0[m]
     */
    private double dE0;
    /**
     * 测站坐标N0[m]
     */
    private double dN0;
    /**
     * 测站坐标H0[m]
     */
    private double dH0;
    /**
     * 仪器高[m]
     */
    private double dHi;

}
