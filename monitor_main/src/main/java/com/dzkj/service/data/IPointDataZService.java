package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.data.vo.PtDataChartCondition;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataZ;

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
public interface IPointDataZService extends IService<PointDataZ> {

    /**
     * 查询项目下符合条件的报表数据
     *
     * @description 查询项目下符合条件的报表数据
     * @author jing.fang
     * @date 2022/5/18 15:48
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
     * @Date: 2023/2/16 11:06
     * @param ptIds ptIds
     * @return boolean
    **/
    boolean removeByPtIds(List<Long> ptIds);

    /**
     * 获取测量最新五期测量日期
     *
     * @description 获取测量最新五期测量日期
     * @author jing.fang
     * @date 2024/8/1 10:23
     * @param condition condition
     * @return: java.util.List<com.dzkj.entity.data.PointDataZ>
     **/
    List<PointDataZ> getDateLimit(PtDataChartCondition condition);
}
