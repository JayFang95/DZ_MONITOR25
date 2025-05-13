package com.dzkj.service.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.project.vo.ProCondition;
import com.dzkj.biz.project.vo.ProjectVO;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.project.Project;
import com.dzkj.mapper.project.ProjectMapper;
import com.dzkj.service.project.IProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Override
    public IPage<ProjectVO> getPage(Integer pageIndex, Integer pageSize, ProCondition cond) {
        IPage<Project> result = baseMapper.getPage(new Page<>(pageIndex, pageSize), cond);
        return DzBeanUtils.pageCopy(result, ProjectVO.class);
    }

    @Override
    public List<ProjectVO> getList(ProCondition cond) {
        List<Project> result = baseMapper.getList(cond);
        return DzBeanUtils.listCopy(result, ProjectVO.class);
    }

    @Override
    public List<ProjectVO> findByName(ProjectVO project) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getName, project.getName())
                .eq(Project::getCompanyId, project.getCompanyId())
                .ne(project.getId()!=null, Project::getId, project.getId());
        List<Project> list = baseMapper.selectList(wrapper);
        return DzBeanUtils.listCopy(list, ProjectVO.class);
    }

    @Override
    public List<ProjectVO> listByCompanyId(Long companyId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getCompanyId, companyId);
        List<Project> list = baseMapper.selectList(wrapper);
        return DzBeanUtils.listCopy(list, ProjectVO.class);
    }
}
