package com.dzkj.biz.survey.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 多站联测vo
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class RobotSurveyControlGroupVo {

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
     * 备注信息
     */
    private String note;

    /**
     * 创建人Id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 测量状态
     */
    private String surveyStatus;

    /**
     * 测站配置修改参数
     */
    private boolean surveyCfg;
    /**
     * 测站包含的控制器组
     */
    private List<String> serialNos;


}
