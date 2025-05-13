package com.dzkj.biz.system;

import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.biz.system.vo.ResourceVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Resource;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/19
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IResourceBiz {
    /**
     * 查询资源列表
     *
     * @description 查询角色列表
     * @author wangy
     * @date 2021/8/19 10:06
     * @return java.util.List<com.dzkj.entity.system.Resource>
     **/
    List<ResourceVO> getList(ResourceCondition resourceCondition);


    /**
     * 编辑
     *
     * @description 编辑
     * @author wangy
     * @date 2021/8/19 10:11
     * @param resource resource
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil updateResource(Resource resource);
}
