package com.dzkj.dataSwap.controller;


import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskCdBiz;
import com.dzkj.dataSwap.vo.PushTaskCdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21
 * @description 推送任务controller-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PushTaskCdController {

    @Autowired
    private IPushTaskCdBiz pushTaskCdBiz;

    @RequestMapping(value = "pushTaskCd/list/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询推送任务", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil list(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(pushTaskCdBiz.queryList(companyId));
    }

    @RequestMapping(value = "pushTaskCd/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil add(@RequestBody PushTaskCdVO data){
        return pushTaskCdBiz.add(data);
    }

    @RequestMapping(value = "pushTaskCd/edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil edit(@RequestBody PushTaskCdVO data){
        return pushTaskCdBiz.edit(data);
    }

    @RequestMapping(value = "pushTaskCd/start/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "开启推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil startTask(@PathVariable("id") Long id){
        return pushTaskCdBiz.startTask(id);
    }

    @RequestMapping(value = "pushTaskCd/stop/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "暂停推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil stopTask(@PathVariable("id") Long id){
        return pushTaskCdBiz.stopTask(id);
    }

    @RequestMapping(value = "pushTaskCd/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除推送任务", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH_CD)
    public ResponseUtil deleteTask(@PathVariable("id") Long id){
        return pushTaskCdBiz.deleteTask(id);
    }


}
