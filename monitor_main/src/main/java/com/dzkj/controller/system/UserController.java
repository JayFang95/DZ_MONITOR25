package com.dzkj.controller.system;


import com.dzkj.biz.system.IUserBiz;
import com.dzkj.biz.vo.PasswordVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 人员controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class UserController {

    @Autowired
    private IUserBiz userBiz;

    /**
     * @date 2021/8/6 9:38
     * @param companyId 登录人单位id
     * @param id 选择单位id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "user/list/{company_id}", method = RequestMethod.GET)
    @SysOperateLog(value = "列表查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_USER)
    public ResponseUtil list(@PathVariable("company_id") Long companyId, @RequestParam(required = false) Long id){
        return ResponseUtil.success(userBiz.getList(companyId, id));
    }

    @RequestMapping(value = "user/list/admin", method = RequestMethod.GET)
    @SysOperateLog(value = "超级管理员列表", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_USER)
    public ResponseUtil superList(){
        return ResponseUtil.success(userBiz.getSuperList());
    }

    /**
     * @date 2021/8/6 9:39
     * @param companyId 登录人单位id
     * @param user 用户信息
     * @return com.dzkj.common.util.ResponseUtil
    **/
    @RequestMapping(value = "user/add/{company_id}", method = RequestMethod.POST)
    @SysOperateLog(value = "新增用户", type = LogConstant.CREATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil add(@PathVariable("company_id") Long companyId, @RequestBody User user){
        return userBiz.add(user, companyId);
    }

    /**
     * @date 2021/8/6 9:39
     * @param user 用户信息
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "user/add/admin", method = RequestMethod.POST)
    @SysOperateLog(value = "新增超级管理员", type = LogConstant.CREATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil addSuper( @RequestBody User user){
        return userBiz.addSuper(user);
    }

    /**
     * @date 2021/8/6 9:39
     * @param user 用户信息
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "user/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil update(@RequestBody User user){
        return userBiz.update(user);
    }

    /**
     * @date 2021/8/6 9:39
     * @param id 用户id
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "user/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.SYS_USER)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return userBiz.delete(id);
    }

    /**
     * @date 2021/8/6 9:39
     * @param id 用户id
     * @return com.dzkj.common.util.ResponseUtil
     **/
    @RequestMapping(value = "user/password/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "重置密码", type = LogConstant.UPDATE, modelName = LogConstant.SYS_USER)
    public ResponseUtil password(@PathVariable("id") Long id){
        return userBiz.updatePassword(id);
    }

    /**
     * 更新基础信息
     */
    @RequestMapping(value = "common/user", method = RequestMethod.POST)
    public ResponseUtil updateUser(@RequestBody User user){
        return userBiz.updateUser(user);
    }

    /**
     * 更新密码信息
     */
    @RequestMapping(value = "common/password", method = RequestMethod.POST)
    public ResponseUtil updatePassword(@RequestBody PasswordVO passwordVO){
        return userBiz.updatePwd(passwordVO);
    }


    /**
     * 会话超时测试接口
     **/
    @RequestMapping(value = "user/test", method = RequestMethod.GET)
    public void testOutTime(){

    }
}
