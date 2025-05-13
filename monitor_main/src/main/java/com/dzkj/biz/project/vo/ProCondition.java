package com.dzkj.biz.project.vo;

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
 * @date 2021/9/2
 * @description 项目信息查询条件对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProCondition {

    private Long companyId;
    /**
     * 可以访问项目id集合
     */
    private List<Long> projectIds;
    /**
     * 项目名称
     */
    private String name;
    /**
     * 项目类型
     */
    private Long typeId;
    /**
     * 是否结束
     */
    private Boolean finished;
    /**
     * 创建时间数值
     */
    private Integer timeNum;
    /**
     * 创建时间
     */
    private Date createTime;

}
