package com.dzkj.controller.system;


import com.dzkj.biz.system.IPresetBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.AlarmSetting;
import com.dzkj.entity.system.MonitorType;
import com.dzkj.entity.system.ProjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/17
 * @description 系统预置controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class PresetController {

    @Autowired
    private IPresetBiz presetBiz;

    @SysOperateLog(value = "修改监测类型", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/monitor/update", method = RequestMethod.POST)
    public ResponseUtil updateMonitor(@RequestBody MonitorType monitorType){
        return presetBiz.updateMonitorType(monitorType);
    }

    /**
     * @param id id 上移对象id
     * @param toId toId 上移替换的对象id
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @SysOperateLog(value = "上移监测类型", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/monitor/up/{id}/{toId}", method = RequestMethod.POST)
    public ResponseUtil upMonitor(@PathVariable("id") Long id, @PathVariable("toId") Long toId){
        return presetBiz.updateMonitorIndex(id, toId);
    }

    /**
     * @param id id 下移对象id
     * @param toId toId 下移替换的对象id
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @SysOperateLog(value = "下移监测类型", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/monitor/down/{id}/{toId}", method = RequestMethod.POST)
    public ResponseUtil downMonitor(@PathVariable("id") Long id, @PathVariable("toId") Long toId){
        return presetBiz.updateMonitorIndex(id, toId);
    }

    @SysOperateLog(value = "新增项目类型", type = LogConstant.CREATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/project/add", method = RequestMethod.POST)
    public ResponseUtil addProject(@RequestBody ProjectType projectType){
        return presetBiz.saveProject(projectType);
    }

    @SysOperateLog(value = "修改项目类型", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/project/update", method = RequestMethod.POST)
    public ResponseUtil updateProject(@RequestBody ProjectType projectType){
        return presetBiz.saveProject(projectType);
    }

    @SysOperateLog(value = "删除项目类型", type = LogConstant.DELETE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/project/delete/{id}", method = RequestMethod.DELETE)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return presetBiz.deleteProject(id);
    }

    @SysOperateLog(value = "更新报警信息", type = LogConstant.UPDATE, modelName = LogConstant.SYS_PAGE)
    @RequestMapping(value = "preset/alarm/update", method = RequestMethod.POST)
    public ResponseUtil updateAlarm(@RequestBody List<AlarmSetting> list){
        return presetBiz.updateAlarm(list);
    }

    /**
     * 查询监测类型大类下拉
     */
    @RequestMapping(value = "common/monitor/drop", method = RequestMethod.GET)
    public ResponseUtil getMonitorType(){
        return ResponseUtil.success(presetBiz.getMonitorDrop());
    }

    /**
     * 查询监测类型子类集合
     */
    @RequestMapping(value = "common/monitor/list", method = RequestMethod.GET)
    public ResponseUtil getMonitorList(){
        return ResponseUtil.success(presetBiz.getMonitorList());
    }

    /**
     * 查询报警等级集合
     */
    @RequestMapping(value = "common/alarm_level/list", method = RequestMethod.GET)
    public ResponseUtil getAlarmLevelList(){
        return ResponseUtil.success(presetBiz.getAlarmLevelList());
    }

}
