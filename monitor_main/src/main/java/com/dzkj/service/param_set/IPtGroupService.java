package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.param_set.vo.GroupCondition;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.entity.param_set.PtGroup;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 点组服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IPtGroupService extends IService<PtGroup> {

    /**
     * 查询编组列表
     *
     * @description 查询编组列表
     * @author jing.fang
     * @date 2022/1/14 10:30
     * @param condition condition
     * @return java.util.List<com.dzkj.entity.param_set.PtGroup>
    **/
    List<PtGroup> getList(GroupCondition condition);

    /**
     * 分页查询
     * @description 查询编组列表
     * @author jing.fang
     * @date 2022/1/14 10:30
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return java.util.List<com.dzkj.entity.param_set.PtGroup>
     */
    Page<PtGroup> getPage(Integer pi, Integer ps, GroupCondition condition);

    /**
     * 根据点组名称查询
     *
     * @description 根据点组名称查询
     * @author jing.fang
     * @date 2021/9/9 9:24
     * @param ptGroup ptGroup
     * @return boolean
    **/
    boolean findByName(PtGroupVO ptGroup);

    /**
     * 根据任务id删除点组信息
     *
     * @description 根据任务id删除点组信息
     * @author jing.fang
     * @date 2021/9/30 9:39
     * @param missionId missionId
     * @return int
    **/
    int deleteByMission(Long missionId);

    /**
     * 查询包含用户的测点编组
     *
     * @description 查询包含用户的测点编组
     * @author jing.fang
     * @date 2022/8/4 17:19
     * @param projectId projectId
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.param_set.PtGroup>
    **/
    List<PtGroup> findByProjectId(Long projectId, Long missionId);

    /**
     * 根据任务id查询
     *
     * @description: 根据任务id查询
     * @author: jing.fang
     * @Date: 2023/2/16 10:13
     * @param missionIds missionIds
     * @return java.util.List<com.dzkj.entity.param_set.PtGroup>
    **/
    List<PtGroup> listByMissionIds(List<Long> missionIds);

    /**
     * 查询任务下所有编组id
     *
     * @description 查询任务下所有编组id
     * @author jing.fang
     * @date 2023/6/7 10:07
     * @param ptGroupId ptGroupId
     * @return: java.util.List<java.lang.Long>
     **/
    List<Long> getGroupIdsInMission(Long ptGroupId);
}
