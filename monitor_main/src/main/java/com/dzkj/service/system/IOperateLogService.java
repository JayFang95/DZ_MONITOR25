package com.dzkj.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.system.vo.OperateLogCondition;
import com.dzkj.entity.system.OperateLog;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IOperateLogService extends IService<OperateLog> {

    /**
     * 分页查询
     *
     * @description: 分页查询
     * @author: jing.fang
     * @Date: 2023/2/15 21:07
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param condition  condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.system.OperateLog>
     **/
    IPage<OperateLog> getPage(int pageIndex, int pageSize, OperateLogCondition condition);

    /**
     * 根据公司id删除
     *
     * @description: 根据公司id删除
     * @author: jing.fang
     * @Date: 2023/2/15 17:13
     * @param companyId companyId
     * @return boolean
    **/
    boolean removeByCompanyId(Long companyId);

}
