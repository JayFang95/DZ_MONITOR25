package com.dzkj.controller.alarm_setting;


import com.dzkj.biz.alarm_setting.IAlarmItemBiz;
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
 * @description 告警项controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class AlarmItemController {

    @Autowired
    private IAlarmItemBiz iAlarmItemBiz;

    @RequestMapping(value = "alarm_item/list/{groupId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil list(@PathVariable("groupId") Long groupId){
        return ResponseUtil.success(iAlarmItemBiz.list(groupId));
    }

    /**
     * @date 2021/9/10 11:00
     * @param alarmItems 新增/导入
     **/
    @RequestMapping(value = "alarm_item/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil add(@RequestBody List<AlarmItemVO> alarmItems){
        return iAlarmItemBiz.add(alarmItems);
    }

    /**
     * @date 2021/9/10 11:00
     * @param alarmItem 编辑
     **/
    @RequestMapping(value = "alarm_item/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil update(@RequestBody AlarmItemVO alarmItem){
        return iAlarmItemBiz.update(alarmItem);
    }

    /**
     * @date 2021/9/10 11:00
     * @param id id
     **/
    @RequestMapping(value = "alarm_item/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil update(@PathVariable("id") Long id){
        return iAlarmItemBiz.delete(id);
    }
}
