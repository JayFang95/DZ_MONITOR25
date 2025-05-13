package com.dzkj.controller.survey;


import com.dzkj.biz.survey.IRobotSurveyControlGroupBiz;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 监测控制组controller
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class RobotSurveyControlGroupController {

    @Autowired
    private IRobotSurveyControlGroupBiz controlGroupBiz;

    @RequestMapping(value = "common/control-group/list/{missionId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询多站联测列表", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil getList(@PathVariable Long missionId){
        return ResponseUtil.success(controlGroupBiz.getList(missionId));
    }

    @RequestMapping(value = "common/control-group/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增多站联测", type = LogConstant.CREATE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil addCycle(@RequestBody RobotSurveyControlGroupVo data){
        return controlGroupBiz.add(data);
    }

    @RequestMapping(value = "common/control-group/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改多站联测", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil updateCycle(@RequestBody RobotSurveyControlGroupVo data){
        return controlGroupBiz.update(data);
    }

    @RequestMapping(value = "common/control-group/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除多站联测", type = LogConstant.DELETE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil deleteCycle(@PathVariable Long id){
        return controlGroupBiz.delete(id);
    }

    @RequestMapping(value = "common/control-group/survey-info/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取多站联测状态信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil getSurveyStatusInfo(@PathVariable Long id){
        return controlGroupBiz.getMultiSurveyStatusInfo(id);
    }

    @RequestMapping(value = "common/control-group/tree", method = RequestMethod.POST)
    @SysOperateLog(value = "获取任务下多站测站树形数据", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil getStationTreeData(@RequestBody RobotSurveyControlGroupVo data){
        return ResponseUtil.success(controlGroupBiz.getStationTreeData(data));
    }

    @RequestMapping(value = "common/control-group/start/{multiStationId}", method = RequestMethod.GET)
    @SysOperateLog(value = "多站联测开测", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil start(@PathVariable Long multiStationId){
        return controlGroupBiz.startMultiSurvey(multiStationId);
    }

    @RequestMapping(value = "common/control-group/startOnce/{multiStationId}", method = RequestMethod.GET)
    @SysOperateLog(value = "多站联测临时加测", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil startOnce(@PathVariable Long multiStationId){
        return controlGroupBiz.startOnceMultiSurvey(multiStationId);
    }

    @RequestMapping(value = "common/control-group/stop/{multiStationId}", method = RequestMethod.GET)
    @SysOperateLog(value = "多站联测停测测", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_MULTI)
    public ResponseUtil stop(@PathVariable Long multiStationId){
        return controlGroupBiz.stopMultiSurvey(multiStationId);
    }

    @RequestMapping(value = "common/control-group/info/{multiStationId}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取测量过程信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil getMultiSurveyInfo(@PathVariable Long multiStationId){
        return ResponseUtil.success(controlGroupBiz.getMultiSurveyInfo(multiStationId));
    }

    @RequestMapping(value = "common/control-group/update/status", method = RequestMethod.POST)
    @SysOperateLog(value = "获取测量过程信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil updateMultiStatus(@RequestBody List<String> serialNoList){
        return ResponseUtil.success(controlGroupBiz.updateMultiStatus(serialNoList));
    }

    @RequestMapping(value = "common/control-group/once/{multiId}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取多站加测结果", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil getMultiOnceResult(@PathVariable Long multiId){
        return ResponseUtil.success(controlGroupBiz.getMultiOnceResult(multiId));
    }


}
