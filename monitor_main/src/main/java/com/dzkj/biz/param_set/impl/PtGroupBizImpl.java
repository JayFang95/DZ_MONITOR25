package com.dzkj.biz.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.vo.PointDataXyzVO;
import com.dzkj.biz.data.vo.PointDataXyzhRealVO;
import com.dzkj.biz.data.vo.PointDataZRealVO;
import com.dzkj.biz.data.vo.PointDataZlVO;
import com.dzkj.biz.param_set.IPtGroupBiz;
import com.dzkj.biz.param_set.vo.*;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.ProjectGroup;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.data.*;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.param_set.ISectionService;
import com.dzkj.service.param_set.ISensorZlService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 点组业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class PtGroupBizImpl implements IPtGroupBiz {

    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private ISectionService sectionService;
    @Autowired
    private ISensorZlService sensorZlService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IAlarmItemService alarmItemService;
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

    @Override
    public IPage<PtGroupVO> getList(Integer pi, Integer ps, GroupCondition condition) {
        if (ps == CommonConstant.SEARCH_ALL_NO){
            Page<PtGroupVO> page = new Page<>(pi, ps);
            List<PtGroupVO> list = DzBeanUtils.listCopy(ptGroupService.getList(condition), PtGroupVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
            return page;
        }else {
            return DzBeanUtils.pageCopy(ptGroupService.getPage(pi, ps, condition), PtGroupVO.class);
        }
    }

    @Override
    public ResponseUtil add(PtGroupVO ptGroup) {
        if(ptGroupService.findByName(ptGroup)){
            return ResponseUtil.failure(500, "点组名称重复");
        }
        // 验证挂接报警组是否存在符合的报警项
        // 2024/11/18 修改去除了报警组必填
//        if(!checkAlarmGroup(ptGroup)){
//            return ResponseUtil.failure(500, "报警组报警项为空 或 不包含当前任务监测类型报警项");
//        }
        // 数据处理
        List<Long> receiveIds = ptGroup.getReceiveIds();
        List<Long> distributeIds = ptGroup.getDistributeIds();
        if (receiveIds!=null && receiveIds.size()>0){
            ptGroup.setAlarmReceiveIds(StringUtils.join(receiveIds, ","));
        }
        if (distributeIds!=null && distributeIds.size()>0){
            ptGroup.setAlarmDistributeIds(StringUtils.join(distributeIds, ","));
        }
        PtGroup copy = DzBeanUtils.propertiesCopy(ptGroup, PtGroup.class);
        boolean b = ptGroupService.save(copy);
        // 更新人员编组信息
        if(b){
            updateProjectGroup(ptGroup, copy);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(PtGroupVO ptGroup) {
        if(ptGroupService.findByName(ptGroup)){
            return ResponseUtil.failure(500, "点组名称重复");
        }
        // 验证挂接报警组是否存在符合的报警项
        // 2024/11/18 修改去除了报警组必填
//        if(!checkAlarmGroup(ptGroup)){
//            return ResponseUtil.failure(500, "报警组报警项为空 或 不包含当前任务监测类型报警项");
//        }
        // 数据处理
        List<Long> receiveIds = ptGroup.getReceiveIds();
        List<Long> distributeIds = ptGroup.getDistributeIds();
        if (receiveIds!=null && receiveIds.size()>0){
            ptGroup.setAlarmReceiveIds(StringUtils.join(receiveIds, ","));
        }
        if (distributeIds!=null && distributeIds.size()>0){
            ptGroup.setAlarmDistributeIds(StringUtils.join(distributeIds, ","));
        }
        PtGroup copy = DzBeanUtils.propertiesCopy(ptGroup, PtGroup.class);
        boolean b = ptGroupService.updateById(copy);
        if(b){
            updateProjectGroup(ptGroup, copy);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = ptGroupService.removeById(id);
        if (b){
            ThreadPoolUtil.getPool().execute(() -> {
                // 删除编组配置人员
                projectGroupService.removeByGroupId(id);
                // 删除编组下测点
                List<Point> points = pointService.listByPtGroupIds(Collections.singletonList(id));
                if (points.size() > 0){
                    List<Long> ptIds = points.stream().map(Point::getId).collect(Collectors.toList());
                    pointService.removeByIds(ptIds);
                    // 删除传感器
                    sensorZlService.removeByPointIds(ptIds);
                    // 删除监测数据
                    dataZService.removeByPtIds(ptIds);
                    dataZRealService.removeByPtIds(ptIds);
                    dataXyzhService.removeByPtIds(ptIds);
                    dataXyzhRealService.removeByPtIds(ptIds);
                    dataXyzService.removeByPtIds(ptIds);
                    dataZlService.removeByPtIds(ptIds);
                }
                // 删除编组下断面
                sectionService.removeByGroupId(id);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<Long> getPtGroupGroupList(Long ptGroupId) {
        List<ProjectGroup> groups = projectGroupService.listByPtGroupId(ptGroupId);
        return groups.stream().map(ProjectGroup::getGroupId).distinct().collect(Collectors.toList());
    }

    @Override
    public List<PtGroupVO> list(List<Long> missionIds) {
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PtGroup::getMissionId, missionIds).orderByDesc(PtGroup::getCreateTime);
        return DzBeanUtils.listCopy(ptGroupService.list(wrapper), PtGroupVO.class);
    }

    @Override
    public List<GroupPointVO> groupPtList(Long missionId) {
        ArrayList<GroupPointVO> list = new ArrayList<>();
        List<PtGroupVO> groupList = list(Collections.singletonList(missionId));
        List<PointVO> pointList = DzBeanUtils.listCopy(pointService.queryByMissionId(missionId)
                .stream().filter(pt -> !pt.getStop()).collect(Collectors.toList()), PointVO.class);
        for (PtGroupVO groupVO : groupList) {
            List<PointVO> filterList = pointList.stream().filter(pt -> pt.getPtGroupId().equals(groupVO.getId()))
                    .collect(Collectors.toList());
            if (filterList.size() > 0){
                GroupPointVO groupPointVO = DzBeanUtils.propertiesCopy(groupVO, GroupPointVO.class);
                groupPointVO.setPointList(filterList);
                list.add(groupPointVO);
            }
        }
        return list;
    }

    public List<GroupPointVO> groupPtAppList(Long missionId){
        List<GroupPointVO> list = new ArrayList<>();
        List<PtGroupVO> groupList = list(Collections.singletonList(missionId));
        ProMission mission = missionService.findById(missionId);
        List<PointAppVO> pointList = DzBeanUtils.listCopy(pointService.queryByMissionId(missionId), PointAppVO.class);
        List<Long> pidList = pointList.stream().map(PointAppVO::getId).collect(Collectors.toList());
        switch (mission.getTypeName()){
            case MissionTypeConst.AUTO_XYZ_OFFSET:
            case MissionTypeConst.HAND_XYZ_OFFSET:
                // xyzh_real表格原始数据
                List<PointDataXyzhRealVO> xyzhReals =  dataXyzhRealService.getDataByPidList(pidList);
                setPointValueXyzh(pointList, xyzhReals);
                break;
            case MissionTypeConst.ZC_FORCE:
                //zl表原始数据
                List<PointDataZlVO> dataZls = dataZlService.getDataByPidList(pidList);
                setPointValueZl(pointList, dataZls);
                break;
            case MissionTypeConst.SP_XY_OFFSET:
                //xyz表原始数据
                List<PointDataXyzVO> xyzList = dataXyzService.getDataByPidList(pidList);
                setPointValueXy(pointList, xyzList);
                break;
            default:
                //z_real表原始数据
                List<PointDataZRealVO> zReals = dataZRealService.getDataByPidList(pidList);
                setPointValueZ(pointList, zReals);
                break;
        }
        for (PtGroupVO groupVO : groupList) {
            List<PointAppVO> filterList = pointList.stream().filter(pt -> pt.getPtGroupId().equals(groupVO.getId()))
                    .collect(Collectors.toList());
            if (filterList.size() > 0){
                GroupPointVO groupPointVO = DzBeanUtils.propertiesCopy(groupVO, GroupPointVO.class);
                groupPointVO.setPointAppList(filterList);
                list.add(groupPointVO);
            }
        }
        return list;
    }

    //region 私有方法

    /**
     * 设置最新观测值
     */
    private void setPointValueZ(List<PointAppVO> pointList, List<PointDataZRealVO> zReals) {
        for (PointAppVO data : pointList) {
            Optional<PointDataZRealVO> optional = zReals.stream().filter(item -> item.getPid().equals(data.getId())).findAny();
            if (optional.isPresent()) {
                data.setFirstData(false);
                data.setZ(optional.get().getZ());
            }else {
                data.setFirstData(true);
            }
        }
    }

    private void setPointValueXy(List<PointAppVO> pointList, List<PointDataXyzVO> xyzList) {
        for (PointAppVO data : pointList) {
            Optional<PointDataXyzVO> optional = xyzList.stream().filter(item -> item.getPid().equals(data.getId())).findAny();
            if (optional.isPresent()) {
                data.setFirstData(false);
                data.setX(optional.get().getX());
                data.setY(optional.get().getY());
            }else {
                data.setFirstData(true);
            }
        }
    }

    private void setPointValueZl(List<PointAppVO> pointList, List<PointDataZlVO> dataZls) {
        for (PointAppVO data : pointList) {
            Optional<PointDataZlVO> optional = dataZls.stream().filter(item -> item.getPid().equals(data.getId())).findAny();
            if (optional.isPresent()) {
                data.setFirstData(false);
                data.setZ(optional.get().getF());
            }else {
                data.setFirstData(true);
            }
        }
    }

    private void setPointValueXyzh(List<PointAppVO> pointList, List<PointDataXyzhRealVO> xyzhReals) {
        for (PointAppVO data : pointList) {
            Optional<PointDataXyzhRealVO> optional = xyzhReals.stream().filter(item -> item.getPid().equals(data.getId())).findAny();
            if (optional.isPresent()) {
                data.setFirstData(false);
                data.setX(optional.get().getX());
                data.setY(optional.get().getY());
                data.setZ(optional.get().getZ());
            }else {
                data.setFirstData(true);
            }
        }
    }

    /**
     * 更新人员编组信息
     */
    private void updateProjectGroup(PtGroupVO ptGroup, PtGroup copy) {
        projectGroupService.deleteByPtGroupId(copy.getId());
        List<Long> groupsIds = ptGroup.getGroupIds();
        ArrayList<ProjectGroup> groups = new ArrayList<>();
        groupsIds.forEach(groupId -> {
            ProjectGroup projectGroup = new ProjectGroup()
                    .setMissionId(copy.getMissionId())
                    .setPtGroupId(copy.getId())
                    .setGroupId(groupId);
            groups.add(projectGroup);
        });
        projectGroupService.saveBatch(groups);
    }

    /**
     * 验证报警组类型和报警项是否符合
     * 1.报警组中报警项不为空
     * 2.报警项中必须包含一条与任务类型一致的数据
     */
    private boolean checkAlarmGroup(PtGroupVO ptGroup) {
        if (ptGroup.getAlarmGroupId() == null && ptGroup.getMissionId()==null){
            return false;
        }
        ProMission mission = missionService.findById(ptGroup.getMissionId());
        LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmItem::getAlarmGroupId, ptGroup.getAlarmGroupId());
        List<AlarmItem> items = alarmItemService.list(wrapper);
        if (mission == null || items.size() == 0){
            return false;
        }
        long count = items.stream().filter(item ->
                item.getMonitorType()!=null && item.getMonitorType().equals(mission.getTypeName())).count();
        return count > 0;
    }
    //endregion

}
