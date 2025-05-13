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
public class CustomCondition {

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 自定义标题
     */
    private String systemTitle;

    /**
     * 显示标题
     */
    private String showTitle;

}
