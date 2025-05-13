package com.dzkj.service.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.entity.project.ProMission;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/3
 * @description 监测任务服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IProMissionService extends IService<ProMission> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2022/1/11 16:37
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.project.vo.ProMissionVO>
     **/
    IPage<ProMissionVO> getPage(Integer pageIndex, Integer pageSize, ProMissionCondition cond);

    /**
     * 列表查询
     * @param cond cond
     * @return List<ProjectVO>
     */
    List<ProMissionVO> getList(ProMissionCondition cond);

    /**
     * 根据名称查询监测任务
     *
     * @description 根据名称查询监测任务
     * @author jing.fang
     * @date 2021/9/3 17:41
     * @param  mission mission
     * @return java.util.List<com.dzkj.entity.project.ProMission>
    **/
    List<ProMission> findByName(ProMissionVO mission);

    /**
     * 根据项目id查询任务列表
     *
     * @description 根据项目id查询任务列表
     * @author jing.fang
     * @date 2021/9/6 9:14
     * @param projectId id
     * @return java.util.List<com.dzkj.entity.project.ProMission>
    **/
    List<ProMission> getMissionByProjectId(Long projectId);

    /**
     * 获取工程下任务信息
     *
     * @description 获取工程下任务信息
     * @author jing.fang
     * @date 2022/3/31 16:36
     * @param projectId projectId
     * @return java.util.List<com.dzkj.entity.project.ProMission>
    **/
    List<ProMission> getList(Long projectId);

    /**
     * 获取工程下任务信息
     *
     * @description 获取工程下任务信息
     * @author jing.fang
     * @date 2022/8/22 14:38
     * @param missionIds missionIds
     * @return java.util.List<com.dzkj.entity.project.ProMission>
    **/
    List<ProMission> getList(List<Long> missionIds);

    /**
     * 获取工程下任务信息
     *
     * @description 获取工程下任务信息
     * @author jing.fang
     * @date 2023/7/31 11:46
     * @param projIds projIds
     * @return: java.util.List<com.dzkj.entity.project.ProMission>
     **/
    List<ProMission> getListInProjIds(List<Long> projIds);

    /**
     * 查询任务信息
     *
     * @description 查询任务信息
     * @author jing.fang
     * @date 2022/3/31 16:37
     * @param missionId missionId
     * @return com.dzkj.entity.project.ProMission
    **/
    ProMission findById(Long missionId);

    /**
     * 获取当前做大序号
     *
     * @description 获取当前做大序号
     * @author jing.fang
     * @date 2022/4/12 14:48
     * @param mission mission
     * @return java.lang.Integer
    **/
    Integer getIndex(ProMissionVO mission);

    /**
     * 查询自动监测类型任务
     *
     * @description 查询自动监测类型任务
     * @author jing.fang
     * @date 2023/6/6 13:46
     * @param companyId companyId
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.project.ProMission>
     **/
    List<ProMission> getMissionInCompany(Long companyId, Long missionId);

    /**
     * 查询自动化监测任务集合(其他推送)
     *
     * @description 查询自动化监测任务集合(其他推送)
     * @author jing.fang
     * @date 2024/11/18 15:25
     * @param companyId companyId
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.project.ProMission>
     **/
    List<ProMission> getMissionOtherInCompany(Long companyId, Long missionId);

}
