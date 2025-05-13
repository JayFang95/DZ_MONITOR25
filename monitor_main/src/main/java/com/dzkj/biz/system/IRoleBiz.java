package com.dzkj.biz.system;

import com.dzkj.biz.system.vo.Tree;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Role;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/6
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IRoleBiz {

    /**
     * 查询角色列表
     *
     * @description 查询角色列表
     * @author jing.fang
     * @date 2021/8/9 10:06
     * @return java.util.List<com.dzkj.entity.system.Role>
    **/
    List<Role> getList();

    /**
     * 新增
     *
     * @description 新增
     * @author jing.fang
     * @date 2021/8/9 10:11
     * @param role role
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil addRole(Role role);

    /**
     * 编辑
     *
     * @description 编辑
     * @author jing.fang
     * @date 2021/8/9 10:11
     * @param role role
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateRole(Role role);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2021/8/9 10:11
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil deleteRole(Long id);

    /**
     * 查询角色菜单
     *
     * @description 查询角色菜单
     * @author jing.fang
     * @date 2021/8/9 10:24
     * @param id id
     * @return java.util.List<com.dzkj.biz.system.vo.Tree>
    **/
    List<Tree> getTree(Long id);

    /**
     * 角色授权更新
     *
     * @description 角色授权更新
     * @author jing.fang
     * @date 2022/3/2 9:55
     * @param id id
     * @param menuIds menuIds
     * @return boolean
    **/
    boolean updateTree(Long id, List<Long> menuIds);

    /**
     * 查询角色下拉集合
     *
     * @description 查询角色下拉集合
     * @author jing.fang
     * @date 2022/3/2 9:55
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getRoleDrop();
}
