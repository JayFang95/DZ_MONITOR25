package com.dzkj.biz.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.project.vo.ProCondition;
import com.dzkj.biz.project.vo.ProjectTypeVO;
import com.dzkj.biz.project.vo.ProjectVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IProjectBiz {

    /**
     * 项目信息分页查询
     *
     * @description 项目信息分页查询
     * @author jing.fang
     * @date 2021/9/2 8:58
     * @param pageIndex index
     * @param pageSize size
     * @param cond cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.project.Project>
    **/
    IPage<ProjectVO> getPage(Integer pageIndex, Integer pageSize, ProCondition cond);

    /**
     * 新增项目信息
     *
     * @description 新增项目信息
     * @author jing.fang
     * @date 2021/9/2 9:39
     * @param project project
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil addProject(ProjectVO project);

    /**
     * 修改项目信息
     *
     * @description 修改项目信息
     * @author jing.fang
     * @date 2021/9/2 9:39
     * @param project project
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateProject(ProjectVO project);

    /**
     * 项目删除
     *
     * @description 项目删除
     * @author jing.fang
     * @date 2021/9/2 11:29
     * @param id id
     * @param flg 是否判断项目结束
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil deleteProject(Long id, boolean flg);

    /**
     * 查询项目下拉集合
     *
     * @description 查询项目下拉集合
     * @author jing.fang
     * @date 2022/2/28 16:35
     * @param userId userId
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getList(Long userId);

    /**
     * 查询项目类型信息
     *
     * @description 查询项目类型信息
     * @author jing.fang
     * @date 2022/2/28 16:53
     * @return java.util.List<com.dzkj.biz.project.vo.ProjectTypeVO>
    **/
    List<ProjectTypeVO> projectTypeList();

    /**
     * 查询项目人员配置信息
     *
     * @description 查询项目人员配置信息
     * @author jing.fang
     * @date 2022/2/28 16:58
     * @param projectId projectId
     * @return java.util.List<java.lang.Long>
    **/
    List<Long> getProjectGroupList(Long projectId);

    /**
     * 获取测点数据下拉列表
     *
     * @description 获取测点数据下拉列表
     * @author jing.fang
     * @date 2022/4/11 13:55
     * @param userId userId
     * @return java.util.Map<java.lang.String,java.lang.Object>
    **/
    Map<String,Object> dropList(Long userId);

    /**
     * 查询项目及下拉集合
     *
     * @description 查询项目及下拉集合
     * @author jing.fang
     * @date 2022/5/7 10:43
     * @param userId userId
     * @return java.util.Map<java.lang.String,java.lang.Object>
    **/
    Map<String,Object> projectMissionList(Long userId);

    /**
     * 查询所有工程和监测任务
     *
     * @description 查询所有工程和监测任务
     * @author jing.fang
     * @date 2023/8/8 16:35
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     **/
    Map<String,Object> projectMissionDropAll();

    /**
     * 查询项目分布情况
     *
     * @description 查询项目分布情况
     * @author jing.fang
     * @date 2022/5/12 16:51
     * @param userId userId
     * @return com.dzkj.biz.project.vo.ProjectVO
    **/
    Map<String, List<ProjectVO>> getProjectInfo(Long userId);
}
