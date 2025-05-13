package com.dzkj.controller.alarm_setting;


import com.dzkj.biz.alarm_setting.IAlarmInfoBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmDetailCond;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoCondition;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description 报警信息控制层
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class AlarmInfoController {

    @Autowired
    private IAlarmInfoBiz alarmInfoBiz;

    @RequestMapping(value = "alarm_info/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_INFO)
    public ResponseUtil page(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody AlarmInfoCondition condition){
        return ResponseUtil.success(alarmInfoBiz.page(pageIndex, pageSize, condition));
    }

    @RequestMapping(value = "alarm_info/handle", method = RequestMethod.POST)
    @SysOperateLog(value = "处理", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_INFO)
    public ResponseUtil handle(@RequestBody AlarmInfoVO data){
        return ResponseUtil.success(alarmInfoBiz.handel(data));
    }

    @RequestMapping(value = "alarm_info/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.ALARM_INFO)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return ResponseUtil.success(alarmInfoBiz.delete(id));
    }

    @RequestMapping(value = "alarm_info/detail", method = RequestMethod.POST)
    @SysOperateLog(value = "详情查看", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_INFO)
    public ResponseUtil detail(@RequestBody AlarmDetailCond cond){
        return ResponseUtil.success(alarmInfoBiz.detail(cond));
    }

}
