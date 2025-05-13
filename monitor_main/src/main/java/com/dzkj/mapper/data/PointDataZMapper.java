package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataZ;
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
public interface PointDataZMapper extends BaseMapper<PointDataZ> {

    /**
     * 按条件查询监测数据
     *
     * @description 按条件查询监测数据
     * @author jing.fang
     * @date 2022/5/18 15:49
     * @param condition condition
     * @return java.util.List<com.dzkj.biz.data.vo.ReportData>
    **/
    List<ReportData> getDataByCond(@Param("cond") TableDataCondition condition, @Param("list") List<Long> list);

    /**
     * 获取最新五期测量日期
     *
     * @description 获取最新五期测量日期
     * @author jing.fang
     * @date 2024/8/1 10:24
     * @param pointIds pointIds
     * @return: java.util.List<com.dzkj.entity.data.PointDataZ>
     **/
    List<PointDataZ> getDateLimit(@Param("list") List<Long> pointIds);
}
