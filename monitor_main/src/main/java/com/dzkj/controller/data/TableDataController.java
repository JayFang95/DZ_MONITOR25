package com.dzkj.controller.data;

import com.dzkj.biz.data.IDataTableBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description 报表数据controller
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt")
public class TableDataController {

    @Autowired
    private IDataTableBiz dataTableBiz;

    @RequestMapping(value = "data/table/search/all", method = RequestMethod.POST)
    @SysOperateLog(value = "报表统计查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_TABLE)
    public ResponseUtil getAllData(@RequestBody TableDataCondition condition){
        return ResponseUtil.success(dataTableBiz.getAllData(condition));
    }

    @RequestMapping(value = "data/table/search/xs", method = RequestMethod.POST)
    @SysOperateLog(value = "巡视类型查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_TABLE)
    public ResponseUtil getXsData(@RequestBody TableDataCondition condition){
        return ResponseUtil.success(dataTableBiz.getXsData(condition));
    }

    @RequestMapping(value = "data/table/search/common", method = RequestMethod.POST)
    @SysOperateLog(value = "常规类型查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_TABLE)
    public ResponseUtil getListData(@RequestBody TableDataCondition condition){
        return dataTableBiz.getListData(condition);
    }

    @RequestMapping(value = "common/data/save/chart", method = RequestMethod.POST)
    public ResponseUtil saveImag(@RequestBody EchartData echartData){
        return ResponseUtil.success(dataTableBiz.saveChartImag(echartData));
    }

    @RequestMapping(value = "data/table/export/all", method = RequestMethod.POST)
    @SysOperateLog(value = "导出列表", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public void exportAll(@RequestBody TableDataAll dataAll, HttpServletResponse response){
        dataTableBiz.exportAll(dataAll, response);
    }

    @RequestMapping(value = "data/table/export/xs", method = RequestMethod.POST)
    @SysOperateLog(value = "导出人工巡视列表", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public void exportXs(@RequestBody XsExportData data, HttpServletResponse response){
        dataTableBiz.exportXs(data, response);
    }

    @RequestMapping(value = "data/table/export/fc", method = RequestMethod.POST)
    @SysOperateLog(value = "导出水平分层列表", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public void exportFc(@RequestBody TableDataCommon dataCommon, HttpServletResponse response){
        dataTableBiz.exportFc(dataCommon, response);
    }

    @RequestMapping(value = "data/table/export/common", method = RequestMethod.POST)
    @SysOperateLog(value = "导出常规列表", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public void exportCommon(@RequestBody TableDataCommon dataCommon, HttpServletResponse response){
        dataTableBiz.exportCommon(dataCommon, response);
    }

}
