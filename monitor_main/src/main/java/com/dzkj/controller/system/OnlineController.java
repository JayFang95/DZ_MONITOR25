package com.dzkj.controller.system;


import com.dzkj.biz.system.IOnlineBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description 在线情况controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/online")
public class OnlineController {

    @Autowired
    private IOnlineBiz iOnlineBiz;

    /**
     * @author wangy
     * @date 2021-08-26 14:16
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "list/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_ONLINE)
    public ResponseUtil getOnLines(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(iOnlineBiz.getOnLines(companyId));
    }

}
