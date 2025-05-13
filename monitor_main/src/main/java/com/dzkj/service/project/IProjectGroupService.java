package com.dzkj.service.project;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.project.ProjectGroup;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目人员配置接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IProjectGroupService extends IService<ProjectGroup> {

    /**
     * 根据项目id删除
     *
     * @description 根据项目id删除
     * @author jing.fang
     * @date 2021/9/6 9:07
     * @param id id
     * @return void
    **/
    void deleteByProjectId(Long id);

    /**
     * 根据任务id删除
     *
     * @description 根据任务id删除
     * @author jing.fang
     * @date 2021/9/6 9:07
     * @param id id
     * @return void
     **/
    void deleteByMissionId(Long id);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:34
     * @param missionIds missionIds
     * @return void
    **/
    void deleteByMissionIds(List<Long> missionIds);

    /**
     * 根据编组id删除
     *
     * @description
     * @author jing.fang
     * @date 2022/1/14 11:03
     * @param id id
     * @return void
    **/
    void deleteByPtGroupId(Long id);

    /**
     * 查询项目下任务人员配置列表
     *
     * @description 查询项目下任务人员配置列表
     * @author jing.fang
     * @date 2022/1/11 16:18
     * @param id id
     * @return java.util.List<java.lang.Long>
    **/
    List<Long> getMissionGroupIds(Long id);

    /**
     * 查询任务下任务人员配置列表
     *
     * @description 查询项目下任务人员配置列表
     * @author jing.fang
     * @date 2022/1/11 16:18
     * @param id id
     * @return java.util.List<java.lang.Long>
     **/
    List<Long> getPtGroupGroupIds(Long id);

    /**
     * 查询人员编组信息
     *
     * @description 查询人员编组信息
     * @author jing.fang
     * @date 2022/8/22 10:40
     * @param userId userId
     * @return java.util.List<com.dzkj.entity.project.ProjectGroup>
    **/
    List<ProjectGroup> listByUserId(Long userId);

    /**
     * 根据groupId删除
     *
     * @description: 根据groupId删除
     * @author: jing.fang
     * @Date: 2023/2/15 21:04
     * @param groupId groupId
     * @return boolean
    **/
    boolean removeByGroupId(Long groupId);

    /**
     * 根据项目id查询
     *
     * @description: 根据项目id查询
     * @author: jing.fang
     * @Date: 2023/2/16 9:55
     * @param projectId projectId
     * @return java.util.List<com.dzkj.entity.project.ProjectGroup>
    **/
    List<ProjectGroup> listByProjectId(Long projectId);

    /**
     * 根据任务id查询
     *
     * @description: 根据任务id查询
     * @author: jing.fang
     * @Date: 2023/2/16 11:14
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.project.ProjectGroup>
    **/
    List<ProjectGroup> listByMissionId(Long missionId);

    /**
     * 根据测点编组id查询
     *
     * @description: 根据测点编组id查询
     * @author: jing.fang
     * @Date: 2023/2/16 11:30
     * @param ptGroupId ptGroupId
     * @return java.util.List<com.dzkj.entity.project.ProjectGroup>
    **/
    List<ProjectGroup> listByPtGroupId(Long ptGroupId);
}
