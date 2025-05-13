package com.dzkj.controller.system;


import com.dzkj.biz.system.IResourceBiz;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 资源controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/resource")
public class ResourceController {
    @Autowired
    private IResourceBiz resourceBiz;

    /**
     * @date 2021/8/19 11:00
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_RESOURCE)
    public ResponseUtil list(@RequestBody ResourceCondition condition){
        return ResponseUtil.success(resourceBiz.getList(condition));
    }


    /**
     * @date 2021/8/19 11:00
     * @param resourc 编辑信息
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.SYS_RESOURCE)
    public ResponseUtil update(@RequestBody Resource resourc){
        return resourceBiz.updateResource(resourc);
    }

}
