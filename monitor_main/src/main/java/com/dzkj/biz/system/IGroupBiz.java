package com.dzkj.biz.system;

import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Groups;

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
public interface IGroupBiz {

    /**
     * 分组查询
     *
     * @description 分组查询
     * @author jing.fang
     * @date 2021/8/6 10:29
     * @param companyId 选择单位id
     * @return java.util.List<com.dzkj.entity.system.Group>
    **/
    List<Groups> getList(Long companyId);

    /**
     * 新增
     *
     * @description 新增
     * @author jing.fang
     * @date 2021/8/6 11:04
     * @param companyId companyId
     * @param groups group
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(Long companyId, Groups groups);

    /**
     * 编辑
     *
     * @description 编辑
     * @author jing.fang
     * @date 2021/8/6 11:05
     * @param groups group
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(Groups groups);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2021/8/6 11:05
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 查询工作组人员列表
     *
     * @description 查询工作组人员列表
     * @author jing.fang
     * @date 2021/8/6 11:18
     * @param id id
     * @return java.util.List<com.dzkj.biz.system.vo.UserVO>
    **/
    List<UserVO> getListUser(Long id);

    /**
     * 更新工作组列表
     *
     * @description 更新工作组列表
     * @author jing.fang
     * @date 2021/8/6 13:21
     * @param id id
     * @param userIds userIds
     * @return boolean
    **/
    boolean updateUserList(Long id, List<Long> userIds);


    /**
     * 查询人员配置集合
     *
     * @description 查询人员配置集合
     * @author jing.fang
     * @date 2022/3/1 17:59
     * @param ids ids
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
     **/
    List<DropVO> getUserGroupListIn(List<Long> ids);

    /**
     * 查询单位下人员配置工作组集合
     *
     * @description 查询单位下人员配置工作组集合
     * @author jing.fang
     * @date 2022/3/2 10:19
     * @param companyId companyId
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getUserGroupList(Long companyId);
}
