package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.ProjectType;

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
public interface IProjectTypeService extends IService<ProjectType> {

    /**
     * 项目类型名称验证
     *
     * @description: 项目类型名称验证
     * @author: jing.fang
     * @Date: 2023/2/15 21:13
     * @param projectType projectType
     * @return boolean
    **/
    boolean checkName(ProjectType projectType);
}
