package com.dzkj.dataSwap.controller;

import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.bean.DataSwapResponse;
import com.dzkj.dataSwap.bean.ResHead;
import com.dzkj.dataSwap.biz.IDataSwapBiz;
import com.dzkj.dataSwap.vo.MonitorErrorVO;
import com.dzkj.dataSwap.vo.PushPointVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 13:39
 * @description 数据交换系统控制层
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/dz_monitor")
public class DataSwapController {

    @Autowired
    private IDataSwapBiz dataSwapBiz;

    @PostMapping("upload")
    @SysOperateLog(value = "同步到监管平台", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_DATA_SWAP)
    public ResponseUtil uploadPoint(@RequestBody List<PushPointVO> pointList){
        return dataSwapBiz.uploadPoint(pointList);
    }

    @PostMapping("queryLatestMonitorData/{missionId}")
    @SysOperateLog(value = "获取推送点最新数据", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_DATA_SWAP)
    public ResponseUtil queryLatestMonitorData(@RequestBody List<Long> pointIds,
                                               @PathVariable("missionId") Long missionId){
        return ResponseUtil.success(dataSwapBiz.queryLatestMonitorData(pointIds, missionId));
    }

    @GetMapping("checkPushTask/{missionId}/{recycleNum}")
    @SysOperateLog(value = "监测点上报检查", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_DATA_SWAP)
    public ResponseUtil checkPushTask(@PathVariable("missionId") Long missionId,
                                    @PathVariable("recycleNum") Integer recycleNum){
        return ResponseUtil.success(dataSwapBiz.checkPushTask(missionId, recycleNum));
    }

    @GetMapping("manualPushMonitorData/{missionId}/{recycleNum}/{thirdPartType}")
    @SysOperateLog(value = "手动推送最新数据", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_DATA_SWAP)
    public ResponseUtil manualPushMonitorData(@PathVariable("missionId") Long missionId,
                                      @PathVariable("recycleNum") Integer recycleNum,
                                      @PathVariable("thirdPartType") Integer thirdPartType){
        return dataSwapBiz.manualPushMonitorData(missionId, recycleNum, thirdPartType);
    }

    @GetMapping("uploadMonitorError")
    @SysOperateLog(value = "监测延测上报", type = LogConstant.UPDATE, modelName = LogConstant.SYS_DATA_SWAP)
    public ResponseUtil uploadMonitorError(@RequestBody MonitorErrorVO monitorErrorVO){
        return dataSwapBiz.uploadMonitorError(monitorErrorVO);
    }

    @PostMapping("dataSwap")
    @SysOperateLog(value = "监测数据交换", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_DATA_SWAP)
    public DataSwapResponse dataSwap(){
        DataSwapResponse response = new DataSwapResponse();
        ResHead head = new ResHead();
        head.setResult(0);
        head.setReason("待实现");
        response.setHead(head);
        response.setBody(new LinkedHashMap<>());
        return response;
    }

}
