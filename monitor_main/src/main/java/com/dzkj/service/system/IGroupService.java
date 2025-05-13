package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.Groups;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 分组service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IGroupService extends IService<Groups> {

    /**
     * 根据公司id删除
     *
     * @description: 根据公司id删除
     * @author: jing.fang
     * @Date: 2023/2/15 17:00
     * @param companyId companyId
     * @return boolean
    **/
    boolean removeByCompanyId(Long companyId);

    /**
     * 工作组名称重复验证
     *
     * @description: 工作组名称重复验证
     * @author: jing.fang
     * @Date: 2023/2/15 21:01
     * @param groups groups
     * @return boolean
    **/
    boolean checkName(Groups groups);
}
