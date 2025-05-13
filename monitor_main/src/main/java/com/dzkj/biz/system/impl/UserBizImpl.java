package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.system.IUserBiz;
import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.biz.vo.CreateMemberVO;
import com.dzkj.biz.vo.PasswordVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.QwUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.User;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IRoleService;
import com.dzkj.service.system.IUserGroupService;
import com.dzkj.service.system.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/5
 * @description 用户业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class UserBizImpl implements IUserBiz {

    @Autowired
    private IUserService userService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private QwUtil qwUtil;

    @Override
    public List<UserVO> getList(Long companyId, Long id) {
        if(id != null){
            companyId = id;
        }
        return userService.getList(companyId);
    }

    @Override
    public List<UserVO> getSuperList() {
        return userService.getSuperList();
    }

    @Override
    public ResponseUtil add(User user, Long companyId) {
        if(companyId == 0){
            companyId = companyService.getCurrentCompany();
            if(companyId==-1){
                return ResponseUtil.failure(500, "无法添加用户,请先创建单位");
            }
        }
        if(userService.checkNameOrPhone(null, user.getUsername(), null)){
            return ResponseUtil.failure(500, "用户名[" + user.getUsername() + "]已存在");
        }
        if(StringUtils.isNotEmpty(user.getPhone())){
            List<User> users = userService.findByIdAndPhone(user);
            if (users.size() > 0){
                return ResponseUtil.failure(500, "手机号与[" + users.get(0).getCompanyName()
                        + "]的[" + users.get(0).getUsername() + "]账号重复");
            }
        }
        user.setCompanyId(companyId)
                .setPassword(new BCryptPasswordEncoder().encode("888888"))
                .setDeleteFlg(true);
        /**
         * 判断手机号是否存在通讯录中
         */
        boolean flag = false;
        if (StringUtils.isNotEmpty(user.getPhone())){
            String userId = qwUtil.getUserId(user.getPhone());
            if (userId == null){
                user.setAppId(user.getUsername()+System.currentTimeMillis());
            }else {
                flag = true;
                user.setAppId(userId);
            }
        }
        boolean b = userService.save(user);
        if (b && !flag && StringUtils.isNotEmpty(user.getPhone())){
            createWxMember(user);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil addSuper(User user) {
        if(userService.checkNameOrPhone(null, user.getUsername(), null)){
            return ResponseUtil.failure(500, "用户名[" + user.getUsername() + "]已存在");
        }
        if(StringUtils.isNotEmpty(user.getPhone())){
            List<User> users = userService.findByIdAndPhone(user);
            if (users.size() > 0){
                return ResponseUtil.failure(500, "手机号与[" + users.get(0).getCompanyName()
                        + "]的[" + users.get(0).getUsername() + "]账号重复");
            }
        }
        user.setRoleId(roleService.getSuperRoleId())
                .setCompanyId(0L)
                .setPassword(new BCryptPasswordEncoder().encode("888888"))
                .setDeleteFlg(true);
        boolean b = userService.save(user);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(User user) {
        if(userService.checkNameOrPhone(user.getId(), user.getUsername(), null)){
            return ResponseUtil.failure(500, "用户名[" + user.getUsername() + "]已存在");
        }

        return updateUser(user);
    }

    @Override
    public ResponseUtil delete(Long id) {
        User user = userService.getById(id);
        if(!user.getDeleteFlg()){
            return ResponseUtil.failure(500, "无法删除当前用户");
        }
        if(0L == user.getCompanyId() && userService.countSuper()==1){
            return ResponseUtil.failure(500, "超级管理员不能为空");
        }
        boolean b = userService.removeById(id);
        if (b){
            userGroupService.removeByUserIds(Collections.singletonList(id));
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil updatePassword(Long id) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id)
                .set(User::getPassword, new BCryptPasswordEncoder().encode("888888"));
        boolean b = userService.update(wrapper);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil updateUser(User user) {
        User oldUser = userService.getById(user.getId());
        if(StringUtils.isNotEmpty(user.getPhone())){
            List<User> users = userService.findByIdAndPhone(user);
            if (users.size() > 0){
                return ResponseUtil.failure(500, "手机号与[" + users.get(0).getCompanyName()
                        + "]的[" + users.get(0).getUsername() + "]账号重复");
            }
        }
        boolean flag = false;
        if (user.getCompanyId()!=0){
            /**
             * 手机号修改且不为空
            **/
            if (!Objects.equals(user.getPhone(), oldUser.getPhone())){
                if(StringUtils.isNotEmpty(user.getPhone())){
                    String userId = qwUtil.getUserId(user.getPhone());
                    if (userId == null){
                        user.setAppId(user.getUsername() + System.currentTimeMillis());
                    }else {
                        flag = true;
                        user.setAppId(userId);
                    }
                }else {
                    user.setAppId("");
                }
            }
        }
        boolean b = userService.updateById(user);
        if (b && !flag && user.getCompanyId()!=0){
            if (!Objects.equals(user.getPhone(), oldUser.getPhone()) && StringUtils.isNotEmpty(user.getPhone())){
                createWxMember(user);
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil updatePwd(PasswordVO passwordVO) {
        User user = userService.getById(passwordVO.getId());
        if(user==null){
            return ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(passwordVO.getPassword(), user.getPassword())){
            return ResponseUtil.failure(500, "原密码输入错误");
        }
        user.setPassword(encoder.encode(passwordVO.getNewPassword()));
        boolean b = userService.updateById(user);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    /**
     * 添加企业微信成员
     * @param user user
     */
    private void createWxMember(User user) {
        CreateMemberVO userVO = new CreateMemberVO();
        userVO.setName(StringUtils.isNotEmpty(user.getName()) ? user.getName() : user.getUsername())
                .setAlias(user.getUsername())
                .setMobile(user.getPhone())
                .setUserid(user.getAppId());
        qwUtil.createUser(userVO);
    }

    /**
     * 添加企业微信成员
     * @param user user
     * @param oldUser oldUser
     */
    private void updateWxMember(User user, User oldUser) {
        if (StringUtils.isNotEmpty(oldUser.getAppId())){
            qwUtil.deleteUser(oldUser.getAppId());
        }
        if (StringUtils.isNotEmpty(user.getPhone())){
            createWxMember(user);
        }
    }

}
