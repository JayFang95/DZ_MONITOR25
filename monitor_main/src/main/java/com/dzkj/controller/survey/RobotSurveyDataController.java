package com.dzkj.controller.survey;

import com.dzkj.biz.survey.IRobotSurveyDataBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:27
 * @description 测量数据管理controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class RobotSurveyDataController {

    @Autowired
    private IRobotSurveyDataBiz surveyDataBiz;

}
