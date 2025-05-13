package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.ISensorZlBiz;
import com.dzkj.biz.param_set.vo.SensorZlVO;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 传感器信息业务controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/sensor")
public class SensorZlController {

    @Autowired
    private ISensorZlBiz sensorZlBiz;

    @RequestMapping(value = "list/{pointId}", method = RequestMethod.GET)
    public ResponseUtil getList(@PathVariable("pointId") Long pointId){
        return ResponseUtil.success(sensorZlBiz.getList(pointId));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseUtil add(@RequestBody SensorZlVO data){
        return sensorZlBiz.add(data);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseUtil update(@RequestBody SensorZlVO data){
        return sensorZlBiz.update(data);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return sensorZlBiz.delete(id);
    }

    @RequestMapping(value = "export/{missionId}", method = RequestMethod.GET)
    public void exportData(@PathVariable("missionId") Long missionId, HttpServletResponse response){
        sensorZlBiz.exportData(missionId, response);
    }

    @RequestMapping(value = "import/{missionId}", method = RequestMethod.POST)
    public ResponseUtil importData(@PathVariable("missionId") Long missionId, @RequestBody List<SensorZlVO> list){
        return sensorZlBiz.importData(missionId, list);
    }

    /**
     * 查询工程内列表
     */
    @RequestMapping(value = "pro_list/{projectId}", method = RequestMethod.GET)
    public ResponseUtil getListInProject(@PathVariable("projectId") Long projectId){
        return ResponseUtil.success(sensorZlBiz.getListInProject(projectId));
    }

}
