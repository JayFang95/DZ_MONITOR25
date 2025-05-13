package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.PtDataChartCondition;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataXyzh;

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
public interface IPointDataXyzhService extends IService<PointDataXyzh> {

    /**
     * 查询项目下符合条件的报表数据
     *
     * @description 查询项目下符合条件的报表数据
     * @author jing.fang
     * @date 2022/5/18 16:09
     * @param condition condition
     * @param pidList pidList
     * @return java.util.List<com.dzkj.biz.data.vo.ReportData>
    **/
    List<ReportData> getDataByCond(TableDataCondition condition, List<Long> pidList);

    /**
     * 根据测点id删除
     *
     * @description: 根据测点id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:09
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
     * 查询最新一期监测数据
     *
     * @description 查询最新一期监测数据
     * @author jing.fang
     * @date 2023/6/8 11:15
     * @param pointIds pointIds
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> queryLatestData(List<Long> pointIds);

    /**
     * 获取测点上传未报警测量数据
     *
     * @description 获取测点上传未报警测量数据
     * @author jing.fang
     * @date 2023/6/9 15:49
     * @param pointId pointId
     * @return: com.dzkj.entity.data.PointDataXyzh
     **/
    PointDataXyzh getLastNoAlarmData(Long pointId);

    /**
     * 获取指定测点最新五期测量日期
     *
     * @description 获取指定测点最新五期测量日期
     * @author jing.fang
     * @date 2024/8/1 10:19
     * @param condition condition
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> getDateLimit(PtDataChartCondition condition);

    /**
     * 查询最早一期测点测量数据
     *
     * @description 查询最早一期测点测量数据
     * @author jing.fang
     * @date 2024/12/30 13:41
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.entity.data.PointDataXyzh>
     **/
    List<PointDataXyzh> getEarliestRecycleData(List<Long> pidList);

    /**
     * 查询最早一期重置测点测量数据
     *
     * @description 查询最早一期重置测点测量数据
     * @author jing.fang
     * @date 2024/12/30 13:41
     * @param pIds pIds
     * @return: java.util.List<com.dzkj.entity.data
     **/
    List<PointDataXyzh> getEarliestResetRecycleData(List<Long> pIds);
}
