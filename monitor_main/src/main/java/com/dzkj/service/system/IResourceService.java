package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.entity.system.Resource;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 资源service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IResourceService extends IService<Resource> {

    /**
     * 查询资源列表
     *
     * @description 查询资源列表
     * @author jing.fang
     * @date 2021/8/9 10:29
     * @param resourceCondition resourceCondition
     * @return java.util.List<com.dzkj.entity.system.Resource>
    **/
    List<Resource> getMenus(ResourceCondition resourceCondition);


    /**
     * 获取角色资源列表
     *
     * @description 获取角色资源列表
     * @author jing.fang
     * @date 2021/8/9 10:33
     * @param id id
     * @return java.util.List<com.dzkj.entity.system.Resource>
    **/
    List<Resource> findByRoleId(Long id);
}
