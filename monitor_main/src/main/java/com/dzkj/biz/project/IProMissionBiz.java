package com.dzkj.biz.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 监测任务业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IProMissionBiz {

    /**
     * 监测任务分页查询
     *
     * @description 监测任务分页查询
     * @author jing.fang
     * @date 2021/9/2 8:58
     * @param pageIndex index
     * @param pageSize size
     * @param cond cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.project.Project>
    **/
    IPage<ProMissionVO> getPage(Integer pageIndex, Integer pageSize, ProMissionCondition cond);

    /**
     * 新增监测任务
     *
     * @description 新增监测任务
     * @author jing.fang
     * @date 2021/9/2 9:39
     * @param mission mission
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil addMission(ProMissionVO mission);

    /**
     * 修改监测任务
     *
     * @description 修改监测任务
     * @author jing.fang
     * @date 2021/9/2 9:39
     * @param mission mission
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateMission(ProMissionVO mission);

    /**
     * 监测任务删除
     *
     * @description 监测任务删除
     * @author jing.fang
     * @date 2021/9/2 11:29
     * @param id id
     * @param flg 是否判断未完成
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil deleteMission(Long id, boolean flg);
    /**
     * 刪除任務及关联数据
     *
     * @description: 刪除任務及关联数据
     * @author: jing.fang
     * @Date: 2023/2/16 10:26 
     * @param missionIds missionIds
     * @return com.dzkj.common.util.ResponseUtil 
    **/
    ResponseUtil deleteMission(List<Long> missionIds);

    /**
     * 查询项目下监测任务
     *
     * @description 查询项目下监测任务
     * @author jing.fang
     * @date 2022/3/1 9:59
     * @param userId userId
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<ProMissionVO> getMissionList(Long userId);

    /**
     * 查询监测任务人员配置信息
     *
     * @description 查询监测任务人员配置信息
     * @author jing.fang
     * @date 2022/3/1 10:07
     * @param missionId missionId
     * @return java.util.List<java.lang.Long>
    **/
    List<Long> getMissionGroupList(Long missionId);

    /**
     * 上移/下移任务
     *
     * @description 上移/下移任务
     * @author jing.fang
     * @date 2022/4/12 14:54
     * @param mission mission
     * @param type type
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateIdx(ProMissionVO mission, int type);

    /**
     * 查询自动化监测任务集合
     *
     * @description 查询自动化监测任务集合
     * @author jing.fang
     * @date 2023/6/6 11:56
     * @param companyId companyId
     * @param missionId missionId
     * @return: List
     **/
    List<ProMissionVO> getMissionInCompany(Long companyId, Long missionId);

    /**
     * 查询自动化监测任务集合(其他推送)
     *
     * @description 查询自动化监测任务集合(其他推送)
     * @author jing.fang
     * @date 2023/6/6 11:56
     * @param companyId companyId
     * @param missionId missionId
     * @return: List
     **/
    List<ProMissionVO> getMissionOtherInCompany(Long companyId, Long missionId);
}
