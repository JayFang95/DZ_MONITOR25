package com.dzkj.service.survey;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.survey.vo.SurveyCycleVo;
import com.dzkj.entity.survey.SurveyCycle;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 采集周期服务组接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface ISurveyCycleService extends IService<SurveyCycle> {

    /**
     * 根据任务id查询
     *
     * @description 根据任务id查询
     * @author jing.fang
     * @date 2024/2/19 11:17
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.survey.SurveyCycle>
     **/
    List<SurveyCycle> listByMissionId(Long missionId);

    /**
     * 新增策略周期
     *
     * @description 新增策略周期
     * @author jing.fang
     * @date 2024/2/19 11:20
     * @param data 策略周期
     * @return: boolean
     **/
    boolean add(SurveyCycleVo data);

    /**
     * 修改策略周期
     *
     * @description 修改策略周期
     * @author jing.fang
     * @date 2024/2/19 11:20
     * @param data 策略周期
     * @return: boolean
     **/
    boolean edit(SurveyCycleVo data);
}
