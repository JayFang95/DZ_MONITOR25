package com.dzkj.controller.project;


import com.dzkj.biz.project.IProMissionBiz;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 监测任务controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class ProMissionController {

    @Autowired
    private IProMissionBiz missionBiz;

    @RequestMapping(value = "mission/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil page(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody ProMissionCondition cond){
        return ResponseUtil.success(missionBiz.getPage(pageIndex, pageSize, cond));
    }

    @RequestMapping(value = "mission/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil add(@RequestBody ProMissionVO mission){
        return missionBiz.addMission(mission);
    }

    @RequestMapping(value = "mission/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil update(@RequestBody ProMissionVO mission){
        return missionBiz.updateMission(mission);
    }

    @RequestMapping(value = "mission/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil add(@PathVariable("id") Long id){
        return missionBiz.deleteMission(id, true);
    }

    /**
     * 查询指定用户可访问监测任务
     */
    @RequestMapping(value = "common/mission/list/{userId}", method = RequestMethod.GET)
    public ResponseUtil getMissionList(@PathVariable("userId") Long userId){
        return ResponseUtil.success(missionBiz.getMissionList(userId));
    }

    /**
     * 查询监测任务人员配置
     */
    @RequestMapping(value = "common/mission/group/{missionId}", method = RequestMethod.GET)
    public ResponseUtil getMissionGroupList(@PathVariable("missionId") Long missionId){
        return ResponseUtil.success(missionBiz.getMissionGroupList(missionId));
    }

    @RequestMapping(value = "mission/up", method = RequestMethod.POST)
    @SysOperateLog(value = "上移", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil up(@RequestBody ProMissionVO mission){
        return missionBiz.updateIdx(mission, 1);
    }

    @RequestMapping(value = "mission/down", method = RequestMethod.POST)
    @SysOperateLog(value = "下移", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_MISSION)
    public ResponseUtil down(@RequestBody ProMissionVO mission){
        return missionBiz.updateIdx(mission, 2);
    }

    /**
     * 查询单位中自动化监测监测任务
     */
    @RequestMapping(value = "common/mission/monitor/{companyId}/{missionId}", method = RequestMethod.GET)
    public ResponseUtil getMissionInCompany(@PathVariable("companyId") Long companyId, @PathVariable("missionId") Long missionId){
        return ResponseUtil.success(missionBiz.getMissionInCompany(companyId, missionId));
    }

    /**
     * 查询单位中自动化监测监测任务
     */
    @RequestMapping(value = "common/mission/monitor/other/{companyId}/{missionId}", method = RequestMethod.GET)
    public ResponseUtil getMissionOtherInCompany(@PathVariable("companyId") Long companyId, @PathVariable("missionId") Long missionId){
        return ResponseUtil.success(missionBiz.getMissionOtherInCompany(companyId, missionId));
    }
}
