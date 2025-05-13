package com.dzkj.controller.equip;

import com.dzkj.biz.equipment.IEquipManagerBiz;
import com.dzkj.biz.equipment.vo.ControlBoxVO;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

@RestController
@RequestMapping("mt")
public class EquipManagerController {

    @Autowired
    private IEquipManagerBiz equipManagerBiz;

    @RequestMapping(value = "equip/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询设备信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getPage(@PathVariable("pageIndex") Integer pageIndex,
                                @PathVariable("pageSize") Integer pageSize,
                                @RequestBody EquipCondition cond) {
        return ResponseUtil.success(equipManagerBiz.getPage(pageIndex, pageSize, cond));
    }

    @RequestMapping(value = "equip/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil add(@RequestBody ControlBoxVO data) {
        return equipManagerBiz.add(data);
    }

    @RequestMapping(value = "equip/edit", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil update(@RequestBody ControlBoxVO data) {
        return equipManagerBiz.update(data);
    }

    @RequestMapping(value = "equip/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil delete(@PathVariable("id") Long id) {
        return equipManagerBiz.delete(id);
    }

    @RequestMapping(value = "equip/bind/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "绑定", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil bind(@PathVariable("id") Long id){
        return equipManagerBiz.bind(id);
    }

    @RequestMapping(value = "equip/unbind/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "解绑", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil  unbind(@PathVariable("id") Long id){
        return equipManagerBiz.unbind(id);
    }

    @RequestMapping(value = "common/equip/info/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询控制器统计信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getControlBoxTotalInfo(@PathVariable("companyId") Long companyId) {
        return ResponseUtil.success(equipManagerBiz.getControlBoxTotalInfo(companyId));
    }

    @RequestMapping(value = "common/equip/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询控制器显示信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getControlBox(@PathVariable("id") Long id) {
        return ResponseUtil.success(equipManagerBiz.getControlBox(id));
    }

    @RequestMapping(value = "common/equip/update", method = RequestMethod.POST)
    @SysOperateLog(value = "更新控制器设备信息", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil updateDeviceInfo(@RequestBody ControlBoxVO data) {
        return equipManagerBiz.updateDeviceInfo(data);
    }

    @RequestMapping(value = "common/equip/mete-online/{missionId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询任务关联在线温度气压控制器信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getOnlineMeteControlBoxList(@PathVariable("missionId") Long missionId) {
        return ResponseUtil.success(equipManagerBiz.getOnlineMeteControlBoxList(missionId));
    }

    @RequestMapping(value = "common/equip/{missionId}/{serialNo}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询控制器显示信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getControlBoxInfo(@PathVariable("missionId") Long missionId,
                                      @PathVariable("serialNo") String serialNo){
        return ResponseUtil.success(equipManagerBiz.getControlBoxInfo(missionId, serialNo));
    }

    @RequestMapping(value = "common/app/equip/list/{missionId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询设备信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_INFO)
    public ResponseUtil getListApp(@PathVariable("missionId") Long missionId) {
        return ResponseUtil.success(equipManagerBiz.getListApp(missionId));
    }


}
