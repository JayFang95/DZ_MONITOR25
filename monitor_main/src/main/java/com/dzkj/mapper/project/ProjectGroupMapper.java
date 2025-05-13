package com.dzkj.mapper.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.project.ProjectGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目人员配置
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ProjectGroupMapper extends BaseMapper<ProjectGroup> {

    /**
     * 根据userid查询
     *
     * @description 根据userid查询
     * @author jing.fang
     * @date 2022/8/22 10:42
     * @param userId userId
     * @return java.util.List<com.dzkj.entity.project.ProjectGroup>
    **/
    List<ProjectGroup> listByUserId(@Param("userId") Long userId);
}
