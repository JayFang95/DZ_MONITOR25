package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.Role;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 角色service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IRoleService extends IService<Role> {

    /**
     * 查询超级管理员角色id
     *
     * @description 查询超级管理员角色id
     * @author jing.fang
     * @date 2021/9/3 16:31
     * @param
     * @return java.lang.Long
    **/
    Long getSuperRoleId();

    /**
     * 根据角色名称查询
     *
     * @description 根据角色名称查询
     * @author jing.fang
     * @date 2021/9/6 9:02
     * @param role role
     * @return boolean
    **/
    boolean findByName(Role role);
}
