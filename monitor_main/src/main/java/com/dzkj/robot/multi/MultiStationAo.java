package com.dzkj.robot.multi;

import com.dzkj.robot.survey.MultiSurveyBiz;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/29 9:25
 * @description 多站Ao
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Getter
@Setter
public class MultiStationAo {

    private Long id;

    /**
     * 多站联测名
     */
    private String name;

    /**
     * 监测任务ID
     */
    private Long missionId;

    /**
     * 参数
     */
    private String params;

    /**
     * 测量状态
     */
    private String surveyStatus;

    /**
     * 测量状态：0-停测；1-测量中
     */
    private int survey;

    private MultiSurveyBiz multiSurveyBiz;

}
