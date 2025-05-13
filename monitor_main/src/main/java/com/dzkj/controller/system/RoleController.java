package com.dzkj.controller.system;


import com.dzkj.biz.system.IRoleBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 角色controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class RoleController {

    @Autowired
    private IRoleBiz roleBiz;

    /**
     * @date 2021/8/9 11:00
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "role/list", method = RequestMethod.GET)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil list(){
        return ResponseUtil.success(roleBiz.getList());
    }

    /**
     * @date 2021/8/9 11:00
     * @param role 新增角色信息
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "role/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil add(@RequestBody Role role){
        return roleBiz.addRole(role);
    }

    /**
     * @date 2021/8/9 11:00
     * @param role 编辑角色信息
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "role/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil update(@RequestBody Role role){
        return roleBiz.updateRole(role);
    }

    /**
     * @date 2021/8/9 11:00
     * @param id 角色id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "role/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.UPDATE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil update(@PathVariable("id") Long id){
        return roleBiz.deleteRole(id);
    }

    /**
     * @date 2021/8/9 11:00
     * @param id 角色id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "role/tree/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询角色菜单", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil getTree(@PathVariable("id") Long id){
        return ResponseUtil.success(roleBiz.getTree(id));
    }

    /**
     * @date 2021/8/9 11:01
     * @param id 角色id
     * @param menuIds 选择的菜单id集合
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "role/tree/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "角色授权", type = LogConstant.UPDATE, modelName = LogConstant.SYS_ROLE)
    public ResponseUtil updateTree(@PathVariable Long id,
                                   @RequestBody List<Long> menuIds){
        return ResponseUtil.success(roleBiz.updateTree(id, menuIds));
    }

    /**
     * 查询角色下拉集合
     */
    @RequestMapping(value = "common/role", method = RequestMethod.GET)
    public ResponseUtil getRoleList(){
        return ResponseUtil.success(roleBiz.getRoleDrop());
    }

}
