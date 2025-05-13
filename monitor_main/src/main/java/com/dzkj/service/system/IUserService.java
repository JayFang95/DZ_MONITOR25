package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.entity.system.User;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 人员service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IUserService extends IService<User> {

    User findByUsername(String username);

    User findByPhone(String phone);

    /**
     * 超级超级管理员列表
     *
     * @description 超级超级管理员列表
     * @author jing.fang
     * @date 2021/9/3 16:33
     * @param companyId companyId
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getList(Long companyId);

    /**
     * 查询工作组人员列表
     *
     * @description
     * @author jing.fang
     * @date 2021/9/3 16:34
     * @param companyId companyId
     * @param id id
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getListUser(Long companyId, Long id);

    /**
     * 查询人员名称/手机号
     *
     * @description 查询人员名称
     * @author jing.fang
     * @date 2021/9/3 17:52
     * @param id id
     * @param username username
     * @param phone phone
     * @return java.util.List<com.dzkj.entity.system.User>
    **/
    boolean checkNameOrPhone(Long id, String username, String phone);

    /**
     * 查询超级管理员列表
     *
     * @description 查询超级管理员列表
     * @author jing.fang
     * @date 2021/9/26 11:35
     * @param
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getSuperList();

    /**
     * 查询编组下用户信息
     *
     * @description 查询编组下用户信息
     * @author jing.fang
     * @date 2022/8/5 14:16
     * @param groupIds groupIds
     * @return java.util.List<com.dzkj.entity.system.User>
    **/
    List<User> listByGroupIds(List<Long> groupIds);

    /**
     * 根据公司id查询
     *
     * @description: 根据公司id查询
     * @author: jing.fang
     * @Date: 2023/2/15 16:57
     * @param companyId companyId
     * @return java.util.List<com.dzkj.entity.system.User>
    **/
    List<User> findByCompanyId(Long companyId);

    /**
     * 统计超级管理员
     *
     * @description: 统计超级管理员
     * @author: jing.fang
     * @Date: 2023/2/15 20:50
     * @return int
    **/
    int countSuper();

    /**
     * 根据手机号查询用户信息
     *
     * @description 根据手机号查询用户信息
     * @author jing.fang
     * @date 2023/8/3 12:17
     * @param user user
     * @return: java.util.List<com.dzkj.entity.system.User>
     **/
    List<User> findByIdAndPhone(User user);
}
