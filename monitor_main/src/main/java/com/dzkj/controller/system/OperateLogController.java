package com.dzkj.controller.system;


import com.dzkj.biz.system.IOPerateLogBiz;
import com.dzkj.biz.system.vo.OperateLogCondition;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/27
 * @description 日志controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/log")
public class OperateLogController {
    @Autowired
    private IOPerateLogBiz oPerateLogBiz;

    @RequestMapping(value = "page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_LOG)
    public ResponseUtil page(@PathVariable("pageIndex") int pageIndex,
                             @PathVariable("pageSize") int pageSize,
                             @RequestBody OperateLogCondition condition){
        return ResponseUtil.success(oPerateLogBiz.getPage(pageIndex, pageSize, condition));
    }
}
