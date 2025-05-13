package com.dzkj.biz.survey;

import com.dzkj.biz.survey.vo.SurveyCycleVo;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/19 9:56
 * @description 测量策略业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface ISurveyCycleBiz {

    /**
     * 查询列表
     *
     * @description 查询列表
     * @author jing.fang
     * @date 2024/2/19 10:33
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.biz.survey.vo.SurveyCycleVo>
     **/
    List<SurveyCycleVo> getList(Long missionId);


    /**
     * 新增策略周期
     *
     * @description 新增策略周期
     * @author jing.fang
     * @date 2024/2/19 10:34
     * @param data 策略属性
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(SurveyCycleVo data);

    /**
     * 修改策略周期
     *
     * @description 修改策略周期
     * @author jing.fang
     * @date 2024/2/19 10:34
     * @param data 策略属性
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(SurveyCycleVo data);

    /**
     * 删除测量周期
     *
     * @description 删除测量周期
     * @author jing.fang
     * @date 2024/2/19 10:35
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil delete(Long id);
}
