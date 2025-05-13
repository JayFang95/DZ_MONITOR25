package com.dzkj.biz.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.biz.project.IProMissionBiz;
import com.dzkj.biz.project.IProjectBiz;
import com.dzkj.biz.project.vo.*;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.*;
import com.dzkj.entity.alarm_setting.AlarmDistribute;
import com.dzkj.entity.alarm_setting.AlarmGroup;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.file_store.FileStore;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.CustomDisplay;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.project.ProjectGroup;
import com.dzkj.entity.system.ProjectType;
import com.dzkj.service.alarm_setting.IAlarmDistributeService;
import com.dzkj.service.alarm_setting.IAlarmGroupService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.file_store.IFileStoreService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.project.ICustomDisplayService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectGroupService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IProjectTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目信息业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
@Slf4j
public class ProjectBizImpl implements IProjectBiz {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPtGroupService groupService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IProMissionBiz missionBiz;
    @Autowired
    private IProjectTypeService projectTypeService;
    @Autowired
    private ICustomDisplayService displayService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IAlarmGroupService alarmGroupService;
    @Autowired
    private IAlarmItemService alarmItemService;
    @Autowired
    private IAlarmDistributeService alarmDistributeService;
    @Autowired
    private IFileStoreService fileStoreService;
    @Autowired
    private ICommonBiz commonBiz;

    @Override
    public IPage<ProjectVO> getPage(Integer pageIndex, Integer pageSize, ProCondition cond) {
        if (cond.getProjectIds() == null || cond.getProjectIds().size()== 0){
            return new Page<>();
        }
        if(0==cond.getCompanyId()){
            cond.setCompanyId(companyService.getCurrentCompany());
        }
        Integer timeNum = cond.getTimeNum();
        if(timeNum !=null){
            cond.setCreateTime(DateUtil.getDateOfMonth(new Date(), -timeNum));
        }
        if (pageSize == CommonConstant.SEARCH_ALL_NO){
            Page<ProjectVO> page = new Page<>(pageIndex, pageSize);
            List<ProjectVO> list = projectService.getList(cond);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
            return page;
        }
        return projectService.getPage(pageIndex, pageSize, cond);
    }

    @Override
    public ResponseUtil addProject(ProjectVO project) {
        // 工程必须挂接到单位，没有单位不能创建
        if(0==project.getCompanyId()){
            Long currentCompany = companyService.getCurrentCompany();
            if (currentCompany == 0){
                return ResponseUtil.failure(500, "暂无授权单位，请先创建");
            }
            project.setCompanyId(currentCompany);
        }
        // 项目名称重复验证
        if(projectService.findByName(project).size() > 0){
            return ResponseUtil.failure(500, "项目名称重复");
        }
        // 新增工程设置报表默认期数（日，月，周，自选，全部）
        project.setExtraInfo("1,1,1,1,1");
        // 根据工程经纬度回去工程所属省份信息
        String province = EchartMapUtil.getProvince(project.getLng(), project.getLat());
        project.setProvince(province);
        Project copy = DzBeanUtils.propertiesCopy(project, Project.class);
        boolean b = projectService.save(copy);
        if(b){
            saveProjectGroup(project, copy);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil updateProject(ProjectVO project) {
        // 项目配置人员组已经使用时不能修改删除，更新工程时需要验证
        if(checkGroup(project)){
            return ResponseUtil.failure(500, "原人员配置已使用,请勿删除");
        }
        if(projectService.findByName(project).size() > 0){
            return ResponseUtil.failure(500, "项目名称重复");
        }
        // 根据工程经纬度回去工程所属省份信息
        String province = EchartMapUtil.getProvince(project.getLng(), project.getLat());
        project.setProvince(province);
        Project copy = DzBeanUtils.propertiesCopy(project, Project.class);
        boolean b = projectService.updateById(copy);
        if(b){
            projectGroupService.deleteByProjectId(project.getId());
            saveProjectGroup(project, copy);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil deleteProject(Long id, boolean flg) {
        Project project = projectService.getById(id);
        if(flg && project!=null && !project.getFinished()){
            return ResponseUtil.failure(500, "未结束项目不能删除");
        }
        boolean b = projectService.removeById(id);
        // 工程删除成功后需要删除工程关联表数据
        if(b){
            ThreadPoolUtil.getPool().execute(() -> {
                LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ProjectGroup::getProjectId, id);
                projectGroupService.remove(wrapper);
                deleteProRelate(id);
                deleteAlarmRelate(id);
                deleteFileRelate(id);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<DropVO> getList(Long userId) {
        // 查询 包含当前用户的人员编组信息
        List<Long> projectIds = projectGroupService.listByUserId(userId).stream().map(ProjectGroup::getProjectId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (projectIds.size() == 0) {
            return new ArrayList<>();
        }
        return DzBeanUtils.listCopy(projectService.list(new LambdaQueryWrapper<Project>()
                .in(Project::getId, projectIds).orderByDesc(Project::getCreateTime)), DropVO.class);
    }

    @Override
    public List<ProjectTypeVO> projectTypeList() {
        LambdaQueryWrapper<ProjectType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ProjectType::getCreateTime);
        return DzBeanUtils.listCopy(projectTypeService.list(wrapper), ProjectTypeVO.class);
    }

    @Override
    public List<Long> getProjectGroupList(Long projectId) {
        return projectGroupService.listByProjectId(projectId)
                .stream().map(ProjectGroup::getGroupId).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> dropList(Long userId) {
        HashMap<String, Object> map = new HashMap<>(16);
        // 查询 包含当前人员编组信息
        List<ProjectGroup> projectGroups = projectGroupService.listByUserId(userId);
        List<Long> projectIds = projectGroups.stream().map(ProjectGroup::getProjectId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> missionIds = projectGroups.stream().map(ProjectGroup::getMissionId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> ptGroupIds = projectGroups.stream().map(ProjectGroup::getPtGroupId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (projectIds.size() == 0) {
            map.put("mission", new ArrayList<>());
            map.put("group", new ArrayList<>());
            map.put("point", new ArrayList<>());
            map.put("display", new ArrayList<>());
            return map;
        }
        List<DropVO> projectList = DzBeanUtils.listCopy(projectService.list(new LambdaQueryWrapper<Project>()
                .in(Project::getId, projectIds).orderByDesc(Project::getCreateTime)), DropVO.class);
        map.put("project", projectList);
        if (projectList.size() > 0 && missionIds.size() > 0){
            List<ProMissionVO> missionList = DzBeanUtils.listCopy(missionService.getList(missionIds), ProMissionVO.class);
            map.put("mission", missionList);
            LambdaQueryWrapper<CustomDisplay> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(CustomDisplay::getMissionId, missionIds)
                    .orderByAsc(CustomDisplay::getSeq);
            List<CustomDisplay> list = displayService.list(queryWrapper);
            map.put("display", list);
            if(missionList.size() > 0 && ptGroupIds.size() > 0){
                List<PtGroupVO> groupList = DzBeanUtils.listCopy(groupService.list(new LambdaQueryWrapper<PtGroup>()
                        .in(PtGroup::getId, ptGroupIds).orderByDesc(PtGroup::getCreateTime)), PtGroupVO.class);
                map.put("group", groupList);
                if(groupList.size() > 0){
                    LambdaQueryWrapper<Point> wrapper3 = new LambdaQueryWrapper<>();
                    List<Long> groupIds = groupList.stream().map(PtGroupVO::getId).collect(Collectors.toList());
                    wrapper3.in(Point::getPtGroupId, groupIds)
                            .orderByAsc(Point::getName);
                    List<PointVO> pointList = DzBeanUtils.listCopy(pointService.list(wrapper3), PointVO.class);
                    map.put("point", pointList);
                }else {
                    map.put("point", new ArrayList<>());
                }
                for (ProMissionVO vo : missionList) {
                    LambdaQueryWrapper<CustomDisplay> wrapper3 = new LambdaQueryWrapper<>();
                    wrapper3.eq(CustomDisplay::getMissionId, vo.getId()).orderByAsc(CustomDisplay::getSeq);
                    List<CustomDisplay> displays = displayService.list(wrapper3);
                    if (displays.size() <= 0){
                        displays = list.stream().filter(item -> item.getType().equals(vo.getTypeName())).collect(Collectors.toList());
                    }
                    vo.setDisplayList(DzBeanUtils.listCopy(displays, CustomDisplayVO.class));
                }
            }else {
                map.put("group", new ArrayList<>());
                map.put("point", new ArrayList<>());
                map.put("display", new ArrayList<>());
            }
        }else {
            map.put("mission", new ArrayList<>());
            map.put("group", new ArrayList<>());
            map.put("point", new ArrayList<>());
            map.put("display", new ArrayList<>());
        }
        return map;
    }

    @Override
    public Map<String, Object> projectMissionList(Long userId) {
        HashMap<String, Object> map = new HashMap<>(16);
        // 查询 包含当前登陆的人员编组信息
        List<ProjectGroup> projectGroups = projectGroupService.listByUserId(userId);
        List<Long> projectIds = projectGroups.stream().map(ProjectGroup::getProjectId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> missionIds = projectGroups.stream().map(ProjectGroup::getMissionId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (projectIds.size() == 0) {
            map.put("project", new ArrayList<>());
            map.put("unFinishProject", new ArrayList<>());
            map.put("mission", new ArrayList<>());
            return map;
        }
        List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>()
                .in(Project::getId, projectIds)
                .select(Project::getId, Project::getName, Project::getFinished)
                .orderByDesc(Project::getCreateTime));
        List<DropVO> projectList = DzBeanUtils.listCopy(projects, DropVO.class);
        map.put("project", projectList);
        map.put("unFinishProject", DzBeanUtils.listCopy(projects.stream()
                .filter(pro -> !pro.getFinished()).collect(Collectors.toList()), DropVO.class));
        if (projectList.size() > 0 && missionIds.size() > 0){
            List<ProMissionVO> missionList = DzBeanUtils.listCopy(missionService.getList(missionIds), ProMissionVO.class);
            map.put("mission", missionList);
        }else {
            map.put("mission", new ArrayList<>());
        }
        return map;
    }

    @Override
    public Map<String, Object> projectMissionDropAll() {
        HashMap<String, Object> map = new HashMap<>(16);

        List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>()
                .select(Project::getId, Project::getName, Project::getFinished)
                .orderByDesc(Project::getCreateTime));
        List<DropVO> projectList = DzBeanUtils.listCopy(projects, DropVO.class);
        map.put("project", projectList);
        if (projectList.size() > 0){
            LambdaQueryWrapper<ProMission> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(ProMission::getProjectId, projectList.stream().map(DropVO::getId).collect(Collectors.toList()))
                    .select(ProMission::getId);
            List<ProMission> missions = missionService.list(wrapper);
            if (missions.size() > 0){
                List<Long> missionIds = missions.stream().map(ProMission::getId).collect(Collectors.toList());
                List<ProMissionVO> missionList = DzBeanUtils.listCopy(missionService.getList(missionIds), ProMissionVO.class);
                map.put("mission", missionList);
            }else {
                map.put("mission", new ArrayList<>());
            }
        }else {
            map.put("mission", new ArrayList<>());
        }
        return map;
    }

    @Override
    public Map<String, List<ProjectVO>> getProjectInfo(Long userId) {
        // 查询 包含当前用户的人员编组信息
        List<ProjectGroup> projectGroups = projectGroupService.listByUserId(userId);
        List<Long> proIds = projectGroups.stream().map(ProjectGroup::getProjectId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        HashMap<String, List<ProjectVO>> map = new HashMap<>(16);
        if(proIds.size() == 0){
            map.put("finish", new ArrayList<>());
            map.put("unFinish", new ArrayList<>());
            return map;
        }
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Project::getId, proIds).orderByDesc(Project::getCreateTime);
        List<ProjectVO> list = DzBeanUtils.listCopy(projectService.list(wrapper), ProjectVO.class);
        List<ProjectVO> finishList = list.stream().filter(ProjectVO::getFinished).collect(Collectors.toList());
        List<ProjectVO> unFinishList = list.stream().filter(item -> !item.getFinished()).collect(Collectors.toList());
        if (unFinishList.size() > 0){
            List<Long> projectIds = unFinishList.stream().map(ProjectVO::getId).collect(Collectors.toList());
            List<AlarmInfo> result = alarmInfoService.getMaxLevel(projectIds);
            unFinishList.forEach(item -> {
                Optional<AlarmInfo> optional = result.stream().filter(temp -> temp.getProjectId().equals(item.getId())).findAny();
                if (optional.isPresent()){
                    item.setLevel(optional.get().getAlarmLevel());
                }else {
                    item.setLevel("0");
                }
            });
        }
        unFinishList.sort(Comparator.comparing(ProjectVO::getLevel).reversed());
        map.put("finish", finishList);
        map.put("unFinish", unFinishList);
        return map;
    }

    //region 私有方法
    /**
     * 验证是否包含监测任务下所有人员配置
    **/
    private boolean checkGroup(ProjectVO project) {
        // 查询监测任务中使用的人员配置组集合
        List<Long> groupIds = projectGroupService.getMissionGroupIds(project.getId());
        if (groupIds.size()==0){
            return false;
        }
        return !project.getGroupsIds().containsAll(groupIds);
    }

    /**
     * 删除报警管理关联数据
     */
    private void deleteAlarmRelate(Long id) {
        // 删除报警组
        LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmGroup::getProjectId, id);
        List<AlarmGroup> groups = alarmGroupService.list(wrapper);
        if (groups.size() > 0){
            List<Long> alarmGroupIds = groups.stream().map(AlarmGroup::getId).collect(Collectors.toList());
            alarmGroupService.removeByIds(alarmGroupIds);
            LambdaQueryWrapper<AlarmItem> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.in(AlarmItem::getAlarmGroupId, alarmGroupIds);
            alarmItemService.remove(wrapper2);
        }
        // 删除报警分发
        LambdaQueryWrapper<AlarmDistribute> wrapper3 = new LambdaQueryWrapper<>();
        wrapper3.eq(AlarmDistribute::getProjectId, id);
        alarmDistributeService.remove(wrapper3);
    }

    /**
     * 删除文件相关
     */
    private void deleteFileRelate(Long id) {
        LambdaQueryWrapper<FileStore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileStore::getProjectId, id);
        List<FileStore> fileStores = fileStoreService.list(wrapper);
        if (fileStores.size() > 0){
            List<String> collect = fileStores.stream().map(FileStore::getFileIds).filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (collect.size() > 0){
                List<Long> fileIds = new ArrayList<>();
                for (String s : collect) {
                    Arrays.stream(s.split(",")).forEach(item -> fileIds.add(Long.valueOf(item)));
                    commonBiz.deleteFileByIds(fileIds);
                }
            }
            fileStoreService.remove(wrapper);
        }
    }

    /**
     * 删除工程管理关联数据
     */
    private void deleteProRelate(Long id) {
        // 删除任务相关
        List<ProMission> missions = missionService.getMissionByProjectId(id);
        if (missions.size() > 0){
            missionBiz.deleteMission(missions.stream().map(ProMission::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 保存人员配置信息
     */
    private void saveProjectGroup(ProjectVO project, Project copy) {
        List<Long> groupsIds = project.getGroupsIds();
        ArrayList<ProjectGroup> groups = new ArrayList<>();
        groupsIds.forEach(groupId -> {
            ProjectGroup projectGroup = new ProjectGroup().setProjectId(copy.getId()).setGroupId(groupId);
            groups.add(projectGroup);
        });
        projectGroupService.saveBatch(groups);
    }
    //endregion

}
