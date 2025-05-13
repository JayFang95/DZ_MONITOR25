package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class TypeZlVO {

    private Long id;

    /**
     * 所属项目id
     */
    private Long projectId;

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 支撑类型
     */
    private String type;

    /**
     * 混凝土参数A1
     */
    private Double a;

    /**
     * 混凝土参数ES
     */
    private Long es;

    /**
     * 混凝土参数ACEC_ASES
     */
    private Long acecAses;

    /**
     * 钢支撑参数外径
     */
    private Integer r;

    /**
     * 钢支撑参数壁厚
     */
    private Integer th;

    /**
     * 创建时间
     */
    private Date createTime;

}
