package com.dzkj.controller.alarm_setting;


import com.dzkj.biz.alarm_setting.IDistributeBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmDistributeVO;
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
 * @description 报警分发controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class AlarmDistributeController {

    @Autowired
    private IDistributeBiz iDistributeBiz;

    @RequestMapping(value = "distribute/list/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.ALARM_DISTRIBUTE)
    public ResponseUtil list(@PathVariable("pi") Integer pi,
                             @PathVariable("ps") Integer ps,
                             @RequestBody List<Long> projectIds){
        return ResponseUtil.success(iDistributeBiz.list(pi, ps, projectIds));
    }

    @RequestMapping(value = "distribute/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.ALARM_DISTRIBUTE)
    public ResponseUtil add(@RequestBody AlarmDistributeVO distribute){
        return iDistributeBiz.add(distribute);
    }

    @RequestMapping(value = "distribute/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_DISTRIBUTE)
    public ResponseUtil update(@RequestBody AlarmDistributeVO distribute){
        return iDistributeBiz.update(distribute);
    }

    @RequestMapping(value = "distribute/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_DISTRIBUTE)
    public ResponseUtil update(@PathVariable("id") Long id){
        return iDistributeBiz.delete(id);
    }

    @RequestMapping(value = "distribute/import", method = RequestMethod.POST)
    @SysOperateLog(value = "导入规则", type = LogConstant.UPDATE, modelName = LogConstant.ALARM_LEVEL)
    public ResponseUtil importDistribute(@RequestBody List<AlarmDistributeVO> list){
        return iDistributeBiz.importDistribute(list);
    }

    /**
     * 查询分发规则列表
     */
    @RequestMapping(value = "common/distribute/{projectId}", method = RequestMethod.GET)
    public ResponseUtil getAlarmDistributeList(@PathVariable("projectId") Long projectId){
        return ResponseUtil.success(iDistributeBiz.getAlarmDistributeList(projectId));
    }

}
