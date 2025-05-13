package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzhRealVO;
import com.dzkj.entity.data.PointDataXyzhReal;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/2
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IPointDataXyzhRealService extends IService<PointDataXyzhReal> {

    /**
     * 根据测点id删除
     *
     * @description: 根据测点id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:10
     * @param ptIds ptIds
     * @return boolean
    **/
    boolean removeByPtIds(List<Long> ptIds);

    /**
     * 删除临时加测数据
     * @param pidList pidList
     * @param recycleNum recycleNum
     * @return boolean
     */
    boolean removeOnceData(List<Long> pidList, int recycleNum);

    /**
     * 查询数据
     *
     * @description 查询数据
     * @author jing.fang
     * @date 2023/8/7 12:01
     * @param condition condition
     * @return: java.util.List<com.dzkj.biz.data.vo.PointDataXyzhVO>
     **/
    List<PointDataXyzhRealVO> getList(OtherDataCondition condition);

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/8/7 12:01
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.dzkj.biz.data.vo.PointDataXyzhVO>
     **/
    Page<PointDataXyzhRealVO> getPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 查询最新一期测点数据
     *
     * @description 查询最新一期测点数据
     * @author jing.fang
     * @date 2024/8/28 13:46
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzhReal>
     **/
    List<PointDataXyzhRealVO> getDataByPidList(List<Long> pidList);
}
