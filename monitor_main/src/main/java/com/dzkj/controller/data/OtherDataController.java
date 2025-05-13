package com.dzkj.controller.data;

import com.dzkj.biz.data.IDataOtherBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description 其他数据controller
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt")
public class OtherDataController {

    @Autowired
    private IDataOtherBiz dataOtherBiz;

    @RequestMapping(value = "data/other/page/xyz/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "全站仪三维数据分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil xyzPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody OtherDataCondition condition){
        return ResponseUtil.success(dataOtherBiz.getXyzPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/other/page/xyz_hand/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "手动三维数据分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil xyzHandPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                                @RequestBody OtherDataCondition condition){
        return ResponseUtil.success(dataOtherBiz.xyzHandPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/other/page/xy/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "水平位移xy分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil xyPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                                 @RequestBody OtherDataCondition condition){
        return ResponseUtil.success(dataOtherBiz.getXyPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/other/page/zl/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "轴力分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil zlPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody OtherDataCondition condition){
        return ResponseUtil.success(dataOtherBiz.getZlPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/other/stop/page/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "停测记录查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil ptStopPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody PtDataStopCondition condition){
        return ResponseUtil.success(dataOtherBiz.getStopPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/other/data/delete", method = RequestMethod.POST)
    @SysOperateLog(value = "删除监测数据", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public ResponseUtil deleteData(@RequestBody OtherDataCondition condition){
        return ResponseUtil.success(dataOtherBiz.deleteData(condition));
    }

    @RequestMapping(value = "data/other/export/xy", method = RequestMethod.POST)
    @SysOperateLog(value = "水平位移数据导出", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public void xyExport(@RequestBody List<PointDataXyzVO> list, HttpServletResponse response){
        dataOtherBiz.xyExport(list, response);
    }

    @RequestMapping(value = "data/other/export/zl", method = RequestMethod.POST)
    @SysOperateLog(value = "轴力数据导出", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public void zlExport(@RequestBody List<PointDataZlVO> list, HttpServletResponse response){
        dataOtherBiz.zlExport(list, response);
    }

    @RequestMapping(value = "data/other/export/stop", method = RequestMethod.POST)
    @SysOperateLog(value = "停测数据导出", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_OTHER)
    public void stopExport(@RequestBody List<PtStopVO> list, HttpServletResponse response){
        dataOtherBiz.stopExport(list, response);
    }

    @RequestMapping(value = "common/data/other/download/check/{filename}", method = RequestMethod.GET)
    @SysOperateLog(value = "测量过程下载验证", type = LogConstant.CREATE, modelName = LogConstant.DATA_UPLOAD)
    public ResponseUtil downloadProcessCheck(@PathVariable("filename") String filename){
        return dataOtherBiz.downloadProcessCheck(filename);
    }

    @RequestMapping(value = "common/data/other/download", method = RequestMethod.GET)
    @SysOperateLog(value = "测量过程下载", type = LogConstant.CREATE, modelName = LogConstant.DATA_UPLOAD)
    public void downloadProcess(@RequestParam("fileName")String fileName,
                         HttpServletResponse response){
        dataOtherBiz.downloadProcess(fileName, response);
    }

}
