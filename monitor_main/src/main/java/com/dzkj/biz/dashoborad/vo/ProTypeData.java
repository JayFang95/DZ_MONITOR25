package com.dzkj.biz.dashoborad.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/14
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProTypeData {

    // 类型id
    private Long id;
    // 项目类型
    private String name;
    // 项目类型数
    private int typeNum;
    // 类型报警测点数
    private int ptNum;
    // 类型报警条数
    private int alarmNum;

}
