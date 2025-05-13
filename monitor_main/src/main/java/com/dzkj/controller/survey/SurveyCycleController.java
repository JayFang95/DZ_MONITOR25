package com.dzkj.controller.survey;


import com.dzkj.biz.survey.ISurveyCycleBiz;
import com.dzkj.biz.survey.vo.SurveyCycleVo;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 采集周期controller
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class SurveyCycleController {

    @Autowired
    private ISurveyCycleBiz surveyCycleBiz;

    @RequestMapping(value = "common/cycle/list/{missionId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询测量策略列表", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CYCLE)
    public ResponseUtil getList(@PathVariable Long missionId){
        return ResponseUtil.success(surveyCycleBiz.getList(missionId));
    }

    @RequestMapping(value = "common/cycle/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增测量策略", type = LogConstant.CREATE, modelName = LogConstant.EQUIP_CYCLE)
    public ResponseUtil addCycle(@RequestBody SurveyCycleVo data){
        return surveyCycleBiz.add(data);
    }

    @RequestMapping(value = "common/cycle/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改测量策略", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_CYCLE)
    public ResponseUtil updateCycle(@RequestBody SurveyCycleVo data){
        return surveyCycleBiz.update(data);
    }

    @RequestMapping(value = "common/cycle/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除测量策略", type = LogConstant.DELETE, modelName = LogConstant.EQUIP_CYCLE)
    public ResponseUtil deleteCycle(@PathVariable Long id){
        return surveyCycleBiz.delete(id);
    }

}
