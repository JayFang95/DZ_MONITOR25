package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.PtDataStopCondition;
import com.dzkj.biz.data.vo.PtStopVO;
import com.dzkj.entity.param_set.PtStop;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/23
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IPtStopService extends IService<PtStop> {

    /**
     * 分页查询
     * 
     * @description 分页查询
     * @author jing.fang
     * @date 2022/5/23 14:47
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PtStopVO>
    **/
    IPage<PtStopVO> getPage(Integer pi, Integer ps, PtDataStopCondition condition);

    /**
     * 查询停测记录
     *
     * @description 查询停测记录
     * @author jing.fang
     * @date 2022/5/23 14:49
     * @param condition condition
     * @return java.util.List<com.dzkj.biz.data.vo.PtStopVO>
    **/
    List<PtStopVO> getList(PtDataStopCondition condition);
}
