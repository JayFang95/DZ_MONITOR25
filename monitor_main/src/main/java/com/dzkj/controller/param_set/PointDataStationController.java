package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.IPointDataStationBiz;
import com.dzkj.biz.param_set.vo.PointDataStationVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11
 * @description 测站配置点信息controller
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PointDataStationController {

    @Autowired
    private IPointDataStationBiz pointDataStationBiz;

    @RequestMapping(value = "common/app/point/data/list", method = RequestMethod.POST)
    @SysOperateLog(value = "查询测站配置点", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil list(@RequestBody List<Long> pidList){
        return ResponseUtil.success(pointDataStationBiz.list(pidList));
    }

    @RequestMapping(value = "common/app/point/save", method = RequestMethod.POST)
    @SysOperateLog(value = "更新测站配置点", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil saveOrUpdate(@RequestBody List<PointDataStationVO> list){
        return pointDataStationBiz.saveOrUpdate(list);
    }

}
