package com.dzkj.biz.alarm_setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.alarm_setting.IAlarmInfoBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmDetailCond;
import com.dzkj.biz.alarm_setting.vo.AlarmDetailVO;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoCondition;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.File;
import com.dzkj.entity.alarm_setting.AlarmDistribute;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmInfoCorrect;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.service.IFileService;
import com.dzkj.service.alarm_setting.IAlarmDistributeService;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPointDataZService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.project.IProMissionService;
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
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class AlarmInfoBizImpl implements IAlarmInfoBiz {

    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IAlarmDistributeService distributeService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private ICommonBiz commonBiz;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPointDataZService pointDataZService;
    @Autowired
    private IPointDataXyzhService pointDataXyzhService;

    @Autowired
    private IAlarmInfoCorrectService alarmInfoCorrectService;

    @Override
    public IPage<AlarmInfoVO> page(Integer pageIndex, Integer pageSize, AlarmInfoCondition cond) {
        if (cond.getMissionIds() == null || cond.getMissionIds().size() == 0){
            return new Page<>(pageIndex, pageSize, 0);
        }
        // region 2024/11/21 独立显示页面查询
        if (cond.getDbSource() != null && 2 == cond.getDbSource()) {
            return getCorrectPage(pageIndex, pageSize, cond);
        }
        // endregion 2024/11/21 独立显示页面查询
        LambdaQueryWrapper<AlarmInfo> wrapper = new LambdaQueryWrapper<>();
        Integer timeNum = cond.getAlarmTimeNum();
        if(timeNum!=null && timeNum>0){
            cond.setAlarmTime(DateUtil.getDateOfDay(new Date(), -timeNum));
        }
        // 查询用户可查询报警测点
        Map<String, List<Long>> map = getUserPid(cond);
        wrapper.and(item ->item
                .and(temp1 -> temp1.in(AlarmInfo::getPtId, map.get("level1")).eq(AlarmInfo::getAlarmLevel, "1"))
                .or(temp2->temp2.in(AlarmInfo::getPtId, map.get("level2")).eq(AlarmInfo::getAlarmLevel, "2"))
                .or(temp3-> temp3.in(AlarmInfo::getPtId, map.get("level3")).eq(AlarmInfo::getAlarmLevel, "3"))
        ).in(AlarmInfo::getMissionId, cond.getMissionIds())
                .eq(cond.getHandle()!=null,AlarmInfo::getHandle, cond.getHandle())
                .ge(cond.getAlarmTimeNum()!=null,AlarmInfo::getAlarmTime, cond.getAlarmTime())
                .eq(cond.getRecycleNum()!=null,AlarmInfo::getRecycleNum, cond.getRecycleNum())
                .like(StringUtils.isNotEmpty(cond.getAlarmOrigin()),AlarmInfo::getAlarmOrigin, cond.getAlarmOrigin())
                .like(StringUtils.isNotEmpty(cond.getInfo()),AlarmInfo::getInfo, cond.getInfo())
                .orderByDesc(AlarmInfo::getAlarmTime);
        if (pageSize == CommonConstant.SEARCH_ALL_NO){
            List<AlarmInfoVO> list = DzBeanUtils.listCopy(alarmInfoService.list(wrapper), AlarmInfoVO.class);
            Page<AlarmInfoVO> page = new Page<>(pageIndex, pageSize);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), 500));
            return page;
        }
        return DzBeanUtils.pageCopy(alarmInfoService.page(new Page<>(pageIndex, pageSize), wrapper), AlarmInfoVO.class);
    }

    /**
     * 查询同步表报警信息
     */
    private IPage<AlarmInfoVO> getCorrectPage(Integer pageIndex, Integer pageSize, AlarmInfoCondition cond) {
        LambdaQueryWrapper<AlarmInfoCorrect> wrapper = new LambdaQueryWrapper<>();
        Integer timeNum = cond.getAlarmTimeNum();
        if(timeNum!=null && timeNum>0){
            cond.setAlarmTime(DateUtil.getDateOfDay(new Date(), -timeNum));
        }
        // 查询用户可查询报警测点
        Map<String, List<Long>> map = getUserPid(cond);
        wrapper.and(item ->item
                        .and(temp1 -> temp1.in(AlarmInfoCorrect::getPtId, map.get("level1")).eq(AlarmInfoCorrect::getAlarmLevel, "1"))
                        .or(temp2->temp2.in(AlarmInfoCorrect::getPtId, map.get("level2")).eq(AlarmInfoCorrect::getAlarmLevel, "2"))
                        .or(temp3-> temp3.in(AlarmInfoCorrect::getPtId, map.get("level3")).eq(AlarmInfoCorrect::getAlarmLevel, "3"))
                ).in(AlarmInfoCorrect::getMissionId, cond.getMissionIds())
                .eq(cond.getHandle()!=null,AlarmInfoCorrect::getHandle, cond.getHandle())
                .ge(cond.getAlarmTimeNum()!=null,AlarmInfoCorrect::getAlarmTime, cond.getAlarmTime())
                .eq(cond.getRecycleNum()!=null,AlarmInfoCorrect::getRecycleNum, cond.getRecycleNum())
                .like(StringUtils.isNotEmpty(cond.getAlarmOrigin()),AlarmInfoCorrect::getAlarmOrigin, cond.getAlarmOrigin())
                .like(StringUtils.isNotEmpty(cond.getInfo()),AlarmInfoCorrect::getInfo, cond.getInfo())
                .orderByDesc(AlarmInfoCorrect::getAlarmTime);
        if (pageSize == CommonConstant.SEARCH_ALL_NO){
            List<AlarmInfoVO> list = DzBeanUtils.listCopy(alarmInfoCorrectService.list(wrapper), AlarmInfoVO.class);
            Page<AlarmInfoVO> page = new Page<>(pageIndex, pageSize);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), 500));
            return page;
        }
        return DzBeanUtils.pageCopy(alarmInfoCorrectService.page(new Page<>(pageIndex, pageSize), wrapper), AlarmInfoVO.class);
    }

    @Override
    public boolean handel(AlarmInfoVO data) {
        // 删除多余图片信息
        String picInfo = data.getHandlePic();
        ArrayList<Long> ids = new ArrayList<>();
        AlarmInfo info = alarmInfoService.getById(data.getId());
        if (info!=null && StringUtils.isNotEmpty(info.getHandlePic())){
            String[] split = info.getHandlePic().split(",");
            for (String picId : split) {
                boolean b = StringUtils.isEmpty(picInfo) || !picInfo.contains(picId);
                if(b && StringUtils.isNotEmpty(picId)){
                    ids.add(Long.valueOf(picId));
                }
            }
            if (ids.size()>0){
                commonBiz.deleteFileByIds(ids);
            }
        }
        return alarmInfoService.updateById(DzBeanUtils.propertiesCopy(data, AlarmInfo.class));
    }

    @Override
    public boolean delete(Long id) {
        // 删除关联的处理图片信息
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getCategoryId, id).eq(File::getCategoryName, "alarm_detail");
        List<File> files = fileService.list(wrapper);
        if (files.size() > 0 ){
            List<Long> collect = files.stream().map(File::getId).collect(Collectors.toList());
            commonBiz.deleteFileByIds(collect);
        }
        return alarmInfoService.removeById(id);
    }

    @Override
    public boolean delete(List<Long> ids) {
        // 删除关联的处理图片信息
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(File::getCategoryId, ids).eq(File::getCategoryName, "alarm_detail");
        List<File> files = fileService.list(wrapper);
        if (files.size() > 0 ){
            List<Long> collect = files.stream().map(File::getId).collect(Collectors.toList());
            commonBiz.deleteFileByIds(collect);
        }
        return alarmInfoService.removeByIds(ids);
    }

    @Override
    public AlarmDetailVO detail(AlarmDetailCond cond) {
        // 查询监测任务类型
        ProMission mission = missionService.findById(cond.getMissionId());
        AlarmDetailVO result;
        if (MissionTypeConst.HAND_XYZ_OFFSET.equals(mission.getTypeName()) || MissionTypeConst.AUTO_XYZ_OFFSET.equals(mission.getTypeName())){
            LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataXyzh::getPid, cond.getPid()).eq(PointDataXyzh::getRecycleNum, cond.getRecycleNum());
            result = DzBeanUtils.propertiesCopy(pointDataXyzhService.getOne(wrapper), AlarmDetailVO.class);
        }else {
            LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataZ::getPid, cond.getPid()).eq(PointDataZ::getRecycleNum, cond.getRecycleNum());
            result = DzBeanUtils.propertiesCopy(pointDataZService.getOne(wrapper), AlarmDetailVO.class);
        }
        if (result != null){
            result.setXyzh(MissionTypeConst.HAND_XYZ_OFFSET.equals(mission.getTypeName()) || MissionTypeConst.AUTO_XYZ_OFFSET.equals(mission.getTypeName()));
            result.setGetTimeStr(DateUtil.dateToDateString(result.getGetTime(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN));
        }
        return result;
    }

    //region 私有方法
    /**
     * 查询用户可访问
     */
    private Map<String, List<Long>> getUserPid(AlarmInfoCondition cond) {
        Map<String, List<Long>> map = new HashMap<>(16);
        List<Long> levelOne = new ArrayList<>(Collections.singletonList(0L));
        List<Long> levelTwo = new ArrayList<>(Collections.singletonList(0L));
        List<Long> levelThree = new ArrayList<>(Collections.singletonList(0L));
        // 查询任务下所有测点编组
        List<PtGroup> ptGroups = ptGroupService.listByMissionIds(cond.getMissionIds());
        // 查询所有包含当前用户的工作编组
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroup::getUserId, cond.getUserId());
        List<Long> userGroupIds = userGroupService.list(wrapper).stream().map(UserGroup::getGroupId).collect(Collectors.toList());
        // 过滤测点编组
        List<PtGroup> filter = ptGroups.stream().filter(item -> {
            if (StringUtils.isAnyEmpty(item.getAlarmReceiveIds(), item.getAlarmDistributeIds())){
                return false;
            }
            String[] split = item.getAlarmReceiveIds().split(",");
            boolean res = false;
            for (String receiveId : split) {
                if (userGroupIds.contains(Long.valueOf(receiveId))) {
                    res = true;
                }
            }
            return res ;
        }).collect(Collectors.toList());
        // 获取编组下测点和对应的报警等级关系
        if (filter.size() > 0){
            setPidInfo(userGroupIds, filter, levelOne, levelTwo, levelThree);
        }
        map.put("level1",levelOne);
        map.put("level2", levelTwo);
        map.put("level3", levelThree);
        return map;
    }

    /**
     * 设置测点和接收报警关系
     */
    private void setPidInfo(List<Long> userGroupIds, List<PtGroup> ptGroups, List<Long> levelOne, List<Long> levelTwo, List<Long> levelThree) {
        List<Long> ptGroupIds = ptGroups.stream().map(PtGroup::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Point::getId, Point::getPtGroupId).in(Point::getPtGroupId, ptGroupIds);
        List<Point> points = pointService.list(wrapper);
        List<String> distributeStr = ptGroups.stream().map(PtGroup::getAlarmDistributeIds).collect(Collectors.toList());
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
        for (PtGroup ptGroup : ptGroups) {
            List<Long> pointIds = points.stream().filter(item -> item.getPtGroupId().equals(ptGroup.getId())).map(Point::getId).collect(Collectors.toList());
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
    private void getDistributeLevel(List<Long> userGroupIds, List<AlarmDistribute> distributes, PtGroup ptGroup, List<Integer> level) {
        String[] splitReceive = ptGroup.getAlarmReceiveIds().split(",");
        String[] splitDistribute = ptGroup.getAlarmDistributeIds().split(",");
        for (int i = 0; i < splitReceive.length; i++) {
            if (userGroupIds.contains(Long.valueOf(splitReceive[i]))){
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
    //endregion
}
