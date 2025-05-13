package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.PointDataZRealVO;
import com.dzkj.entity.data.PointDataZReal;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IPointDataZRealService extends IService<PointDataZReal> {

    /**
     * 根据测点id删除
     *
     * @description: 根据测点id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:07
     * @param ptIds ptIds
     * @return boolean
    **/
    boolean removeByPtIds(List<Long> ptIds);

    /**
     * 查询测点最新一期数据
     *
     * @description 查询测点最新一期数据
     * @author jing.fang
     * @date 2024/8/28 14:16
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.biz.data.vo.PointDataZRealVO>
     **/
    List<PointDataZRealVO> getDataByPidList(List<Long> pidList);
}
