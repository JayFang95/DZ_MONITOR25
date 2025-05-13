package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/13
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class GroupPointVO {

    private Long id;

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 点组名
     */
    private String name;

    private List<PointVO> pointList;
    private List<PointAppVO> pointAppList;

}
