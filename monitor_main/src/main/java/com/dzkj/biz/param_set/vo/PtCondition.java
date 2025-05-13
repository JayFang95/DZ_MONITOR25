package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class PtCondition {

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 点类型
     */
    private String type;

    /**
     * 测点名称
     */
    private String name;

}
