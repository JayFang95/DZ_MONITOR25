package com.dzkj.controller.system;


import com.dzkj.biz.system.IGroupBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 分组controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class GroupController {

    @Autowired
    private IGroupBiz groupBiz;

    /**
     * @date 2021/8/6 10:59
     * @param companyId 选择单位id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/list/{company_id}", method = RequestMethod.GET)
    @SysOperateLog(value = "工作组查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_USER)
    public ResponseUtil list(@PathVariable("company_id") Long companyId){
        return ResponseUtil.success(groupBiz.getList(companyId));
    }

    /**
     * @date 2021/8/6 10:59
     * @param companyId 选择单位id
     * @param groups 工作组信息
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/add/{company_id}", method = RequestMethod.POST)
    @SysOperateLog(value = "新增工作组", type = LogConstant.CREATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil add(@PathVariable("company_id")Long companyId, @RequestBody Groups groups){
        return groupBiz.add(companyId, groups);
    }

    /**
     * @date 2021/8/6 11:01
     * @param groups 工作组信息
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑工作组", type = LogConstant.UPDATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil update(@RequestBody Groups groups){
        return groupBiz.update(groups);
    }

    /**
     * @date 2021/8/6 11:04
     * @param id 工作组id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "解除工作组", type = LogConstant.DELETE, modelName = LogConstant.SYS_USER)
    public ResponseUtil delete(@PathVariable("id")Long id){
        return groupBiz.delete(id);
    }

    /**
     * @date 2021/8/6 11:20
     * @param id 工作组id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/list/user/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询工作组员", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_USER)
    public ResponseUtil listUser(@PathVariable("id")Long id){
        return ResponseUtil.success(groupBiz.getListUser(id));
    }

    /**
     * @date 2021/8/6 13:21
     * @param id 管理工作组id
     * @param userIds 选择的人员id 集合
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "group/update/user/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "管理工作组员", type = LogConstant.UPDATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil updateUserList(@PathVariable("id")Long id, @RequestBody List<Long> userIds){
        return ResponseUtil.success(groupBiz.updateUserList(id, userIds));
    }

    /**
     * 人员配置工作组集合
     */
    @RequestMapping(value = "common/user/group/list", method = RequestMethod.POST)
    public ResponseUtil getUserGroupListIn(@RequestBody List<Long> ids){
        return ResponseUtil.success(groupBiz.getUserGroupListIn(ids));
    }

    /**
     * 单位人员配置工作组集合
     */
    @RequestMapping(value = "common/user/group/{companyId}", method = RequestMethod.GET)
    public ResponseUtil getUserGroupList(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(groupBiz.getUserGroupList(companyId));
    }

}
