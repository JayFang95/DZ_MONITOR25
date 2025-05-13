package com.dzkj.controller.alarm_setting;


import com.dzkj.biz.alarm_setting.IAlarmGroupBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmGroupVO;
import com.dzkj.biz.alarm_setting.vo.AlarmItemVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 告警组controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class AlarmGroupController {

    @Autowired
    private IAlarmGroupBiz iAlarmGroupBiz;

    @RequestMapping(value = "alarm_group/list/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil list(@PathVariable("pi") Integer pi,
                             @PathVariable("ps") Integer ps,
                             @RequestBody List<Long> projectIds){
        return ResponseUtil.success(iAlarmGroupBiz.list(pi, ps, projectIds));
    }

    @RequestMapping(value = "alarm_group/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil add(@RequestBody AlarmGroupVO alarmGroup){
        return iAlarmGroupBiz.add(alarmGroup);
    }

    @RequestMapping(value = "alarm_group/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil update(@RequestBody AlarmGroupVO alarmGroup){
        return iAlarmGroupBiz.update(alarmGroup);
    }

    @RequestMapping(value = "alarm_group/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil update(@PathVariable("id") Long id){
        return iAlarmGroupBiz.delete(id);
    }

    @RequestMapping(value = "alarm_group/export/{projectId}", method = RequestMethod.GET)
    @SysOperateLog(value = "导出报警组", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil exportGroup(@PathVariable("projectId") Long projectId){
        return iAlarmGroupBiz.exportGroup(projectId);
    }

    @RequestMapping(value = "alarm_group/import", method = RequestMethod.POST)
    @SysOperateLog(value = "导入报警组", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil importGroup(@RequestBody List<AlarmItemVO> itemList){
        return iAlarmGroupBiz.importGroup(itemList);
    }

    /**
     * 查询报警组集合
     */
    @RequestMapping(value = "common/alarm_group/{projectId}", method = RequestMethod.GET)
    public ResponseUtil getAlarmItemList(@PathVariable("projectId") Long projectId){
        return ResponseUtil.success(iAlarmGroupBiz.getAlarmGroupList(projectId));
    }

}
