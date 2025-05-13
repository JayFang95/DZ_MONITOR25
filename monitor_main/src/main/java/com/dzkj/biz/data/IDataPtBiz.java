package com.dzkj.biz.data;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDataPtBiz {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2022/4/12 16:06
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PointDataZVO>
    **/
    IPage<PointDataZVO> zPage(Integer pi, Integer ps, PtDataCondition condition);

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2022/4/12 16:06
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PointDataXyzhVO>
    **/
    IPage<PointDataXyzhVO> xyzhPage(Integer pi, Integer ps, PtDataCondition condition);

    /**
     * 数据初始化
     *
     * @description 数据初始化
     * @author jing.fang
     * @date 2022/5/7 16:14
     * @param condition condition
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil dataInit(PtDataCondition condition);

    /**
     * 查询巡视列表数据
     *
     * @description 查询巡视列表数据
     * @author jing.fang
     * @date 2022/5/7 17:53
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<java.lang.Object>
    **/
    IPage<JcInfoVO> xsPage(Integer pi, Integer ps, PtDataCondition condition);

    /**
     * 数据初始化
     *
     * @description 数据初始化
     * @author jing.fang
     * @date 2022/5/10 10:41
     * @param missionId missionId
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil dataXsInit(Long missionId);

    /**
     * 删除巡视记录
     *
     * @description 删除巡视记录
     * @author jing.fang
     * @date 2022/5/10 10:43
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil deleteXs(Long id);

    /**
     * 重新计算
     *
     * @description 重新计算
     * @author jing.fang
     * @date 2022/5/10 16:30
     * @param calculate calculate
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil dataCalculate(PtDataCalculate calculate);

    /**
     * 获取图表分析数据集合
     *
     * @description 获取图表分析数据集合
     * @author jing.fang
     * @date 2024/8/1 9:39
     * @param condition condition
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    List<PointDataZVO> dataListChart(PtDataChartCondition condition);

    /**
     * 获取图表分析数据集合
     *
     * @description 获取图表分析数据集合
     * @author jing.fang
     * @date 2024/8/1 9:39
     * @param condition condition
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    List<PointDataXyzhVO> dataXyzhListChart(PtDataChartCondition condition);
}
