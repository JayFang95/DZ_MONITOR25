package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.IPtGroupBiz;
import com.dzkj.biz.param_set.vo.GroupCondition;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 参数设置-点组controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class PtGroupController {

    @Autowired
    private IPtGroupBiz ptGroupBiz;

    @RequestMapping(value = "pt_group/list/{pi}/{ps}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_PTGROUP)
    public ResponseUtil list(@PathVariable("pi") Integer pi, @PathVariable("ps") Integer ps,
                             @RequestBody GroupCondition condition){
        return ResponseUtil.success(ptGroupBiz.getList(pi, ps, condition));
    }

    @RequestMapping(value = "pt_group/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_PTGROUP)
    public ResponseUtil add(@RequestBody PtGroupVO ptGroup){
        return ptGroupBiz.add(ptGroup);
    }

    @RequestMapping(value = "pt_group/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_PTGROUP)
    public ResponseUtil update(@RequestBody PtGroupVO ptGroup){
        return ptGroupBiz.update(ptGroup);
    }

    @RequestMapping(value = "pt_group/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.PROJECT_PTGROUP)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return ptGroupBiz.delete(id);
    }

    /**
     * 查询编组人员配置信息
     */
    @RequestMapping(value = "common/pt_group/group/{ptGroupId}", method = RequestMethod.GET)
    public ResponseUtil getPtGroupGroupList(@PathVariable("ptGroupId") Long ptGroupId){
        return ResponseUtil.success(ptGroupBiz.getPtGroupGroupList(ptGroupId));
    }

    /**
     * 查询编组下拉
     */
    @RequestMapping(value = "common/pt_group/list", method = RequestMethod.POST)
    public ResponseUtil list(@RequestBody List<Long> missionIds){
        return ResponseUtil.success(ptGroupBiz.list(missionIds));
    }

    /**
     * 查询编组及下测点集合
    **/
    @RequestMapping(value = "common/pt_group/list/{missionId}", method = RequestMethod.GET)
    public ResponseUtil groupPtList(@PathVariable Long missionId){
        return ResponseUtil.success(ptGroupBiz.groupPtList(missionId));
    }
    /**
     * 查询编组及下测点集合
     **/
    @RequestMapping(value = "common/app/pt_group/list/{missionId}", method = RequestMethod.GET)
    public ResponseUtil groupPtAppList(@PathVariable Long missionId){
        return ResponseUtil.success(ptGroupBiz.groupPtAppList(missionId));
    }


}
