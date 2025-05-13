package com.dzkj.service.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.project.vo.ProCondition;
import com.dzkj.biz.project.vo.ProjectVO;
import com.dzkj.entity.project.Project;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IProjectService extends IService<Project> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2021/9/2 10:02
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.project.Project>
    **/
    IPage<ProjectVO> getPage(Integer pageIndex, Integer pageSize, ProCondition cond);

    /**
     * 查询列表
     *
     * @description 查询列表
     * @author jing.fang
     * @date 2023/3/29 10:06
     * @param cond cond
     * @return: java.util.List<com.dzkj.entity.project.Project>
     **/
    List<ProjectVO> getList(ProCondition cond);

    /**
     * 根据名称查询
     *
     * @description 根据名称查询
     * @author jing.fang
     * @date 2021/9/3 17:46
     * @param project project
     * @return java.util.List<com.dzkj.entity.project.Project>
    **/
    List<ProjectVO> findByName(ProjectVO project);

    /**
     * 查询公司下所有项目
     *
     * @description: 查询公司下所有项目
     * @author: jing.fang
     * @Date: 2023/2/15 17:20
     * @param companyId companyId
     * @return java.util.List<com.dzkj.biz.project.vo.ProjectVO>
    **/
    List<ProjectVO> listByCompanyId(Long companyId);
}
