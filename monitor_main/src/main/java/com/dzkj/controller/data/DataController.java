package com.dzkj.controller.data;

import com.dzkj.biz.data.IDataBiz;
import com.dzkj.biz.data.IDataPtBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

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
@RestController
@RequestMapping("mt")
public class DataController {

    @Autowired
    private IDataBiz dataBiz;
    @Autowired
    private IDataPtBiz dataPtBiz;

    @RequestMapping(value = "data/upload", method = RequestMethod.POST)
    @SysOperateLog(value = "数据上传", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_UPLOAD)
    public ResponseUtil upload(UploadData uploadData, MultipartFile file){
        return dataBiz.upload(uploadData, file);
    }

    @RequestMapping(value = "common/data/download/{missionId}/{secondId}", method = RequestMethod.GET)
    @SysOperateLog(value = "模板下载", type = LogConstant.CREATE, modelName = LogConstant.DATA_UPLOAD)
    public void download(@PathVariable("missionId")Long missionId,
                         @PathVariable("secondId")Long secondId,
                         HttpServletResponse response){
        dataBiz.download(missionId, secondId, response);
    }

    @RequestMapping(value = "data/save", method = RequestMethod.POST)
    @SysOperateLog(value = "数据入库", type = LogConstant.CREATE, modelName = LogConstant.DATA_UPLOAD)
    public ResponseUtil save(@RequestBody UploadDataCb dataCb){
        return dataBiz.saveToDb(dataCb);
    }

    @RequestMapping(value = "data/page/pt/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil ptPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody PtDataCondition condition){
        if (condition.getIsXyz()){
            return ResponseUtil.success(dataPtBiz.xyzhPage(pi, ps, condition));
        }
        return ResponseUtil.success(dataPtBiz.zPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/init/pt", method = RequestMethod.POST)
    @SysOperateLog(value = "数据初始化", type = LogConstant.UPDATE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil dataInit(@RequestBody PtDataCondition condition){
        return dataPtBiz.dataInit(condition);
    }

    @RequestMapping(value = "data/page/pt_xs/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil ptXsPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody PtDataCondition condition){
        return ResponseUtil.success(dataPtBiz.xsPage(pi, ps, condition));
    }

    @RequestMapping(value = "data/init/pt_xs/{missionId}", method = RequestMethod.POST)
    @SysOperateLog(value = "数据初始化", type = LogConstant.UPDATE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil dataXsInit(@PathVariable("missionId")Long missionId){
        return dataPtBiz.dataXsInit(missionId);
    }

    @RequestMapping(value = "data/pt_xs/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil deleteXs(@PathVariable("id")Long id){
        return dataPtBiz.deleteXs(id);
    }

    @RequestMapping(value = "data/pt/calculate", method = RequestMethod.POST)
    @SysOperateLog(value = "重新计算", type = LogConstant.UPDATE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil dataCalculate(@RequestBody PtDataCalculate calculate){
        return dataPtBiz.dataCalculate(calculate);
    }

    @RequestMapping(value = "common/app/data/pt/chart", method = RequestMethod.POST)
    @SysOperateLog(value = "获取app图表分析数据", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_POINT)
    public ResponseUtil dataListChart(@RequestBody PtDataChartCondition condition){
        if (condition.getSelectIds() == null || condition.getSelectIds().size() == 0) {
            return ResponseUtil.success(new ArrayList<>());
        }
        if (condition.getIsXyz()){
            return ResponseUtil.success(dataPtBiz.dataXyzhListChart(condition));
        }
        return ResponseUtil.success(dataPtBiz.dataListChart(condition));
    }

    @RequestMapping(value = "common/app/data/upload/{index}", method = RequestMethod.POST)
    @SysOperateLog(value = "数据上传app", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_UPLOAD)
    public ResponseUtil uploadApp(@PathVariable("index")int index, @RequestBody UploadDataApp uploadDataApp){
        return dataBiz.uploadApp(index, uploadDataApp);
    }

    @RequestMapping(value = "common/app/data/upload/list/zl/{groupId}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取轴力数据模版", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_UPLOAD)
    public ResponseUtil getZlUploadData(@PathVariable("groupId")Long groupId){
        return dataBiz.getZlUploadData(groupId);
    }
}
