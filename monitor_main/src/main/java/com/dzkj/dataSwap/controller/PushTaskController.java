package com.dzkj.dataSwap.controller;


import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskBiz;
import com.dzkj.dataSwap.vo.PushTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 13:39
 * @description 推送任务控制层
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PushTaskController {

    @Autowired
    private IPushTaskBiz pushTaskBiz;

    @RequestMapping(value = "pushTask/list/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询推送任务", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil list(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(pushTaskBiz.queryList(companyId));
    }

    @RequestMapping(value = "pushTask/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil add(@RequestBody PushTaskVO data){
        return pushTaskBiz.add(data);
    }

    @RequestMapping(value = "pushTask/edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil edit(@RequestBody PushTaskVO data){
        return pushTaskBiz.edit(data);
    }

    @RequestMapping(value = "pushTask/start/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "开启推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil startTask(@PathVariable("id") Long id){
        return pushTaskBiz.startTask(id);
    }

    @RequestMapping(value = "pushTask/stop/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "暂停推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil stopTask(@PathVariable("id") Long id){
        return pushTaskBiz.stopTask(id);
    }

    @RequestMapping(value = "pushTask/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil deleteTask(@PathVariable("id") Long id){
        return pushTaskBiz.deleteTask(id);
    }


}
