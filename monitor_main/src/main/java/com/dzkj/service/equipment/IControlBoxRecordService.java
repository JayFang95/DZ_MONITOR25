package com.dzkj.service.equipment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.equipment.vo.CtlBoxRecordCondition;
import com.dzkj.entity.equipment.ControlBoxRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13
 * @description 控制器记录服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IControlBoxRecordService extends IService<ControlBoxRecord> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/7/13 13:52
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.equipment.ControlBoxRecord>
     **/
    IPage<ControlBoxRecord> getPage(Integer pageIndex, Integer pageSize, CtlBoxRecordCondition cond);
}
