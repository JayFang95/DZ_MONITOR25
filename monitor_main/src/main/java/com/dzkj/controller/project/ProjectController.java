package com.dzkj.controller.project;


import com.dzkj.biz.project.IProjectBiz;
import com.dzkj.biz.project.vo.ProCondition;
import com.dzkj.biz.project.vo.ProjectVO;
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
 * @description 项目信息controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class ProjectController {

    @Autowired
    private IProjectBiz projectBiz;

    @RequestMapping(value = "project/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.PROJECT_INFO)
    public ResponseUtil page(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody ProCondition cond){
        return ResponseUtil.success(projectBiz.getPage(pageIndex, pageSize, cond));
    }

    @RequestMapping(value = "project/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.PROJECT_INFO)
    public ResponseUtil add(@RequestBody ProjectVO project){
        return projectBiz.addProject(project);
    }

    @RequestMapping(value = "project/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.PROJECT_INFO)
    public ResponseUtil update(@RequestBody ProjectVO project){
        return projectBiz.updateProject(project);
    }

    @RequestMapping(value = "project/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.PROJECT_INFO)
    public ResponseUtil add(@PathVariable("id") Long id){
        return projectBiz.deleteProject(id, true);
    }

    /**
     * 查询项目下拉集合
     */
    @RequestMapping(value = "common/project/list/{userId}", method = RequestMethod.GET)
    public ResponseUtil projectList(@PathVariable("userId") Long userId){
        return ResponseUtil.success(projectBiz.getList(userId));
    }

    /**
     * 查询项目类型下拉集合
     */
    @RequestMapping(value = "common/project/type/list", method = RequestMethod.GET)
    public ResponseUtil projectTypeList(){
        return ResponseUtil.success(projectBiz.projectTypeList());
    }

    /**
     * 查询项目人员配置
     */
    @RequestMapping(value = "common/project/group/{projectId}", method = RequestMethod.GET)
    public ResponseUtil getProjectGroupList(@PathVariable("projectId") Long projectId){
        return ResponseUtil.success(projectBiz.getProjectGroupList(projectId));
    }

    /**
     * 查询用户可见项目下拉综合信息
     */
    @RequestMapping(value = "common/project/drop/{userId}", method = RequestMethod.GET)
    public ResponseUtil dropList(@PathVariable("userId") Long userId){
        return ResponseUtil.success(projectBiz.dropList(userId));
    }

    /**
     * 查询项目与任务集合
     */
    @RequestMapping(value = "common/project/mission/drop/{userId}", method = RequestMethod.GET)
    public ResponseUtil projectMissionDrop(@PathVariable("userId") Long userId){
        return ResponseUtil.success(projectBiz.projectMissionList(userId));
    }

    /**
     * 查询项目与任务集合
     */
    @RequestMapping(value = "common/project/mission/all_drop", method = RequestMethod.GET)
    public ResponseUtil projectMissionDropAll(){
        return ResponseUtil.success(projectBiz.projectMissionDropAll());
    }

}
