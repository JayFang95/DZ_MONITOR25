package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.MonitorType;

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
public interface IMonitorTypeService extends IService<MonitorType> {

    /**
     * 检验名称是否存在
     *
     * @description: 检验名称是否存在
     * @author: jing.fang
     * @Date: 2023/2/15 21:11
     * @param monitorType monitorType
     * @return boolean
    **/
    boolean checkTypeName(MonitorType monitorType);
}
