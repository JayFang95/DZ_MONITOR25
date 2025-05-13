package com.dzkj.biz.dashoborad;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
import com.dzkj.biz.dashoborad.vo.*;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.biz.project.vo.ProjectVO;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.alarm_setting.AlarmDistribute;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.entity.equipment.ControlBox;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.system.ProjectType;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.service.alarm_setting.IAlarmDistributeService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPointDataZService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectGroupService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IProjectTypeService;
import com.dzkj.service.system.IUserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/14
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class DashboardBizImpl implements IDashboardBiz {

    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IProjectTypeService projectTypeService;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private IAlarmDistributeService distributeService;

    @Override
    public StatisticInfoData getDashboardInfo(InfoData infoData, Long userId) {
        String ptNumInfo = "设计点数：0000  在测点数：0000  停测点数：0000  报警点数：0000";
        List<Integer> ptNumList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        StatisticInfoData data = new StatisticInfoData(ptNumInfo, ptNumList, alarmInfoList);
        if (infoData == null || infoData.getPointList().size() == 0){
            return data;
        }
        List<ProMissionVO> missionList = infoData.getMissionList();
        List<PtGroupVO> groupList = infoData.getGroupList();
        List<PointVO> points = infoData.getPointList();
        // 设置设计点数
        int designNum = missionList.stream().filter(item-> item.getDesignPtNum()!=null)
                .mapToInt(ProMissionVO::getDesignPtNum).sum();
        // 分层任务
        List<Long> fcList = missionList.stream().filter(item -> MissionTypeConst.SP_DEEP_OFFSET.equals(item.getTypeName()))
                .map(ProMissionVO::getId).collect(Collectors.toList());
        // 数据过滤分析
        // region 2024/11/21 只统计监测点
        List<PointVO> stopPoints = points.stream().filter(item -> item.getStop() && "监测点".equals(item.getType()) ).collect(Collectors.toList());
        List<PointVO> workPoints = points.stream().filter(item -> !item.getStop() && "监测点".equals(item.getType())).collect(Collectors.toList());
        // endregion 2024/11/21 只统计监测点
        List<PointVO> fcStopPoints = new ArrayList<>();
        List<PointVO> commonStopPoints ;
        List<PointVO> fcWorkPoints = new ArrayList<>();
        List<PointVO> commonWorkPoints ;
        if (fcList.size() > 0){
            List<Long> groupIds = groupList.stream().filter(item -> fcList.contains(item.getMissionId())).map(PtGroupVO::getId).collect(Collectors.toList());
            if (groupIds.size() > 0){
                fcStopPoints = stopPoints.stream().filter(item -> groupIds.contains(item.getPtGroupId())).collect(Collectors.toList());
                commonStopPoints = stopPoints.stream().filter(item -> !groupIds.contains(item.getPtGroupId())).collect(Collectors.toList());
                fcWorkPoints = workPoints.stream().filter(item -> groupIds.contains(item.getPtGroupId())).collect(Collectors.toList());
                commonWorkPoints = workPoints.stream().filter(item -> !groupIds.contains(item.getPtGroupId())).collect(Collectors.toList());
            }else {
                commonStopPoints = stopPoints;
                commonWorkPoints = workPoints;
            }
        }else {
            commonStopPoints = stopPoints;
            commonWorkPoints = workPoints;
        }
        // 停测点数
        int stopNum = (int) (commonStopPoints.size() + fcStopPoints.stream().map(PointVO::getPtGroupId).distinct().count());
        // 在测点数
        int workNum = getWorkNum(fcWorkPoints, commonWorkPoints);
        // 报警点数
        List<PointVO> fcPtList = new ArrayList<>();
        fcPtList.addAll(fcStopPoints);
        fcPtList.addAll(fcWorkPoints);
        List<PointVO> commonPtList = new ArrayList<>();
        commonPtList.addAll(commonStopPoints);
        commonPtList.addAll(commonWorkPoints);
        // 获取监测报警信息
        int alarmNum = getAlarmNum(fcPtList, commonPtList, alarmInfoList, infoData, userId);
        // 设计 在测  停测 报警
        ptNumList.add(0, designNum);
        ptNumList.add(1, workNum);
        ptNumList.add(2, stopNum);
        ptNumList.add(3, alarmNum);
        ptNumInfo = "设计点数："+ String.format("%04d", designNum) +
                "  在测点数："+ String.format("%04d", workNum) +
                "  停测点数："+ String.format("%04d", stopNum) +
                "  报警点数："+ String.format("%04d", alarmNum);
        data.setPtNumInfo(ptNumInfo);
        data.setPtNumList(ptNumList);
        data.setAlarmInfoList(alarmInfoList);
        return data;
    }

    /**
     * 获取报警信息和报警点数
     *
     * @description 获取报警信息和报警点数
     * @author jing.fang
     * @date 2022/7/18 14:24
     * @param fcPtList fcPtList
     * @param commonPtList commonPtList
     * @param alarmInfoList alarmInfoList
     * @param infoData infoData
     * @param userId userId
     * @return int
    **/
    private int getAlarmNum(List<PointVO> fcPtList, List<PointVO> commonPtList, List<AlarmInfoVO> alarmInfoList, InfoData infoData, Long userId) {
        int alarmNum = 0;
        // 查询报警信息
        getAlarmInfo(infoData, alarmInfoList, userId);
        if (fcPtList.size() > 0){
            List<Long> fcIds = fcPtList.stream().map(PointVO::getId).collect(Collectors.toList());
            List<Long> alarmIds = alarmInfoList.stream().map(AlarmInfoVO::getPtId).filter(fcIds::contains).distinct().collect(Collectors.toList());
            int count = (int) fcPtList.stream().filter(item -> alarmIds.contains(item.getId()))
                    .map(PointVO::getPtGroupId).distinct().count();
            alarmNum += count;
        }
        if (commonPtList.size() > 0){
            List<Long> commonIds = commonPtList.stream().map(PointVO::getId).collect(Collectors.toList());
            int count = (int) alarmInfoList.stream().map(AlarmInfoVO::getPtId)
                    .filter(commonIds::contains).distinct().count();
            alarmNum += count;
        }
        return alarmNum;
    }

    /**
     * 查询报警信息
     */
    private void getAlarmInfo(InfoData infoData, List<AlarmInfoVO> alarmInfoList, Long userId) {
        Map<String, List<Long>> map = new HashMap<>(16);
        List<Long> levelOne = new ArrayList<>();
        List<Long> levelTwo = new ArrayList<>();
        List<Long> levelThree = new ArrayList<>();
        levelOne.add(0L);
        levelTwo.add(0L);
        levelThree.add(0L);
        // 查询所有包含当前用户的工作编组
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroup::getUserId, userId);
        List<Long> userGroupIds = userGroupService.list(wrapper).stream().map(UserGroup::getGroupId).collect(Collectors.toList());
        // 过滤测点编组
        List<PtGroupVO> filter = infoData.getGroupList().stream().filter(item -> {
            if(StringUtils.isEmpty(item.getAlarmReceiveIds()) || StringUtils.isEmpty(item.getAlarmDistributeIds())){
                return false;
            }
            boolean res = false;
            for (String receiveId : item.getAlarmReceiveIds().split(",")) {
                if (userGroupIds.contains(Long.valueOf(receiveId))) {
                    res = true;
                }
            }
            return res ;
        }).collect(Collectors.toList());
        // 获取编组下测点和对应的报警等级关系
        if (filter.size() > 0){
            setPidInfo(userGroupIds, filter, infoData.getPointList(), levelOne, levelTwo, levelThree);
        }
        map.put("level1",levelOne);
        map.put("level2", levelTwo);
        map.put("level3", levelThree);
        LambdaQueryWrapper<AlarmInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(item ->item
                .and(temp1 -> temp1.in(AlarmInfo::getPtId, map.get("level1")).eq(AlarmInfo::getAlarmLevel, "1"))
                .or(temp2->temp2.in(AlarmInfo::getPtId, map.get("level2")).eq(AlarmInfo::getAlarmLevel, "2"))
                .or(temp3-> temp3.in(AlarmInfo::getPtId, map.get("level3")).eq(AlarmInfo::getAlarmLevel, "3"))
        );
        List<Long> missionIds = infoData.getMissionList().stream().map(ProMissionVO::getId).collect(Collectors.toList());
        queryWrapper.in(AlarmInfo::getMissionId, missionIds).eq(AlarmInfo::getHandle, false)
                .orderByDesc(AlarmInfo::getAlarmTime);
        List<AlarmInfoVO> list = DzBeanUtils.listCopy(alarmInfoService.list(queryWrapper), AlarmInfoVO.class);
        if (list.size() > 0){
            alarmInfoList.addAll(list);
        }
    }

    /**
     * 设置测点和接收报警关系
     */
    private void setPidInfo(List<Long> userGroupIds, List<PtGroupVO> ptGroups, List<PointVO> points, List<Long> levelOne, List<Long> levelTwo, List<Long> levelThree) {
        List<String> distributeStr = ptGroups.stream().map(PtGroupVO::getAlarmDistributeIds).collect(Collectors.toList());
        List<Long> distributeIds = new ArrayList<>();
        distributeStr.forEach(item -> {
            for (String distributeId : item.split(",")) {
                distributeIds.add(Long.valueOf(distributeId));
            }
        });
        List<AlarmDistribute> distributes = distributeService.listByIds(distributeIds);
        if (points.size() == 0 || distributes.size() == 0){
            return;
        }
        for (PtGroupVO ptGroup : ptGroups) {
            List<Long> pointIds = points.stream().filter(item -> item.getPtGroupId().equals(ptGroup.getId())).map(PointVO::getId).collect(Collectors.toList());
            List<Integer> level = new ArrayList<>();
            // 获取报警接收人和分发规则匹配关系
            getDistributeLevel(userGroupIds, distributes, ptGroup, level);
            if (level.contains(1)){
                levelOne.addAll(pointIds);
            }
            if (level.contains(2)){
                levelTwo.addAll(pointIds);
            }
            if (level.contains(3)){
                levelThree.addAll(pointIds);
            }
        }
    }

    /**
     * 配置报警接收人和分发规则匹配关系
     */
    private void getDistributeLevel(List<Long> groupIds, List<AlarmDistribute> distributes, PtGroupVO ptGroup, List<Integer> level) {
        String[] splitReceive = ptGroup.getAlarmReceiveIds().split(",");
        String[] splitDistribute = ptGroup.getAlarmDistributeIds().split(",");
        for (int i = 0; i < splitReceive.length; i++) {
            if (groupIds.contains(Long.valueOf(splitReceive[i]))){
                String distributeId = i < splitDistribute.length ? splitDistribute[i] : splitDistribute[splitDistribute.length - 1];
                Optional<AlarmDistribute> optional = distributes.stream().filter(item -> (item.getId() + "").equals(distributeId)).findAny();
                if (optional.isPresent() && StringUtils.isNotEmpty(optional.get().getWebAlarmLevel())){
                    String[] webAlarmLevel = optional.get().getWebAlarmLevel().split(",");
                    for (String alarmLevel : webAlarmLevel) {
                        switch (alarmLevel){
                            case "超预警值":
                                level.add(1);
                                break;
                            case "超报警值":
                                level.add(2);
                                break;
                            case "超控制值":
                                level.add(3);
                                break;
                            default:
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取在测点数
     * 水平分层一个编组算一个测点
     */
    private int getWorkNum(List<PointVO> fcWorkPoints, List<PointVO> commonWorkPoints) {
        int workNum = 0;
        if (fcWorkPoints.size() > 0){
            List<Long> fcPtIds = fcWorkPoints.stream().map(PointVO::getId).collect(Collectors.toList());
            LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(PointDataZ::getPid, fcPtIds);
            List<Long> fcDataPtIdList = dataZService.list(wrapper).stream().map(PointDataZ::getPid).distinct().collect(Collectors.toList());
            int fcCount = (int) fcWorkPoints.stream().filter(item -> fcDataPtIdList.contains(item.getId())).map(PointVO::getPtGroupId).distinct().count();
            workNum += fcCount;
        }
        if (commonWorkPoints.size() > 0){
            List<Long> commonPtIds = commonWorkPoints.stream().map(PointVO::getId).collect(Collectors.toList());
            LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(PointDataZ::getPid, commonPtIds);
            int dataZCount = (int) dataZService.list(wrapper).stream().map(PointDataZ::getPid).distinct().count();
            LambdaQueryWrapper<PointDataXyzh> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.in(PointDataXyzh::getPid, commonPtIds);
            int dataXyzCount = (int) dataXyzhService.list(wrapper1).stream().map(PointDataXyzh::getPid).distinct().count();
            workNum += dataZCount + dataXyzCount;
        }
        return workNum;
    }

    @Override
    public ScreenData getDashboardScreenInfo(Long companyId) {
        ScreenData data = new ScreenData();
        // 查询 包含当前请求的人员编组信息
        if (companyId <= 0){
            companyId = companyService.getCurrentCompany();
        }
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getCompanyId, companyId).orderByDesc(Project::getCreateTime);
        List<Project> list = projectService.list(wrapper);

        List<Long> projIds = list.stream().map(Project::getId).collect(Collectors.toList());
        List<ProMission> missions = list.size() > 0 ? missionService.getListInProjIds(projIds) : new ArrayList<>();
        List<Long> missionIds = missions.stream().map(ProMission::getId).collect(Collectors.toList());
        LambdaQueryWrapper<PtGroup> ptGroupWrapper = new LambdaQueryWrapper<>();
        ptGroupWrapper.in(PtGroup::getMissionId, missionIds);
        List<PtGroup> ptGroups = missions.size()>0 ? ptGroupService.list(ptGroupWrapper) : new ArrayList<>();

        // 项目进度统计
        List<Integer> processList = Arrays.asList(0, 0, 0, 0, 0);
        processList.set(0, (int) list.stream().filter(item -> item.getProgress() >= 80.0).count());
        processList.set(1, (int) list.stream().filter(item -> item.getProgress()>=60.0 && item.getProgress()<80.0).count());
        processList.set(2, (int) list.stream().filter(item -> item.getProgress()>=40.0 && item.getProgress()<60.0).count());
        processList.set(3, (int) list.stream().filter(item -> item.getProgress()>=20.0 && item.getProgress()<40.0).count());
        processList.set(4, (int) list.stream().filter(item -> item.getProgress()>=0.0 && item.getProgress()<20.0).count());
        data.setProjectProcess(processList);
        List<Project> collect = list.stream().filter(item -> !item.getFinished()).collect(Collectors.toList());
        data.setProjectList(DzBeanUtils.listCopy(collect, ProjectVO.class));
        // 项目报警测点数统计
        List<Integer> proAlarmPtnNum = Arrays.asList(0, 0, 0, 0);
        List<Integer> alarmPtNum = new ArrayList<>();

        // 查询各项目权限下报警测点树
        collect.forEach(item -> {
            setAlarmNum(item, missions, ptGroups, alarmPtNum);
        });
        proAlarmPtnNum.set(0, (int) alarmPtNum.stream().filter(item -> item == 0).count());
        proAlarmPtnNum.set(1, (int) alarmPtNum.stream().filter(item -> item > 0 && item <= 30).count());
        proAlarmPtnNum.set(2, (int) alarmPtNum.stream().filter(item -> item > 30 && item <= 60).count());
        proAlarmPtnNum.set(3, (int) alarmPtNum.stream().filter(item -> item > 60).count());
        data.setProAlarmPtnNum(proAlarmPtnNum);
        // 项目类别统计
        List<ProTypeData> typeData = new ArrayList<>();
        List<ProjectType> projectTypes = projectTypeService.list();
        projectTypes.forEach(item -> {
            long count = collect.stream().filter(temp -> temp.getTypeId().equals(item.getId())).count();
            if (count > 0){
                ProTypeData data1 = new ProTypeData();
                data1.setId(item.getId()).setName(item.getName()).setTypeNum((int) count);
                typeData.add(data1);
            }
        });
        data.setProTypeData(typeData);
        // 报警项目占比
        data.setAlarmProRate(Math.round((collect.size() - proAlarmPtnNum.get(0))*1.0 / collect.size() * 100 * 100) / 100.0);
        // 工程总数 / 设备总数 / 监测数据总数
        data.setProjectNum(collect.size());
        if(collect.size() > 0){
            LambdaQueryWrapper<ControlBox> equipWrapper = new LambdaQueryWrapper<>();
            List<Long> projectIds = collect.stream().map(Project::getId).collect(Collectors.toList());
            equipWrapper.in(ControlBox::getProjectId, projectIds);
            // 设备数保持原样，不随控制器改变：2023/03/10
            data.setEquipNum(controlBoxService.count(equipWrapper));
            List<StatisticTempData> tempDataList = pointService.getIdInProject(projectIds);
            List<Long> ptIds = tempDataList.stream().map(StatisticTempData::getPointId).collect(Collectors.toList());
            if (ptIds.size() > 0){
                LambdaQueryWrapper<PointDataZ> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.in(PointDataZ::getPid, ptIds);
                int count = dataZService.count(wrapper1);
                LambdaQueryWrapper<PointDataXyzh> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.in(PointDataXyzh::getPid, ptIds);
                int count2 = dataXyzhService.count(wrapper2);
                data.setMonitorDataNum(count + count2);
            }else {
                data.setMonitorDataNum(0);
            }
        }else {
            data.setEquipNum(0);
        }
        // 项目省份统计
        List<String> provinceList = collect.stream().map(Project::getProvince).distinct().collect(Collectors.toList());
        data.setProvince(provinceList);
        ArrayList<Integer> provinceNum = new ArrayList<>(provinceList.size());
        provinceList.forEach(item -> provinceNum.add(0));
        for (int i = 0; i < provinceList.size(); i++) {
            String province = provinceList.get(i);
            provinceNum.set(i, (int) collect.stream().filter(item -> item.getProvince().equals(province)).count());
        }
        data.setProvinceNum(provinceNum);
        return data;
    }

    /**
     * 设置项目报警测点数量
     */
    private void setAlarmNum(Project project, List<ProMission> missions, List<PtGroup> ptGroups, List<Integer> alarmPtNum) {
        List<ProMission> missionList = missions.stream().filter(item -> item.getProjectId().equals(project.getId())).collect(Collectors.toList());
        int num = 0;
        if (missionList.size() == 0){
            alarmPtNum.add(num);
            return;
        }
        for (ProMission mission : missionList) {
            List<Long> ptGroupIds = ptGroups.stream().filter(item -> item.getMissionId().equals(mission.getId())).map(PtGroup::getId).collect(Collectors.toList());
            if (ptGroupIds.size() == 0){
                continue;
            }
            LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Point::getPtGroupId, ptGroupIds).select(Point::getId,Point::getPtGroupId);
            List<Point> points = pointService.list(wrapper);
            if (points.size() == 0){
                continue;
            }
            List<Long> pointIds = points.stream().map(Point::getId).collect(Collectors.toList());
            LambdaQueryWrapper<AlarmInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(AlarmInfo::getPtId, pointIds).select(AlarmInfo::getPtId);
            List<AlarmInfo> infos = alarmInfoService.list(queryWrapper);
            if (infos.size() == 0){
                continue;
            }
            if (MissionTypeConst.SP_DEEP_OFFSET.equals(mission.getTypeName())){
                List<Long> ptIds = infos.stream().map(AlarmInfo::getPtId).collect(Collectors.toList());
                num += points.stream().filter(item -> ptIds.contains(item.getId())).map(Point::getPtGroupId).distinct().count();
            }else {
                num += infos.stream().map(AlarmInfo::getPtId).distinct().count();
            }
        }
        alarmPtNum.add(num);
    }

}
