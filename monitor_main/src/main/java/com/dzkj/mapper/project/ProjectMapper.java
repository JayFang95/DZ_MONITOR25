package com.dzkj.mapper.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.project.vo.ProCondition;
import com.dzkj.entity.project.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目信息
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2021/9/2 10:02
     * @param projectPage projectPage
     * @param cond cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.project.Project>
    **/
    IPage<Project> getPage(Page<Project> projectPage, @Param("cond") ProCondition cond);

    /**
     * 按条件查询
     *
     * @description 按条件查询
     * @author jing.fang
     * @date 2023/3/29 10:09
     * @param cond cond
     * @return: java.util.List<com.dzkj.entity.project.Project>
     **/
    List<Project> getList(@Param("cond") ProCondition cond);
}
