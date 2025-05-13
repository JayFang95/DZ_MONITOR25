package com.dzkj.biz.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.alarm_setting.IAlarmInfoBiz;
import com.dzkj.biz.project.IProMissionBiz;
import com.dzkj.biz.project.vo.CustomDisplayVO;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.data.PushPoint;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.data.PushTaskRecord;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.CustomDisplay;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.ProjectGroup;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.*;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.param_set.*;
import com.dzkj.service.project.ICustomDisplayService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectGroupService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/3
 * @description 监测任务业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class ProMissionBizImpl implements IProMissionBiz {

    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private ISectionService sectionService;
    @Autowired
    private ISensorZlService sensorZlService;
    @Autowired
    private ITypeZlService typeZlService;
    @Autowired
    private ICustomDisplayService displayService;
    @Autowired
    private IJcInfoService jcInfoService;
    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataZRealService dataZRealService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IPointDataXyzService dataXyzService;
    @Autowired
    private IPointDataZlService dataZlService;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IRobotSurveyRecordService surveyRecordService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IAlarmInfoBiz alarmInfoBiz;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IPushTaskRecordService pushTaskRecordService;
    @Autowired
    private IPushPointService pushPointService;

    @Override
    public IPage<ProMissionVO> getPage(Integer pageIndex, Integer pageSize, ProMissionCondition cond) {
        if (cond.getMissionIds() == null || cond.getMissionIds().size() == 0){
            return new Page<>(pageIndex, pageSize, 0);
        }
        Integer timeNum = cond.getTimeNum();
        if(timeNum !=null){
            cond.setCreateTime(DateUtil.getDateOfMonth(new Date(), -timeNum));
        }
        if (pageSize == CommonConstant.SEARCH_ALL_NO){
            Page<ProMissionVO> page = new Page<>(pageIndex, pageSize);
            List<ProMissionVO> list = missionService.getList(cond);
            list.forEach(item -> {
                if (StringUtils.isEmpty(item.getNoDataAlarmGroupIdStr())){
                    item.setNoDataAlarmGroupsIds(new ArrayList<>());
                }else {
                    item.setNoDataAlarmGroupsIds(
                            Arrays.stream(item.getNoDataAlarmGroupIdStr().split(","))
                                    .map(Long::parseLong).collect(Collectors.toList()));
                }
            });
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
            return page;
        }
        IPage<ProMissionVO> page = missionService.getPage(pageIndex, pageSize, cond);
        page.getRecords().forEach(item -> {
            if (StringUtils.isEmpty(item.getNoDataAlarmGroupIdStr())){
                item.setNoDataAlarmGroupsIds(new ArrayList<>());
            }else {
                item.setNoDataAlarmGroupsIds(
                        Arrays.stream(item.getNoDataAlarmGroupIdStr().split(","))
                        .map(Long::parseLong).collect(Collectors.toList()));
            }
        });
        return page;
    }

    @Override
    public ResponseUtil addMission(ProMissionVO mission) {
        if(missionService.findByName(mission).size()>0){
            return ResponseUtil.failure(500, "监测名称项目内重复");
        }
        // 添加序号
        mission.setIdx(missionService.getIndex(mission) + 1);
        //漏测接收人员
        setNoDataGroupIds(mission);
        ProMission copy = DzBeanUtils.propertiesCopy(mission, ProMission.class);
        boolean b = missionService.save(copy);
        if(b){
            ThreadPoolUtil.getPool().execute(() -> {
                saveMissionGroup(mission, copy);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil updateMission(ProMissionVO mission) {
        if(missionService.findByName(mission).size()>0){
            return ResponseUtil.failure(500, "监测名称项目内重复");
        }
        if(checkGroup(mission)){
            return ResponseUtil.failure(500, "原人员配置已使用,请勿删除");
        }
        setNoDataGroupIds(mission);
        ProMission copy = DzBeanUtils.propertiesCopy(mission, ProMission.class);
        boolean b = missionService.updateById(copy);
        if(b){
            ThreadPoolUtil.getPool().execute(() -> {
                projectGroupService.deleteByMissionId(mission.getId());
                saveMissionGroup(mission, copy);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil deleteMission(Long id, boolean flg) {
        ProMission mission = missionService.getById(id);
        if(flg && mission!=null && !mission.getFinished()){
            return ResponseUtil.failure(500, "删除错误：监测任务未完成");
        }
        return deleteMission(Collections.singletonList(id));
    }

    @Override
    public ResponseUtil deleteMission(List<Long> missionIds) {
        boolean b = missionService.removeByIds(missionIds);
        if(b){
            ThreadPoolUtil.getPool().execute(() -> {
                // 删除任务相关
                List<Long> ptIds = deleteProRelate(missionIds);
                // 删除报警信息
                List<AlarmInfo> alarmInfos = alarmInfoService.listByMissionIds(missionIds);
                if (alarmInfos.size() > 0){
                    List<Long> infoIds = alarmInfos.stream().map(AlarmInfo::getId).collect(Collectors.toList());
                    alarmInfoBiz.delete(infoIds);
                }
                // 删除设备信息
                controlBoxService.removeByMissionIds(missionIds);
                surveyControlService.removeByMissionIds(missionIds);
                surveyDataService.removeByMissionIds(missionIds);
                surveyRecordService.removeByMissionIds(missionIds);
                //删除推送相关
                deletePushTask(missionIds);
                // 删除巡视数据
                jcInfoService.removeByMissionIds(missionIds);
                if(ptIds.size() > 0){
                    // 删除监测数据
                    dataZService.removeByPtIds(ptIds);
                    dataZRealService.removeByPtIds(ptIds);
                    dataXyzhService.removeByPtIds(ptIds);
                    dataXyzhRealService.removeByPtIds(ptIds);
                    dataXyzService.removeByPtIds(ptIds);
                    dataZlService.removeByPtIds(ptIds);
                }
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<ProMissionVO> getMissionList(Long userId) {
        // 查询 包含当前用户的人员编组信息
        List<ProjectGroup> projectGroups = projectGroupService.listByUserId(userId);
        List<Long> missionIds = projectGroups.stream().map(ProjectGroup::getMissionId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (missionIds.size() == 0){
            return new ArrayList<>();
        }
        return DzBeanUtils.listCopy(missionService.list(new LambdaQueryWrapper<ProMission>()
                .in(ProMission::getId, missionIds).orderByDesc(ProMission::getCreateTime)), ProMissionVO.class);
    }

    @Override
    public List<Long> getMissionGroupList(Long missionId) {
        List<ProjectGroup> groups = projectGroupService.listByMissionId(missionId);
        return groups.stream().map(ProjectGroup::getGroupId).distinct().collect(Collectors.toList());
    }

    @Override
    public ResponseUtil updateIdx(ProMissionVO mission, int type) {
        LambdaQueryWrapper<ProMission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProMission::getProjectId, mission.getProjectId()).orderByDesc(ProMission::getIdx);
        List<ProMission> list = missionService.list(wrapper);
        ProMission current = new ProMission();
        ProMission up;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(mission.getId())){
                index = i;
                current = list.get(i);
            }
        }
        if (type == 1){
            if (index == 0){
                return ResponseUtil.failure(500, "当前任务位于所属工程最高位");
            }
            up = list.get(index -1);
        }else {
            if (index == list.size()-1){
                return ResponseUtil.failure(500, "当前任务位于所属工程最低位");
            }
            up = list.get(index +1);
        }
        int to = up.getIdx();
        up.setIdx(current.getIdx());
        current.setIdx(to);
        List<ProMission> missions = Arrays.asList(current, up);
        boolean b = missionService.updateBatchById(missions);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "监测任务排序失败");
    }

    @Override
    public List<ProMissionVO> getMissionInCompany(Long companyId, Long missionId) {
        if (companyId == 0) {
            return new ArrayList<>();
        }
        return DzBeanUtils.listCopy(missionService.getMissionInCompany(companyId, missionId),ProMissionVO.class);
    }

    @Override
    public List<ProMissionVO> getMissionOtherInCompany(Long companyId, Long missionId) {
        if (companyId == 0) {
            return new ArrayList<>();
        }
        return DzBeanUtils.listCopy(missionService.getMissionOtherInCompany(companyId, missionId),ProMissionVO.class);
    }
    //region

    /**
     * 设置漏测报警接收人
     * @param mission mission
     */
    private void setNoDataGroupIds(ProMissionVO mission) {
        //漏测接收人员
        if (mission.getNoDataAlarmGroupsIds() != null && mission.getNoDataAlarmGroupsIds().size() > 0 ){
            StringBuilder noDataIds = new StringBuilder();
            for (Long id : mission.getNoDataAlarmGroupsIds()) {
                if (noDataIds.length() > 0){
                    noDataIds.append(",");
                }
                noDataIds.append(id);
            }
            mission.setNoDataAlarmGroupIdStr(noDataIds.toString());
        }else {
            mission.setNoDataAlarmGroupIdStr("");
        }
    }

    /**
     * 验证是否包含测点编组下所有人员配置
     **/
    private boolean checkGroup(ProMissionVO mission) {
        // 查询监测任务中使用的人员配置组集合
        List<Long> groupIds = projectGroupService.getPtGroupGroupIds(mission.getId());
        if (groupIds.size()==0){
            return false;
        }
        return !new HashSet<>(mission.getGroupsIds()).containsAll(groupIds);
    }

    /**
     * 删除工程管理相关数据
     */
    private List<Long> deleteProRelate(List<Long> missionIds) {
        List<Long> ptIds = new ArrayList<>();
        // 删除任务显示项
        displayService.removeByMissionIds(missionIds);
        // 删除任务人员配置
        projectGroupService.deleteByMissionIds(missionIds);
        // 删除测点编组
        List<PtGroup> groups = ptGroupService.listByMissionIds(missionIds);
        if (groups.size() > 0){
            List<Long> ptGroupIds = groups.stream().map(PtGroup::getId).collect(Collectors.toList());
            ptGroupService.removeByIds(ptGroupIds);
            // 删除测点
            List<Point> points = pointService.listByPtGroupIds(ptGroupIds);
            ptIds = points.stream().map(Point::getId).collect(Collectors.toList());
            pointService.removeByPtGroupIds(ptGroupIds);
        }
        // 删除轴力数据
        typeZlService.removeByMissionIds(missionIds);
        if(ptIds.size() > 0){
            // 删除传感器信息
            sensorZlService.removeByPointIds(ptIds);
        }
        // 删除断面
        sectionService.deleteByMissions(missionIds);
        return ptIds;
    }

    /**
     * 删除推送任务相关
     * @param missionIds missionIds
     */
    private void deletePushTask(List<Long> missionIds) {
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PushTask::getMissionId, missionIds);
        List<PushTask> tasks = pushTaskService.list(wrapper);
        if (tasks.size() > 0){
            List<Long> taskIds = tasks.stream().map(PushTask::getId).collect(Collectors.toList());
            pushTaskService.removeByIds(taskIds);
            LambdaQueryWrapper<PushTaskRecord> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.in(PushTaskRecord::getMissionId, missionIds);
            pushTaskRecordService.remove(wrapper1);
            LambdaQueryWrapper<PushPoint> wrapper2 = new LambdaQueryWrapper<>();
            wrapper2.in(PushPoint::getPushTaskId, taskIds);
            pushPointService.remove(wrapper2);
        }
    }

    /**
     * 更新监测任务人员编组
     */
    private void saveMissionGroup(ProMissionVO mission, ProMission copy) {
        List<Long> groupsIds = mission.getGroupsIds();
        ArrayList<ProjectGroup> groups = new ArrayList<>();
        groupsIds.forEach(groupId -> {
            ProjectGroup projectGroup = new ProjectGroup()
                    .setProjectId(copy.getProjectId())
                    .setMissionId(copy.getId())
                    .setGroupId(groupId);
            groups.add(projectGroup);
        });
        projectGroupService.saveBatch(groups);
        List<CustomDisplayVO> displayList = mission.getDisplayList();
        if (displayList!=null && displayList.size()>0){
            displayList.forEach(item -> item.setMissionId(copy.getId()));
            if (mission.getId() == null){
                displayService.saveBatch(DzBeanUtils.listCopy(displayList, CustomDisplay.class));
            }else {
                displayService.updateBatchById(DzBeanUtils.listCopy(displayList, CustomDisplay.class));
            }
        }
    }
    //endregion

}
