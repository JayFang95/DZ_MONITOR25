package com.dzkj.biz.equipment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.equipment.vo.ControlBoxRecordVO;
import com.dzkj.biz.equipment.vo.CtlBoxRecordCondition;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13 10:34
 * @description 控制器记录业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IControlBoxRecordBiz {

    /**
     * 分页查询控制器上下线记录
     *
     * @description 分页查询控制器上下线记录
     * @author jing.fang
     * @date 2023/7/13 13:47
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.equipment.vo.ControlBoxRecordVO>
     **/
    IPage<ControlBoxRecordVO> getPage(Integer pageIndex, Integer pageSize, CtlBoxRecordCondition cond);
}
