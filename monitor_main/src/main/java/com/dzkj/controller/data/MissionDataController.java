package com.dzkj.controller.data;

import com.dzkj.biz.data.IDataMissionBiz;
import com.dzkj.biz.data.vo.MissionDataCondition;
import com.dzkj.biz.data.vo.MissionDataExport;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description 任务数据controller
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt")
public class MissionDataController {

    @Autowired
    private IDataMissionBiz dataMissionBiz;

    @RequestMapping(value = "data/mission/page/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "分页查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public ResponseUtil getPage(@PathVariable("pi")Integer pi, @PathVariable("ps")Integer ps,
                               @RequestBody MissionDataCondition condition){
        return dataMissionBiz.getPage(pi, ps, condition);
    }

    @RequestMapping(value = "data/mission/export/{missionId}", method = RequestMethod.POST)
    @SysOperateLog(value = "导出列表", type = LogConstant.RETRIEVE, modelName = LogConstant.DATA_MISSION)
    public void exportList(@PathVariable("missionId")Long missionId, @RequestBody MissionDataExport dataExport, HttpServletResponse response){
        dataMissionBiz.exportList(missionId, dataExport, response);
    }

}
