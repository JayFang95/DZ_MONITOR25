package com.dzkj.dataSwap.controller;


import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushPointBiz;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.dataSwap.vo.PushUploadParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 13:39
 * @description 推送测点控制层
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/pushPoint")
public class PushPointController {

    @Autowired
    private IPushPointBiz pushPointBiz;

    @RequestMapping(value = "list/{taskId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询推送详情", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil list(@PathVariable("taskId") Long taskId){
        return ResponseUtil.success(pushPointBiz.queryList(taskId));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增推送点", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil add(@RequestBody PushPointVO data){
        return pushPointBiz.add(data);
    }

    @RequestMapping(value = "edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改推送点", type = LogConstant.UPDATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil edit(@RequestBody PushPointVO data){
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

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @SysOperateLog(value = "批量新增推送点", type = LogConstant.CREATE, modelName = LogConstant.DATA_PUSH)
    public ResponseUtil uploadBatch(PushUploadParam uploadParam, MultipartFile file){
        return pushPointBiz.uploadBatch(uploadParam, file);
    }

    @RequestMapping(value = "download/{pushTaskId}/{missionId}", method = RequestMethod.GET)
    @SysOperateLog(value = "模板下载", type = LogConstant.CREATE, modelName = LogConstant.DATA_UPLOAD)
    public void download(@PathVariable("pushTaskId")Long pushTaskId,
                         @PathVariable("missionId")Long missionId,
                         HttpServletResponse response){
        pushPointBiz.download(pushTaskId, missionId, response);
    }


}
