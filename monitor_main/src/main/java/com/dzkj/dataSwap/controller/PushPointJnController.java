package com.dzkj.dataSwap.controller;


import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushPointJnBiz;
import com.dzkj.dataSwap.vo.PushPointJnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/9
 * @description 推送测点controller-济南局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt/common/pushPointJn")
public class PushPointJnController {

    @Autowired
    private IPushPointJnBiz pushPointBiz;

    @RequestMapping(value = "list/{taskId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询推送详情", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil list(@PathVariable("taskId") Long taskId){
        return ResponseUtil.success(pushPointBiz.queryList(taskId));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送点", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil add(@RequestBody PushPointJnVO data){
        return pushPointBiz.add(data);
    }

    @RequestMapping(value = "addBatch", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送点(批量)", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil addBatch(@RequestBody List<PushPointJnVO> list){
        return pushPointBiz.addBatch(list);
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改推送点", type = LogConstant.UPDATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil edit(@RequestBody PushPointJnVO data){
        return pushPointBiz.edit(data);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除推送点", type = LogConstant.DELETE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return pushPointBiz.delete(id);
    }

    @RequestMapping(value = "list/point/{missionId}/{pushTaskId}/{pointId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询任务可选推送点", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil listPoint(@PathVariable("missionId") Long missionId,
                                  @PathVariable("pushTaskId") Long pushTaskId,
                                  @PathVariable("pointId") Long pointId){
        return ResponseUtil.success(pushPointBiz.listPoint(missionId, pushTaskId, pointId));
    }

}
