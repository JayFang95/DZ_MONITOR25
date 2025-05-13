package com.dzkj.biz.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/4
 * @description 单位授权查询条件
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CompanyCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 创建时间数值
     */
    private Integer timeNum;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private Date createTime;

}
