package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataXyzh;
import org.apache.ibatis.annotations.Param;

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
public interface PointDataXyzhMapper extends BaseMapper<PointDataXyzh> {

    /**
     * 按条件查询监测数据
     *
     * @description 按条件查询监测数据
     * @author jing.fang
     * @date 2022/5/18 16:10
     * @param condition condition
     * @return java.util.List<com.dzkj.biz.data.vo.ReportData>
    **/
    List<ReportData> getDataByCond(@Param("cond") TableDataCondition condition, @Param("list") List<Long> list);

    /**
     * 查询最新一期监测数据
     *
     * @description 查询最新一期监测数据
     * @author jing.fang
     * @date 2023/6/8 11:17
     * @param pointIds pointIds
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> queryLatestData(@Param("list") List<Long> pointIds);

    /**
     * 查询最近一条未报警数据
     *
     * @description 查询最近一条未报警数据
     * @author jing.fang
     * @date 2023/6/9 15:49
     * @param pointId pointId
     * @return: com.dzkj.entity.data.PointDataXyzh
     **/
    PointDataXyzh getLastNoAlarmData(@Param("pointId") Long pointId);

    /**
     * 获取最新五期测量日期
     *
     * @description 获取最新五期测量日期
     * @author jing.fang
     * @date 2024/8/1 10:20
     * @param pointIds pointIds
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> getDateLimit(@Param("list") List<Long> pointIds);

    /**
     * 查询测点最早一期数据
     *
     * @description 查询测点最早一期数据
     * @author jing.fang
     * @date 2024/12/30 13:44
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> getEarliestRecycleData(@Param("list")List<Long> pidList);

    /**
     * 查询最早一期重置测点测量数据
     *
     * @description 查询最早一期重置测点测量数据
     * @author jing.fang
     * @date 2025/3/7 下午5:34
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> getEarliestResetRecycleData(@Param("list")List<Long> pidList);
}
