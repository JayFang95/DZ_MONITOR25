package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzVO;
import com.dzkj.entity.data.PointDataXyz;

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
public interface IPointDataXyzService extends IService<PointDataXyz> {

    /**
     * 查询列表信息
     *
     * @description 查询列表信息
     * @author jing.fang
     * @date 2022/5/20 16:27
     * @param condition condition
     * @return java.util.List<com.dzkj.biz.data.vo.PointDataXyzVO>
    **/
    List<PointDataXyzVO> getList(OtherDataCondition condition);

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2022/5/20 16:27
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.dzkj.biz.data.vo.PointDataXyzVO>
    **/
    Page<PointDataXyzVO> getPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 根据测点id删除
     *
     * @description: 根据测点id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:11
     * @param ptIds ptIds
     * @return boolean
    **/
    boolean removeByPtIds(List<Long> ptIds);

    /**
     * 查询测点最新一期数据
     *
     * @description 查询测点最新一期数据
     * @author jing.fang
     * @date 2024/8/28 14:07
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.biz.data.vo.PointDataXyzVO>
     **/
    List<PointDataXyzVO> getDataByPidList(List<Long> pidList);
}
