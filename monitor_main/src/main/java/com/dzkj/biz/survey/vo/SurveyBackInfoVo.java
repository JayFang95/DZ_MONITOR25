package com.dzkj.biz.survey.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:25
 * @description 测量过程信息
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class SurveyBackInfoVo {

    /**
     * 测量状态
    **/
    private String surveyStatus;
    /**
     * 是否开始测量
     **/
    private boolean startSurveying;
    /**
     * 测量过程状态信息
     **/
    private String statusInfo;
    /**
     * 测量过程回传信息
     **/
    private List<String> backInfos;

}
