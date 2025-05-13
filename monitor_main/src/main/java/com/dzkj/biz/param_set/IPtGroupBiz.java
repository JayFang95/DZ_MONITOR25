package com.dzkj.biz.param_set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.param_set.vo.GroupCondition;
import com.dzkj.biz.param_set.vo.GroupPointVO;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 点组业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IPtGroupBiz {

    /**
     * 点组列表查询
     *
     * @description 点组列表查询
     * @author jing.fang
     * @date 2021/9/8 14:22
     * @param condition condition
     * @param pi pi
     * @param ps ps
     * @return java.util.List<com.dzkj.entity.param_set.PtGroup>
    **/
    IPage<PtGroupVO> getList(Integer pi, Integer ps, GroupCondition condition);

    /**
     * 新增点组
     *
     * @description 新增点组
     * @author jing.fang
     * @date 2021/9/8 14:23
     * @param ptGroup ptGroup
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(PtGroupVO ptGroup);

    /**
     * 修改点组
     *
     * @description 修改点组
     * @author jing.fang
     * @date 2021/9/8 14:23
     * @param ptGroup ptGroup
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(PtGroupVO ptGroup);

    /**
     * 删除点组
     *
     * @description 删除点组
     * @author jing.fang
     * @date 2021/9/8 14:23
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 查询编组人员配置信息
     *
     * @description 查询编组人员配置信息
     * @author jing.fang
     * @date 2022/3/1 17:53
     * @param ptGroupId ptGroupId
     * @return java.util.List<java.lang.Long>
    **/
    List<Long> getPtGroupGroupList(Long ptGroupId);

    /**
     * 查询编组列表
     *
     * @description 查询编组列表
     * @author jing.fang
     * @date 2022/3/21 16:55
     * @param missionIds missionIds
     * @return java.util.List<com.dzkj.biz.param_set.vo.PtGroupVO>
    **/
    List<PtGroupVO> list(List<Long> missionIds);

    /**
     * 查询任务包含编组及下测点集合
     *
     * @description 查询任务包含编组及下测点集合
     * @author jing.fang
     * @date 2023/3/13 15:14
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.biz.param_set.vo.GroupPointVO>
     **/
    List<GroupPointVO> groupPtList(Long missionId);

    /**
     * 获取app手工录入数据模板
     *
     * @description 获取app手工录入数据模板
     * @author jing.fang
     * @date 2024/8/28 8:55
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.biz.param_set.vo.GroupPointVO>
     **/
    List<GroupPointVO> groupPtAppList(Long missionId);
}
