package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.ProjectType;
import com.dzkj.mapper.system.ProjectTypeMapper;
import com.dzkj.service.system.IProjectTypeService;
import org.springframework.stereotype.Service;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/17
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class ProjectTypeServiceImpl extends ServiceImpl<ProjectTypeMapper, ProjectType> implements IProjectTypeService {

    @Override
    public boolean checkName(ProjectType projectType) {
        LambdaQueryWrapper<ProjectType> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(projectType.getId()!=null, ProjectType::getId, projectType.getId())
                .eq(ProjectType::getName, projectType.getName());
        return baseMapper.selectCount(wrapper) > 0;
    }

}
