package com.dzkj.biz.survey.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 采集周期vo
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class SurveyCycleVo {

    private Long id;

    /**
     * 监测任务id
     */
    private Long missionId;

    /**
     * 组名
     */
    private String name;

    /**
     * 参数信息
     */
    private String params;

    /**
     * 创建人Id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;


}
