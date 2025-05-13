package com.dzkj.controller.dashboard;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/14
 * @description 信息总览
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import com.dzkj.biz.dashoborad.IDashboardBiz;
import com.dzkj.biz.dashoborad.vo.InfoData;
import com.dzkj.biz.project.IProjectBiz;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("mt/common")
public class DashboardController {

    @Autowired
    private IDashboardBiz dashboardBiz;
    @Autowired
    private IProjectBiz projectBiz;

    /**
     * 查询首页统计信息
     */
    @RequestMapping(value = "dashboard/info/{userId}", method = RequestMethod.POST)
    public ResponseUtil getDashboardInfo(@RequestBody InfoData infoData,@PathVariable("userId") Long userId){
        return ResponseUtil.success(dashboardBiz.getDashboardInfo(infoData, userId));
    }

    /**
     * 项目分布查询
     */
    @RequestMapping(value = "dashboard/project/{userId}", method = RequestMethod.GET)
    @SysOperateLog(value = "项目分布查询", type = LogConstant.RETRIEVE, modelName = LogConstant.DASHBOARD_PROJECT)
    public ResponseUtil getProjectInfo(@PathVariable("userId") Long userId){
        return ResponseUtil.success(projectBiz.getProjectInfo(userId));
    }

    /**
     * 查询大屏统计信息
     */
    @RequestMapping(value = "dashboard/screen/{companyId}", method = RequestMethod.GET)
    public ResponseUtil getDashboardScreenInfo(@PathVariable("companyId") Long companyId){
        return ResponseUtil.success(dashboardBiz.getDashboardScreenInfo(companyId));
    }
}
