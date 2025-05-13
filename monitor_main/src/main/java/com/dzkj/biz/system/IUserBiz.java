package com.dzkj.biz.system;

import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.biz.vo.PasswordVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.User;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/5
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IUserBiz {

    /**
     * 用户列表查询
     *
     * @description 用户列表查询
     * @author jing.fang
     * @date 2021/8/6 9:17
     * @param id
     * @param companyId companyId
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getList(Long companyId, Long id);

    /**
     * 超级管理员列表查询
     *
     * @description 超级管理员列表查询
     * @author jing.fang
     * @date 2021/8/6 9:30
     * @param
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getSuperList();

    /**
     * 新增用户
     *
     * @description 新增用户
     * @author jing.fang
     * @date 2021/8/6 9:41
     * @param user user
     * @param companyId companyId
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(User user, Long companyId);

    /**
     * 新增超级管理员
     *
     * @description 新增超级管理员
     * @author jing.fang
     * @date 2021/8/6 9:41
     * @param user user
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil addSuper(User user);

    /**
     * 编辑用户信息
     *
     * @description 编辑用户信息
     * @author jing.fang
     * @date 2021/8/6 9:52
     * @param user user
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(User user);

    /**
     * 删除用户
     *
     * @description 删除用户
     * @author jing.fang
     * @date 2021/8/6 9:53
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 重置密码
     *
     * @description 重置密码
     * @author jing.fang
     * @date 2021/8/6 10:00
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updatePassword(Long id);

    /**
     * 更新用户基本信息
     *
     * @description 更新用户基本信息
     * @author jing.fang
     * @date 2022/3/2 10:10
     * @param user user
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateUser(User user);

    /**
     * 更新密码
     *
     * @description 更新密码
     * @author jing.fang
     * @date 2022/3/2 10:10
     * @param passwordVO passwordVO
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updatePwd(PasswordVO passwordVO);
}
