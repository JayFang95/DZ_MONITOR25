package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.IPointBiz;
import com.dzkj.biz.param_set.vo.PointVO;
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
 * @date 2021/9/7
 * @description 参数设置-测点controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PointController {

    @Autowired
    private IPointBiz pointBiz;

    @RequestMapping(value = "point/list/{groupId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil list(@PathVariable("groupId") Long groupId){
        return ResponseUtil.success(pointBiz.getList(groupId));
    }

    @RequestMapping(value = "point/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil add(@RequestBody PointVO point){
        return pointBiz.add(point);
    }

    @RequestMapping(value = "point/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil update(@RequestBody PointVO point){
        return pointBiz.update(point);
    }

    @RequestMapping(value = "point/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return pointBiz.delete(id);
    }

    @RequestMapping(value = "point/add/batch", method = RequestMethod.POST)
    @SysOperateLog(value = "批量添加", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil saveBatch(@RequestBody List<PointVO> list){
        return pointBiz.saveBatch(list);
    }

    @RequestMapping(value = "point/import", method = RequestMethod.POST)
    @SysOperateLog(value = "批量导入", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil exportBatch(@RequestBody List<PointVO> list){
        return pointBiz.importBatch(list);
    }

    @RequestMapping(value = "common/point/export/{projectId}", method = RequestMethod.GET)
    @SysOperateLog(value = "批量导出", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_POINT)
    public void exportBatch(@PathVariable("projectId") Long projectId, HttpServletResponse response){
        pointBiz.exportBatch(projectId, response);
    }

    @RequestMapping(value = "point/change", method = RequestMethod.POST)
    @SysOperateLog(value = "停测/开测", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil changeStatus(@RequestBody PointVO point){
        return pointBiz.changeStatus(point);
    }

    @RequestMapping(value = "point/up", method = RequestMethod.POST)
    @SysOperateLog(value = "上移测点", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil upPoint(@RequestBody List<PointVO> points){
        return pointBiz.changePointSeq(points);
    }

    @RequestMapping(value = "point/down", method = RequestMethod.POST)
    @SysOperateLog(value = "下移测点", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_POINT)
    public ResponseUtil downPoint(@RequestBody List<PointVO> points){
        return pointBiz.changePointSeq(points);
    }

    /**
     * 查询监测任务包含测点集合
     */
    @RequestMapping(value = "common/point/list/{missionId}", method = RequestMethod.GET)
    public ResponseUtil queryByMissionId(@PathVariable("missionId") Long missionId){
        return ResponseUtil.success(pointBiz.queryByMissionId(missionId));
    }

}
