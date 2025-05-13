package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

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
public class GroupCondition {

    /**
     * 所属任务id
     */
    private List<Long> missionIds;

    private Long userId;

}
