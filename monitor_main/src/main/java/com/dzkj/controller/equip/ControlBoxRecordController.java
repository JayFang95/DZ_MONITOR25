package com.dzkj.controller.equip;


import com.dzkj.biz.equipment.IControlBoxRecordBiz;
import com.dzkj.biz.equipment.vo.CtlBoxRecordCondition;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13
 * @description 控制器上下线
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/ctlBox")
public class ControlBoxRecordController {

    @Autowired
    private IControlBoxRecordBiz controlBoxRecordBiz;

    @RequestMapping(value = "page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询控制器上下线记录", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_RECORD)
    public ResponseUtil page(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody CtlBoxRecordCondition cond){
        return ResponseUtil.success(controlBoxRecordBiz.getPage(pageIndex, pageSize, cond));
    }

}
