package com.dzkj.biz.data;

import com.dzkj.biz.data.vo.*;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
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
public interface IDataTableBiz {

    /**
     * 查询报表汇总信息
     *
     * @description 查询报表汇总信息
     * @author jing.fang
     * @date 2022/5/18 10:44
     * @param condition condition
     * @return com.dzkj.biz.data.vo.TableDataAll
    **/
    TableDataAll getAllData(TableDataCondition condition);

    /**
     * 查询巡视记录数据
     *
     * @description 查询巡视记录数据
     * @author jing.fang
     * @date 2022/5/19 9:16
     * @param condition condition
     * @return java.util.List<com.dzkj.biz.data.vo.JcInfoVO>
    **/
    List<JcInfoVO> getXsData(TableDataCondition condition);

    /**
     * 查询非巡视类型数据
     *
     * @description 查询非巡视类型数据
     * @author jing.fang
     * @date 2022/5/19 13:53
     * @param condition condition
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil getListData(TableDataCondition condition);

    /**
     * 保存eChart图片到本地并返回文件路径
     *
     * @description 保存eChart图片到本地并返回文件路径
     * @author jing.fang
     * @date 2022/6/1 9:06
     * @param echartData echartData
     * @return java.lang.String
    **/
    String saveChartImag(EchartData echartData);

    /**
     * 导出全部报表
     *
     * @description 导出全部报表
     * @author jing.fang
     * @date 2022/6/2 14:10
     * @param dataAll dataAll
     * @param response
     * @return void
    **/
    void exportAll(TableDataAll dataAll, HttpServletResponse response);

    /**
     * 导出人工巡视报表
     *
     * @description 导出人工巡视报表
     * @author jing.fang
     * @date 2022/6/6 10:28
     * @param data data
     * @param response response
     * @return void
    **/
    void exportXs(XsExportData data, HttpServletResponse response);

    /**
     * 导出水平分层报表
     *
     * @description 导出水平分层报表
     * @author jing.fang
     * @date 2022/6/6 10:24
     * @param dataCommon dataCommon
     * @param response response
     * @return void
    **/
    void exportFc(TableDataCommon dataCommon, HttpServletResponse response);

    /**
     * 导出常规任务报表
     *
     * @description 导出常规任务报表
     * @author jing.fang
     * @date 2022/6/6 10:24
     * @param dataCommon dataCommon
     * @param response response
     * @return void
    **/
    void exportCommon(TableDataCommon dataCommon, HttpServletResponse response);

}
