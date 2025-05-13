package com.dzkj.dataSwap.controller;


import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskOtherBiz;
import com.dzkj.dataSwap.vo.PushTaskOtherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18
 * @description 数据推送其他controller
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PushTaskOtherController {

    @Autowired
    private IPushTaskOtherBiz pushTaskOtherBiz;

    @RequestMapping(value = "push_other/list/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询推送任务", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil list(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(pushTaskOtherBiz.queryList(companyId));
    }

    @RequestMapping(value = "push_other/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil add(@RequestBody PushTaskOtherVO data){
        return pushTaskOtherBiz.add(data);
    }

    @RequestMapping(value = "push_other/edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil edit(@RequestBody PushTaskOtherVO data){
        return pushTaskOtherBiz.edit(data);
    }

    @RequestMapping(value = "push_other/start/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "开启推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil startTask(@PathVariable("id") Long id){
        return pushTaskOtherBiz.startTask(id);
    }

    @RequestMapping(value = "push_other/stop/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "暂停推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil stopTask(@PathVariable("id") Long id){
        return pushTaskOtherBiz.stopTask(id);
    }

    @RequestMapping(value = "push_other/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_OTHER)
    public ResponseUtil deleteTask(@PathVariable("id") Long id){
        return pushTaskOtherBiz.deleteTask(id);
    }

}
