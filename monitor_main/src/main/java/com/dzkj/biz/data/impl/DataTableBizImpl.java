package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.data.IDataTableBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.FileUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.data.JcInfo;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.data.IJcInfoService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPointDataZService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/18
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class DataTableBizImpl implements IDataTableBiz {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IAlarmItemService alarmItemService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IJcInfoService jcInfoService;
    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public TableDataAll getAllData(TableDataCondition condition) {
        TableDataAll dataAll = new TableDataAll();
        if (condition.getProjectId() == null ||condition.getGroupIds() == null || condition.getGroupIds().size()==0){
            dataAll.setDataList(new ArrayList<>());
            return dataAll;
        }
        Project project = projectService.getById(condition.getProjectId());
        if (project == null){
            dataAll.setDataList(new ArrayList<>());
            return dataAll;
        }
        formatDate(condition);
        List<ProMission> missions = missionService.getList(condition.getProjectId());
        LambdaQueryWrapper<Point> queryWrapper = new LambdaQueryWrapper<Point>()
                .in(Point::getPtGroupId, condition.getGroupIds()).select(Point::getId);
        List<Long> pidList = pointService.list(queryWrapper).stream().map(Point::getId).collect(Collectors.toList());
        // 查询报警信息
        setAlarmStr(pidList, condition, dataAll);
        //查询项目下监测数据
        List<ReportData> dataZList = pidList.size() == 0 ? new ArrayList<>() : dataZService.getDataByCond(condition, pidList);
        List<ReportData> dataXyzList = pidList.size() == 0 ? new ArrayList<>() : dataXyzhService.getDataByCond(condition, pidList);
        List<ReportData> reportDataList = new ArrayList<>();
        reportDataList.addAll(dataZList);
        reportDataList.addAll(dataXyzList);
        if (reportDataList.size() == 0){
            dataAll.setDataList(new ArrayList<>());
            return dataAll;
        }
        // 获取巡视数据
        setXsData(dataAll, condition, missions);
        // 设置其他数据
        List<Long> missionIdList = reportDataList.stream().map(ReportData::getMissionId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<PtGroup> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(PtGroup::getMissionId, missionIdList);
        List<PtGroup> ptGroups = ptGroupService.list(queryWrapper2);
        List<DataAll> dataList = new ArrayList<>();
        List<DepthData> depthData = new ArrayList<>();
        List<NoDepthData> noDepthData = new ArrayList<>();
        missionIdList.forEach(missionId -> {
            Optional<ProMission> optional = missions.stream().filter(item -> item.getId().equals(missionId)).findFirst();
            if (optional.isPresent() && !MissionTypeConst.MANUAL_PATROL.equals(optional.get().getTypeName())){
                ProMission mission = optional.get();
                String type = mission.getTypeName();
                List<ReportData> collect = reportDataList.stream().filter(item -> item.getMissionId().equals(missionId)).collect(Collectors.toList());
                if (MissionTypeConst.SP_DEEP_OFFSET.equals(optional.get().getTypeName())){
                    addDepthData(ptGroups, mission, collect, depthData);
                }else {
                    addNoDepthData(ptGroups, mission, collect, noDepthData);
                }
                DataAll all = new DataAll();
                all.setMissionId(mission.getId()).setMissionName(mission.getName())
                        .setMaxTotalNum(collect.get(0).getTotalNum()).setPtNametTotal(collect.get(0).getPtName())
                        .setMaxVdeltNum(collect.get(0).getVDeltNum()).setPtNametVdelt(collect.get(0).getPtName());
                Long totalGroupId = collect.get(0).getAlarmGroupId();
                Long vDeltGroupId = collect.get(0).getAlarmGroupId();
                for (ReportData data : collect) {
                    String ptName = MissionTypeConst.SP_DEEP_OFFSET.equals(type) ? data.getGroupName() + "(" + data.getPtName() + "m)" : data.getPtName();
                    if (Math.abs(data.getTotalNum()) > Math.abs(all.getMaxTotalNum()))
                    {
                        all.setPtNametTotal(ptName);
                        all.setMaxTotalNum(data.getTotalNum());
                        totalGroupId = data.getAlarmGroupId();
                    }
                    if (Math.abs(data.getVDeltNum()) > Math.abs(all.getMaxVdeltNum()))
                    {
                        all.setPtNametVdelt(ptName);
                        all.setMaxVdeltNum(data.getVDeltNum());
                        vDeltGroupId = data.getAlarmGroupId();
                    }
                }
                // 查询编组告警项
                LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AlarmItem::getAlarmGroupId, totalGroupId).eq(AlarmItem::getResultItemType, "累计变化量")
                        .eq(AlarmItem::getMonitorType, type).eq(AlarmItem::getAlarmType, "1");
                LambdaQueryWrapper<AlarmItem> wrapper2 = new LambdaQueryWrapper<>();
                wrapper2.eq(AlarmItem::getAlarmGroupId, vDeltGroupId).eq(AlarmItem::getResultItemType, "日变化速率")
                        .eq(AlarmItem::getMonitorType, type).eq(AlarmItem::getAlarmType, "1");
                if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(type) || MissionTypeConst.HAND_XYZ_OFFSET.equals(type)){
                    wrapper.eq(AlarmItem::getResultType, "平面位移(P)");
                    wrapper2.eq(AlarmItem::getResultType, "平面位移(P)");
                }
                List<AlarmItem> totalList = alarmItemService.list(wrapper);
                List<AlarmItem> vDeltList = alarmItemService.list(wrapper2);
                if (totalList.size() > 0){
                    getThresholdValue(totalList, all);
                }else {
                    all.setTotalThreshold("-");
                }
                if (vDeltList.size() > 0){
                    getThresholdValue(vDeltList, all);
                }else {
                    all.setVDeltThreshold("-");
                }
                if (StringUtils.isEmpty(all.getNote()))
                {
                    all.setNote("单位: " + mission.getDeltUnit());
                }
                dataList.add(all);
            }
        });
        // 设置封面汇总信息
        dataAll.setProjectName(project.getName()).setReportCode(project.getReportNoPre())
                .setJcCompany(project.getNameJc());
        for (ProMission mission : missions) {
            if (!MissionTypeConst.MANUAL_PATROL.equals(mission.getTypeName())){
                String empInfo = "监测："+mission.getEmpObserve()+"               计算："+mission.getEmpCalculate()
                        +"                检核："+mission.getEmpReview()+"               项目负责人："+mission.getEmpManager();
                dataAll.setEmpInfo(empInfo);
            }
        }
        if (dataList.size()>0 && dataList.stream().anyMatch(item -> "超限".equals(item.getNote()))){
            dataAll.setAlarmStr("是☑    否□");
        }else {
            dataAll.setAlarmStr("是□    否☑");
        }
        // 设置工作描述等信息
        String desc = redisTemplate.opsForValue().get(RedisConstant.PREFIX + "tableDesc");
        if (StringUtils.isNotEmpty(desc)){
            String[] split = desc.split("<>");
            if (split.length == 3){
                dataAll.setWorkDesc(StringUtils.isEmpty(split[0]) ? "无" : split[0]);
                dataAll.setStatusDesc(StringUtils.isEmpty(split[1]) ? "无" : split[1]);
                dataAll.setResult(StringUtils.isEmpty(split[2]) ? "无" : split[2]);
            }else {
                dataAll.setWorkDesc("无");
                dataAll.setStatusDesc("无");
                dataAll.setResult("无");
            }
        }else {
            dataAll.setWorkDesc("无");
            dataAll.setStatusDesc("无");
            dataAll.setResult("无");
        }
        dataAll.setDataList(dataList);
        dataAll.setDepthDataList(depthData);
        dataAll.setNoDepthDataList(noDepthData);
        return dataAll;
    }

    /**
     * @description 获取非水平分层任务数据
     **/
    private void addNoDepthData(List<PtGroup> ptGroups, ProMission mission, List<ReportData> collect, List<NoDepthData> noDepthData) {
        List<PtGroup> groups = ptGroups.stream().filter(item -> item.getMissionId().equals(mission.getId())).collect(Collectors.toList());
        if (groups.size() == 0){
            return;
        }
        List<Long> groupIds = groups.stream().map(PtGroup::getId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId, groupIds);
        List<Point> points = pointService.list(wrapper);
        for (PtGroup group : groups) {
            List<Point> ptList = points.stream().filter(item -> item.getPtGroupId().equals(group.getId())).collect(Collectors.toList());
            if (ptList.size() == 0) {
                continue;
            }
            List<Long> ptIds = ptList.stream().map(Point::getId).distinct().collect(Collectors.toList());
            List<ReportData> dataList = collect.stream().filter(item -> ptIds.contains(item.getPtId())).collect(Collectors.toList());
            if (dataList.size() == 0) {
                continue;
            }
            List<String> ptNames = dataList.stream().map(ReportData::getPtName).distinct().collect(Collectors.toList());
            NoDepthData data = new NoDepthData();
            data.setMissionName(mission.getName()).setGroupId(group.getId()).setGroupName(group.getName())
                    .setEquipName(mission.getEquipName()).setEquipNo(mission.getEquipNo())
                    .setObserve(mission.getEmpObserve()).setCalculate(mission.getEmpCalculate()).setReview(mission.getEmpReview())
                    .setGetTime(dataList.get(0).getGetTime()).setPreTime(dataList.get(dataList.size()-1).getGetTimePre())
                    .setZl(MissionTypeConst.ZC_FORCE.equals(mission.getTypeName()));
            List<NoDepthCalData> calDataList = new ArrayList<>();
            for (String ptName : ptNames) {
                List<ReportData> tempList = dataList.stream().filter(item -> item.getPtName().equals(ptName)).collect(Collectors.toList());
                addToNoDepthList(ptName, mission, tempList, calDataList);
            }
            data.setCalDataList(calDataList);
            List<NoDepthChartData> chartDataList = new ArrayList<>();
            dataList.sort(Comparator.comparing(ReportData::getGetTime));
            for (ReportData rpData : dataList) {
                NoDepthChartData chartData = new NoDepthChartData();
                Date time = rpData.getGetTime();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                if (time != null) {
                    chartData.setGetTimeStr(format1.format(time) + "\r\n" + format2.format(time));
                }
                chartData.setPtName(rpData.getPtName()).setTotalNum(rpData.getTotalNum()).setGetTime(time);
                chartDataList.add(chartData);
            }
            data.setChartDataList(chartDataList);
            noDepthData.add(data);
        }
    }
    /**
     * @description 添加非水平分层计算数据
     **/
    private void addToNoDepthList(String ptName, ProMission mission, List<ReportData> tempList, List<NoDepthCalData> calDataList) {
        NoDepthCalData data = new NoDepthCalData();
        //（a）点名：直接赋值；
        data.setPtName(ptName);
        //（b）初始值：查询结果列表中最后一个监测周期的Z0
        data.setNum0(tempList.get(0).getNum0());
        //（c）本次变化量: 查询结果列表中最后一个监测周期的TotalZ - 最早一个监测周期的TotalZPrev；
        double deltNum = tempList.get(0).getTotalNum() - tempList.get(tempList.size() - 1).getTotalPreNum();
        data.setDeltNum(deltNum);
        //（d）累计变化量：查询结果列表中最后一个监测周期的TotalZ;
        data.setTotalNum(tempList.get(0).getTotalNum());
        //（e）变化速率：VDeltZ=DeltZ/天数（天数=查询结果列表中最后一个监测周期的GetTime - 最早一个监测周期的GetTimePrev,并根据监测任务中设定的间隔计算方式获得天数）;
        double dayNum = getDayNum(tempList, mission);
        double vdeltNum = dayNum==0 ? 0.0 : deltNum / dayNum;
        data.setVDeltNum(vdeltNum);
        //（f）监测预警值/报警值/控制值：由编组挂接的报警设置中的数值(只考虑1项的设置，按“控制值”->“报警值”->“预警值”顺序查找，找到即可，填数值落入报警区间的低位值；若无报警设置，填“-”)；
        setThresholdAndNote(data, tempList, mission);
        //（g）备注：累计变化量、变化速率有一项超限，填写“超限”，否则，填写查询结果列表中最后一个监测周期的工况状态(填不是“正常”的值，“正常”不用填，保留为空)；
        calDataList.add(data);
    }

    /**
     * @description 设置监测阈值和备注信息
     **/
    private void setThresholdAndNote(NoDepthCalData data, List<ReportData> tempList, ProMission mission) {
        LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmItem::getAlarmGroupId, tempList.get(0).getAlarmGroupId())
                .eq(AlarmItem::getMonitorType, mission.getTypeName()).eq(AlarmItem::getAlarmType, "1");
        List<AlarmItem> alarmItems = alarmItemService.list(wrapper);
        List<AlarmItem> totalList = alarmItems.stream().filter(item -> "累计变化量".equals(item.getResultItemType())).collect(Collectors.toList());
        List<AlarmItem> vDeltList = alarmItems.stream().filter(item -> "日变化速率".equals(item.getResultItemType())).collect(Collectors.toList());
        if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(mission.getTypeName()) || MissionTypeConst.HAND_XYZ_OFFSET.equals(mission.getTypeName())){
            totalList = alarmItems.stream().filter(item -> "平面位移(P)".equals(item.getResultType())).collect(Collectors.toList());
            vDeltList = alarmItems.stream().filter(item -> "平面位移(P)".equals(item.getResultType())).collect(Collectors.toList());
        }
        String totalTh = getThresholdStr(totalList, data.getTotalNum(), data);
        String vDeltTh = getThresholdStr(vDeltList, data.getVDeltNum(), data);
        String threshold = totalTh + "/" + vDeltTh;
        if (StringUtils.isEmpty(data.getNote())){
            data.setNote(!"正常".equals(tempList.get(0).getStatus()) ?  tempList.get(0).getStatus() : "");
        }
        data.setThreshold(threshold);
    }

    /**
     * @description 获取报警阈值
     **/
    private String getThresholdStr(List<AlarmItem> itemList, double totalNum, NoDepthCalData data) {
        String threshold = "-";
        if (itemList.size() == 0) {
            return threshold;
        }
        List<AlarmItem> controlList = itemList.stream().filter(item -> "3".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (controlList.size() > 0)
        {
            return getThStr(controlList, totalNum, data);
        }
        List<AlarmItem> alarmList = itemList.stream().filter(item -> "2".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (alarmList.size() > 0)
        {
            return getThStr(alarmList, totalNum, data);
        }
        List<AlarmItem> preList = itemList.stream().filter(item -> "1".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (preList.size() > 0)
        {
            return getThStr(preList, totalNum, data);
        }
        return threshold;
    }

    /**
     * @description 获取阈值下限值
     **/
    private String getThStr(List<AlarmItem> alarmItems, double value, NoDepthCalData data) {
        AlarmItem item = alarmItems.get(0);
        String[] thSplit = item.getAlarmThreshold().replace("(", "")
                .replace("]", "").split(",");
        value = item.getAbsValue() ? Math.abs(value) : value;
        String threshold = thSplit[0];
        if ("∞".equals(thSplit[1]) && value > Double.parseDouble(thSplit[0]))
        {
            data.setNote("超限");
        }
        if ("-∞".equals(thSplit[1]) && value <= Double.parseDouble(thSplit[0]))
        {
            data.setNote("超限");
        }
        if (!thSplit[1].contains("∞") && value >= Double.parseDouble(thSplit[0]) && value <= Double.parseDouble(thSplit[1]))
        {
            data.setNote("超限");
        }
        return threshold;
    }

    /**
     * @description 获取水平分层任务数据
     **/
    private void addDepthData(List<PtGroup> ptGroups, ProMission mission, List<ReportData> collect, List<DepthData> depthData) {
        List<PtGroup> groups = ptGroups.stream().filter(item -> item.getMissionId().equals(mission.getId())).collect(Collectors.toList());
        if (groups.size() == 0){
            return;
        }
        List<Long> groupIds = groups.stream().map(PtGroup::getId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId, groupIds);
        List<Point> points = pointService.list(wrapper);
        for (PtGroup group : groups) {
            List<Point> ptList = points.stream().filter(item -> item.getPtGroupId().equals(group.getId())).collect(Collectors.toList());
            if (ptList.size() == 0) {
                continue;
            }
            List<Long> ptIds = ptList.stream().map(Point::getId).distinct().collect(Collectors.toList());
            List<ReportData> dataList = collect.stream().filter(item -> ptIds.contains(item.getPtId())).collect(Collectors.toList());
            if (dataList.size() == 0) {
                continue;
            }
            List<String> ptNames = dataList.stream().map(ReportData::getPtName).distinct().collect(Collectors.toList());
            DepthData data = new DepthData();
            data.setMissionName(mission.getName()).setGroupId(group.getId()).setGroupName(group.getName())
                    .setEquipName(mission.getEquipName()).setEquipNo(mission.getEquipNo())
                    .setObserve(mission.getEmpObserve()).setCalculate(mission.getEmpCalculate()).setReview(mission.getEmpReview())
                    .setGetTime(dataList.get(0).getGetTime()).setPreTime(dataList.get(dataList.size()-1).getGetTimePre());
            List<DepthCalData> calDataList = new ArrayList<>();
            for (String ptName : ptNames) {
                List<ReportData> tempList = dataList.stream().filter(item -> item.getPtName().equals(ptName)).collect(Collectors.toList());
                addToDepthList(ptName, mission, tempList, calDataList);
            }
            data.setCalDataList(calDataList);
            depthData.add(data);
        }
    }

    /**
     * @description 添加水平分层计算数据
     **/
    private void addToDepthList(String ptName, ProMission mission, List<ReportData> tempList, List<DepthCalData> calDataList) {
        DepthCalData data = new DepthCalData();
        //（a）深度：直接赋值；
        data.setDepth(ptName.contains(".") ? ptName : ptName + ".0");
        //（b）本次增量:  deltZ=查询结果列表中最后一个监测周期的TotalZ - 最早一个监测周期的TotalZPrev；
        double deltNum = tempList.get(0).getTotalNum() - tempList.get(tempList.size() - 1).getTotalPreNum();
        data.setDeltNum(deltNum);
        //（c）上次累计：查询结果列表中最早一个监测周期的TotalZPrev;
        data.setLastTotal(tempList.get(tempList.size() - 1).getTotalPreNum());
        //（d）本次累计：查询结果列表中最后一个监测周期的TotalZ;
        data.setTotalNum(tempList.get(0).getTotalNum());
        //（e）变化速率：VDeltZ=DeltZ/天数（天数=查询结果列表中最后一个监测周期的GetTime - 最早一个监测周期的GetTimePrev,并根据监测任务中设定的间隔计算方式获得天数）;
        double dayNum = getDayNum(tempList, mission);
        double vdeltNum = dayNum==0 ? 0.0 : deltNum / dayNum;
        data.setVDeltNum(vdeltNum);
        calDataList.add(data);
    }

    /**
     * 获取时间间隔天数
     */
    private double getDayNum(List<ReportData> tempList, ProMission mission) {
        double deltTime = 0.0;
        ReportData data = tempList.get(0);
        ReportData lastData = tempList.get(tempList.size()-1);
        double dayNum = (data.getGetTime().getTime() - lastData.getGetTime().getTime()) * 1.0 / (1000 * 60 * 60 * 24);
        if ("1".equals(mission.getCalculateType()))
        {
            deltTime = dayNum;
        }
        else if ("2".equals(mission.getCalculateType()))
        {
            deltTime = Math.ceil(dayNum);
        }
        else
        {
            deltTime = dayNum < 1.0 ? dayNum : Math.ceil(dayNum);
        }
        return Math.round(deltTime * 100)*1.0 / 100;
    }

    /**
     * @description 获取巡视记录数据
     **/
    private void setXsData(TableDataAll dataAll, TableDataCondition cond, List<ProMission> missions) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JcInfo::getProjectId, cond.getProjectId())
                .ge(cond.getStartDate()!=null, JcInfo::getJcDate, cond.getStartDate())
                .le(cond.getEndDate()!=null, JcInfo::getJcDate, cond.getEndDate())
                .orderByDesc(JcInfo::getJcDate);
        List<JcInfo> list = jcInfoService.list(wrapper);
        List<XsData> xsData = new ArrayList<>();
        if (list.size() == 0){
            dataAll.setXsDataList(xsData);
            return;
        }
        List<Long> missionIds = list.stream().map(JcInfo::getMissionId).distinct().collect(Collectors.toList());
        missionIds.forEach(missionId -> {
            List<JcInfo> collect = list.stream().filter(item -> item.getMissionId().equals(missionId)).collect(Collectors.toList());
            Optional<ProMission> optional = missions.stream().filter(mission -> mission.getId().equals(missionId)).findAny();
            if (collect.size() > 0 && optional.isPresent()){
                XsData data = new XsData();
                data.setInfoStr(collect.get(0).getInfo());
                ProMission mission = optional.get();
                data.setMissionName(mission.getName()).setXsMan(mission.getEmpObserve()).setReviewMan(mission.getEmpReview());
                xsData.add(data);
            }
        });
        dataAll.setXsDataList(xsData);
    }

    /**
     * @description 获取巡视记录数据
     **/
    private void setAlarmStr(List<Long> pidList, TableDataCondition cond, TableDataAll dataAll) {
        if (pidList.size() == 0){
          return ;
        }
        LambdaQueryWrapper<AlarmInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AlarmInfo::getPtId, pidList)
                .ge(cond.getStartDate()!=null, AlarmInfo::getAlarmTime, cond.getStartDate())
                .le(cond.getEndDate()!=null, AlarmInfo::getAlarmTime, cond.getEndDate());
        List<AlarmInfo> list = alarmInfoService.list(wrapper);
        if (list.size() > 0){
            StringBuilder builder = new StringBuilder();
            list.forEach(alarmInfo -> {
                if (StringUtils.isEmpty(builder.toString())){
                    builder.append(alarmInfo.getAlarmOrigin());
                }else {
                    builder.append("\r\n").append(alarmInfo.getAlarmOrigin());
                }
            });
            dataAll.setAlarmInfo(builder.toString());
        }
    }

    /**
     * @description 获取报警阈值参数
     **/
    private void getThresholdValue(List<AlarmItem> alarmItems, DataAll all) {
        List<AlarmItem> controlList = alarmItems.stream().filter(item -> "3".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (controlList.size() > 0){
            setThreshold(controlList, all);
            return;
        }
        List<AlarmItem> alarmList = alarmItems.stream().filter(item -> "2".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (alarmList.size() > 0){
            setThreshold(alarmList, all);
            return;
        }
        List<AlarmItem> preAlarmList = alarmItems.stream().filter(item -> "1".equals(item.getAlarmLevel())).collect(Collectors.toList());
        if (preAlarmList.size() > 0){
            setThreshold(preAlarmList, all);
        }
    }

    /**
     * @description 设置报警阈值参数
     **/
    private void setThreshold(List<AlarmItem> alarmItems, DataAll all) {
        AlarmItem item = alarmItems.get(0);
        String[] thSplit = item.getAlarmThreshold().replace("(", "")
                .replace("]", "").split(",");
        double value ="累计变化量".equals(item.getResultItemType()) ? all.getMaxTotalNum() : all.getMaxVdeltNum();
        value = item.getAbsValue() ? Math.abs(value) : value;
        if ("累计变化量".equals(item.getResultItemType()))
        {
            all.setTotalThreshold(thSplit[0]);
        }
        else
        {
            all.setVDeltThreshold(thSplit[0]);
        }
        if ("∞".equals(thSplit[1]) && value > Double.parseDouble(thSplit[0]))
        {
            all.setNote("超限");
        }
        if ("-∞".equals(thSplit[1]) && value <= Double.parseDouble(thSplit[0]))
        {
            all.setNote("超限");
        }
        if (!thSplit[1].contains("∞") && value >= Double.parseDouble(thSplit[0]) && value <= Double.parseDouble(thSplit[1]))
        {
            all.setNote("超限");
        }
    }

    /**
     * @description 时间参数格式化
    **/
    private void formatDate(TableDataCondition condition) {
        if (condition.getStartDate() != null){
            condition.setStartDate(condition.getStartDate());
        }
        if (condition.getEndDate() != null){
            condition.setEndDate(condition.getEndDate());
        }
    }

    @Override
    public List<JcInfoVO> getXsData(TableDataCondition cond) {
        if (cond.getMissionId() == null) {
            return new ArrayList<>();
        }
        formatDate(cond);
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JcInfo::getMissionId, cond.getMissionId())
                .ge(cond.getStartDate() != null, JcInfo::getJcDate, cond.getStartDate())
                .le(cond.getEndDate() != null, JcInfo::getJcDate, cond.getEndDate())
                .orderByDesc(JcInfo::getJcDate);
        return DzBeanUtils.listCopy(jcInfoService.list(wrapper), JcInfoVO.class);
    }

    @Override
    public ResponseUtil getListData(TableDataCondition condition) {
        if (condition.getMissionId() == null) {
            return ResponseUtil.success(new ArrayList<>());
        }
        //查询分组下测点集合
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Point::getPtGroupId, condition.getGroupId()).orderByDesc(Point::getCreateTime);
        List<Point> points = pointService.list(wrapper);
        if (points.size() == 0) {
            TableDataCommon dataCommon = new TableDataCommon();
            dataCommon.setDataCalculateList(new ArrayList<>());
            dataCommon.setDataXyzList(new ArrayList<>());
            dataCommon.setDataZList(new ArrayList<>());
            return ResponseUtil.success(dataCommon);
        }
        List<Long> pointIds = points.stream().map(Point::getId).collect(Collectors.toList());
        formatDate(condition);
        if (condition.getIsXyz()!=null && condition.getIsXyz()){
            return getXyzList(condition, pointIds);
        }else {
            return getCommonList(condition, pointIds);
        }
    }

    @Override
    public String saveChartImag(EchartData echartData) {
        return FileUtil.storeEchart(echartData);
    }

    @Override
    public void exportAll(TableDataAll dataAll, HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/项目监测报表模板.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 添加封面
            addCover(wb, dataAll);
            // 添加汇总表
            addSummary(wb, dataAll);
            // 添加巡视表
            addXsItem(wb, dataAll);
            // 添加水平分层表
            addDepthItem(wb, dataAll);
            // 添加常规表
            addCommonItem(wb, dataAll);
            // 删除模板表
            deleteDefaultSheet(wb);
            // 导出列表
            exportExcel(dataAll.getProjectName() + "_监测数据报表.xlsx", response, wb);
            // 更新报表期数
            updateReportCycle(dataAll);
            String sb = dataAll.getWorkDesc() + "<>" +
                    dataAll.getStatusDesc() + "<>" +
                    dataAll.getResult();
            redisTemplate.opsForValue().set(RedisConstant.PREFIX + "tableDesc", sb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void exportXs(XsExportData data, HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/项目监测报表模板.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 查询工程和任务信息
            JcInfoVO info = data.getJcInfoVO();
            if (info == null){
                return;
            }
            Project project = projectService.getById(info.getProjectId());
            ProMission mission = missionService.getById(info.getMissionId());
            project = project==null ? new Project() : project;
            mission = mission==null ? new ProMission() : mission;
            Sheet sheet = wb.cloneSheet(4);
            wb.setSheetName(wb.getSheetIndex(sheet), mission.getName());
            sheet.getRow(0).getCell(0).setCellValue(project.getName());
            sheet.getRow(1).getCell(1).setCellValue(project.getNameJc());
            sheet.getRow(2).getCell(1).setCellValue(data.getReportNo());
            sheet.getRow(2).getCell(3).setCellValue(project.getReportNoPre() + data.getReportCode());
            sheet.getRow(3).getCell(0).setCellValue(mission.getName());
            String[] split = info.getInfo().split("\\|\\|");
            for (int i = 5; i < 23; i++){
                String[] values = split[i - 5].split("<\\$>");
                sheet.getRow(i).getCell(3).setCellValue(values[0]);
                sheet.getRow(i).getCell(5).setCellValue(values.length==2 ? values[1] : "");
            }
            String empInfo = "巡视："+mission.getEmpObserve()+"                                                                检核："+mission.getEmpReview();
            sheet.getRow(23).getCell(0).setCellValue(empInfo);
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            sheet.getRow(24).getCell(0).setCellValue(format.format(info.getJcDate()));
            // 删除模板表
            deleteDefaultSheet(wb);
            // 导出列表
            exportExcel(mission.getName() + "_巡视记录.xlsx", response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void exportFc(TableDataCommon data, HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/项目监测报表模板.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            int dataSize = data.getDataCalculateList().size();
            Sheet sheet = wb.cloneSheet(dataSize > 66 ? 6 : 5);
            wb.setSheetName(wb.getSheetIndex(sheet), data.getMissionName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
            setSheetInfo(0, data, sheet);
            if (dataSize > 66){
                setSheetInfo(43, data, sheet);
            }
            int indexRow = 6;
            int indexRow2 = 6;
            int index2Row = 49;
            int index2Row2 = 49;
            int index = -1;
            for (TableDataCalculate calData : data.getDataCalculateList()) {
                index++;
                int row;
                if (index <= 32 || (index > 65 && index <= 98))
                {
                    row = ++indexRow;
                    if ( index > 65)
                    {
                        row = ++index2Row;
                    }
                    sheet.getRow(row).getCell(0).setCellValue(calData.getName());
                    sheet.getRow(row).getCell(1).setCellValue(calData.getDeltValue());
                    sheet.getRow(row).getCell(2).setCellValue(calData.getTotalPreValue());
                    sheet.getRow(row).getCell(3).setCellValue(calData.getTotalValue());
                    sheet.getRow(row).getCell(4).setCellValue(calData.getVDeltValue());
                }
                else
                {
                    row = ++indexRow2;
                    if (index > 98)
                    {
                        row = ++index2Row2;
                    }
                    sheet.getRow(row).getCell(5).setCellValue(calData.getName());
                    sheet.getRow(row).getCell(6).setCellValue(calData.getDeltValue());
                    sheet.getRow(row).getCell(7).setCellValue(calData.getTotalPreValue());
                    sheet.getRow(row).getCell(8).setCellValue(calData.getTotalValue());
                    sheet.getRow(row).getCell(9).setCellValue(calData.getVDeltValue());
                }
            }
            // 添加图片
            addEChartWithUrl(wb, sheet, data.getDataUrl(), 7, 40, 10, 11);
            // 删除模板表
            deleteDefaultSheet(wb);
            // 导出列表
            exportExcel(data.getMissionName() + "_监测数据.xlsx", response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void exportCommon(TableDataCommon data, HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/项目监测报表模板.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
            List<TableDataCalculate> list = data.getDataCalculateList();
            int chartRow;
            int sheetIndex;
            if (list.size() <= 10){
                chartRow = 17;
                sheetIndex = 7;
            }else if (list.size() <= 15){
                chartRow = 22;
                sheetIndex = 8;
            }else if (list.size() <= 20){
                chartRow = 27;
                sheetIndex = 9;
            }else if (list.size() <= 25){
                chartRow = 32;
                sheetIndex = 10;
            }else {
                chartRow = 37;
                sheetIndex = 11;
            }
            Sheet sheet = wb.cloneSheet(sheetIndex);
            wb.setSheetName(wb.getSheetIndex(sheet), data.getMissionName());
            sheet.getRow(0).getCell(0).setCellValue(data.getProjectName());
            sheet.getRow(1).getCell(1).setCellValue(data.getJcCompany());
            sheet.getRow(2).getCell(1).setCellValue(data.getReportNo());
            sheet.getRow(2).getCell(5).setCellValue(data.getReportCode());
            sheet.getRow(3).getCell(0).setCellValue(data.getMissionName());
            sheet.getRow(4).getCell(2).setCellValue(format.format(data.getPreTime()));
            sheet.getRow(4).getCell(6).setCellValue(data.getEquipName());
            sheet.getRow(5).getCell(2).setCellValue(format.format(data.getGetTime()));
            sheet.getRow(5).getCell(6).setCellValue(data.getEquipNo());
            if (data.getZl()!=null && data.getZl()){
                sheet.getRow(6).getCell(1).setCellValue("初始值(KN)");
                sheet.getRow(6).getCell(2).setCellValue("本次变化量(KN)");
                sheet.getRow(6).getCell(3).setCellValue("累计变化量(KN)");
                sheet.getRow(6).getCell(4).setCellValue("变化速率(KN/d)");
                sheet.getRow(6).getCell(5).setCellValue("监测控制值(KN)/(KN/d)");
            }
            int indexRow = 6;
            for (TableDataCalculate calData : list) {
                indexRow++;
                sheet.getRow(indexRow).getCell(0).setCellValue(calData.getName());
                sheet.getRow(indexRow).getCell(1).setCellValue(calData.getValue0());
                sheet.getRow(indexRow).getCell(2).setCellValue(calData.getDeltValue());
                sheet.getRow(indexRow).getCell(3).setCellValue(calData.getTotalValue());
                sheet.getRow(indexRow).getCell(4).setCellValue(calData.getVDeltValue());
                sheet.getRow(indexRow).getCell(5).setCellValue(calData.getThreshold());
                sheet.getRow(indexRow).getCell(6).setCellValue(calData.getNote());
            }
            sheet.getRow(chartRow+1).getCell(1).setCellValue(data.getNote());
            String empInfo = "观测："+data.getObserve()+"                           计算："+data.getCalculate()
                    +"                          检核："+data.getReview();
            sheet.getRow(chartRow+2).getCell(0).setCellValue(empInfo);
            sheet.getRow(chartRow+3).getCell(0).setCellValue(format2.format(data.getGetTime()));
            // 添加图片
            addEChartWithUrl(wb, sheet, data.getDataUrl(), chartRow, chartRow + 1, 1, 7);
            // 删除模板表
            deleteDefaultSheet(wb);
            // 导出列表
            exportExcel(data.getMissionName() + "_监测数据.xlsx", response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置水平分层表属性值
     */
    private void setSheetInfo(int addRow, TableDataCommon data, Sheet sheet){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
        // 数据前属性
        sheet.getRow(addRow).getCell(0).setCellValue(data.getProjectName());
        sheet.getRow(1 + addRow).getCell(1).setCellValue(data.getJcCompany());
        sheet.getRow(2 + addRow).getCell(2).setCellValue(data.getReportNo());
        sheet.getRow(2 + addRow).getCell(8).setCellValue(data.getReportCode());
        sheet.getRow(3 + addRow).getCell(0).setCellValue(data.getMissionName());
        sheet.getRow(4 + addRow).getCell(2).setCellValue(format.format(data.getPreTime()));
        sheet.getRow(4 + addRow).getCell(8).setCellValue(data.getEquipName());
        sheet.getRow(5 + addRow).getCell(2).setCellValue(format.format(data.getGetTime()));
        sheet.getRow(5 + addRow).getCell(8).setCellValue(data.getEquipNo());
        // 数据后属性
        sheet.getRow(40 + addRow).getCell(1).setCellValue(data.getNote());
        String empInfo = "观测："+data.getObserve()+"                           计算："+data.getCalculate()
                +"                          检核："+data.getReview();
        sheet.getRow(41 + addRow).getCell(0).setCellValue(empInfo);
        sheet.getRow(42 + addRow).getCell(0).setCellValue(format2.format(data.getGetTime()));
    }

    /**
     * 更新报表期数
     */
    private void updateReportCycle(TableDataAll dataAll) {
        if (dataAll.getProjectId() == null){
            return;
        }
        Project project = projectService.getById(dataAll.getProjectId());
        if (project == null){
            return;
        }
        String extraInfo = project.getExtraInfo();
        if (StringUtils.isEmpty(extraInfo)){
            extraInfo = "1,1,1,1,1";
        }
        String[] split = extraInfo.split(",");
        split[dataAll.getReportTypeIndex()] = Integer.valueOf(split[dataAll.getReportTypeIndex()]) + 1 + "";
        String newExtraInfo = Arrays.toString(split).replace("[", "")
                .replace("]", "").replace(" ", "");
        project.setExtraInfo(newExtraInfo);
        projectService.updateById(project);
    }

    /**
     * 导出表格
     */
    private void exportExcel(String fileName, HttpServletResponse response, Workbook wb) {
        try {
            response.reset();
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1));
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除模板默认子表
     */
    private void deleteDefaultSheet(Workbook wb) {
        for (int i = 11; i >= 0; i--)
        {
            wb.removeSheetAt(i);
        }
    }

    /**
     * 添加常规任务页： 模板页索引6-10
     */
    private void addCommonItem(Workbook wb, TableDataAll dataAll) {
        List<NoDepthData> dataList = dataAll.getNoDepthDataList();
        if (dataList==null || dataList.size()==0){
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (NoDepthData data : dataList) {
            List<NoDepthCalData> list = data.getCalDataList();
            int chartRow;
            int sheetIndex;
            if (list.size() <= 10){
                chartRow = 17;
                sheetIndex = 7;
            }else if (list.size() <= 15){
                chartRow = 22;
                sheetIndex = 8;
            }else if (list.size() <= 20){
                chartRow = 27;
                sheetIndex = 9;
            }else if (list.size() <= 25){
                chartRow = 32;
                sheetIndex = 10;
            }else {
                chartRow = 37;
                sheetIndex = 11;
            }
            Sheet sheet = wb.cloneSheet(sheetIndex);
            String sheetName = data.getMissionName() + "(" + data.getGroupName() + ")";
            wb.setSheetName(wb.getSheetIndex(sheet), sheetName);
            sheet.getRow(0).getCell(0).setCellValue(dataAll.getProjectName());
            sheet.getRow(1).getCell(1).setCellValue(dataAll.getJcCompany());
            sheet.getRow(2).getCell(1).setCellValue(dataAll.getReportNo());
            sheet.getRow(2).getCell(5).setCellValue(dataAll.getReportCode());
            sheet.getRow(3).getCell(0).setCellValue(data.getMissionName());
            sheet.getRow(4).getCell(2).setCellValue(format.format(data.getPreTime()));
            sheet.getRow(4).getCell(6).setCellValue(data.getEquipName());
            sheet.getRow(5).getCell(2).setCellValue(format.format(data.getGetTime()));
            sheet.getRow(5).getCell(6).setCellValue(data.getEquipNo());
            if (data.getZl()!=null && data.getZl()){
                sheet.getRow(6).getCell(1).setCellValue("初始值(KN)");
                sheet.getRow(6).getCell(2).setCellValue("本次变化量(KN)");
                sheet.getRow(6).getCell(3).setCellValue("累计变化量(KN)");
                sheet.getRow(6).getCell(4).setCellValue("变化速率(KN/d)");
                sheet.getRow(6).getCell(5).setCellValue("监测控制值(KN)/(KN/d)");
            }
            int indexRow = 6;
            for (NoDepthCalData calData : list) {
                indexRow++;
                sheet.getRow(indexRow).getCell(0).setCellValue(calData.getPtName());
                sheet.getRow(indexRow).getCell(1).setCellValue(calData.getNum0());
                sheet.getRow(indexRow).getCell(2).setCellValue(calData.getDeltNum());
                sheet.getRow(indexRow).getCell(3).setCellValue(calData.getTotalNum());
                sheet.getRow(indexRow).getCell(4).setCellValue(calData.getVDeltNum());
                sheet.getRow(indexRow).getCell(5).setCellValue(calData.getThreshold());
                sheet.getRow(indexRow).getCell(6).setCellValue(calData.getNote());
            }
            sheet.getRow(chartRow+1).getCell(1).setCellValue(data.getNote());
            String empInfo = "观测："+data.getObserve()+"                           计算："+data.getCalculate()
                    +"                          检核："+data.getReview();
            sheet.getRow(chartRow+2).getCell(0).setCellValue(empInfo);
            String[] dateSplit = dataAll.getReportDate().split("-");
            String dateStr = dateSplit.length==1 ? dateSplit[0] : (dateSplit.length==2 ? dateSplit[1] : "");
            sheet.getRow(chartRow+3).getCell(0).setCellValue(dateStr);
            // 添加图片
            addEChartWithUrl(wb, sheet, data.getEChartPath(), chartRow, chartRow + 1, 1, 7);
        }
    }

    /**
     * 添加水平分层页： 模板页索引5
     */
    private void addDepthItem(Workbook wb, TableDataAll dataAll) {
        List<DepthData> dataList = dataAll.getDepthDataList();
        if (dataList==null || dataList.size()==0){
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (DepthData data : dataList) {
            int dataSize = data.getCalDataList().size();
            Sheet sheet = wb.cloneSheet(dataSize > 66 ? 6 : 5);
            String sheetName = data.getMissionName() + "(" + data.getGroupName() + ")";
            wb.setSheetName(wb.getSheetIndex(sheet), sheetName);
            setSheetInfo(0, dataAll, data, sheet);
            if (dataSize > 66){
                setSheetInfo(43, dataAll, data, sheet);
            }
            int indexRow = 6;
            int indexRow2 = 6;
            int index2Row = 49;
            int index2Row2 = 49;
            int index = -1;
            for (DepthCalData calData : data.getCalDataList()) {
                index++;
                int row;
                if (index <= 32 || (index > 65 && index <= 98))
                {
                    row = ++indexRow;
                    if ( index > 65)
                    {
                        row = ++index2Row;
                    }
                    sheet.getRow(row).getCell(0).setCellValue(calData.getDepth());
                    sheet.getRow(row).getCell(1).setCellValue(calData.getDeltNum());
                    sheet.getRow(row).getCell(2).setCellValue(calData.getLastTotal());
                    sheet.getRow(row).getCell(3).setCellValue(calData.getTotalNum());
                    sheet.getRow(row).getCell(4).setCellValue(calData.getVDeltNum());
                }
                else
                {
                    row = ++indexRow2;
                    if (index > 98)
                    {
                        row = ++index2Row2;
                    }
                    sheet.getRow(row).getCell(5).setCellValue(calData.getDepth());
                    sheet.getRow(row).getCell(6).setCellValue(calData.getDeltNum());
                    sheet.getRow(row).getCell(7).setCellValue(calData.getLastTotal());
                    sheet.getRow(row).getCell(8).setCellValue(calData.getTotalNum());
                    sheet.getRow(row).getCell(9).setCellValue(calData.getVDeltNum());
                }
            }
            // 添加图片
            addEChartWithUrl(wb, sheet, data.getEChartPath(), 7, 40, 10, 11);
        }
    }

    /**
     * 添加汇总水平分层属性值
     */
    private void setSheetInfo(int addRow, TableDataAll dataAll,DepthData data, Sheet sheet){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 数据前属性
        sheet.getRow(addRow).getCell(0).setCellValue(dataAll.getProjectName());
        sheet.getRow(1 + addRow).getCell(1).setCellValue(dataAll.getJcCompany());
        sheet.getRow(2 + addRow).getCell(2).setCellValue(dataAll.getReportNo());
        sheet.getRow(2 + addRow).getCell(8).setCellValue(dataAll.getReportCode());
        sheet.getRow(3 + addRow).getCell(0).setCellValue(data.getMissionName());
        sheet.getRow(4 + addRow).getCell(2).setCellValue(format.format(data.getPreTime()));
        sheet.getRow(4 + addRow).getCell(8).setCellValue(data.getEquipName());
        sheet.getRow(5 + addRow).getCell(2).setCellValue(format.format(data.getGetTime()));
        sheet.getRow(5 + addRow).getCell(8).setCellValue(data.getEquipNo());
        // 数据后属性
        sheet.getRow(40 + addRow).getCell(1).setCellValue(data.getNote());
        String empInfo = "观测："+data.getObserve()+"                           计算："+data.getCalculate()
                +"                          检核："+data.getReview();
        sheet.getRow(41 + addRow).getCell(0).setCellValue(empInfo);
        String[] dateSplit = dataAll.getReportDate().split("-");
        String dateStr = dateSplit.length==1 ? dateSplit[0] : (dateSplit.length==2 ? dateSplit[1] : "");
        sheet.getRow(42 + addRow).getCell(0).setCellValue(dateStr);
    }

    /**
     * 添加巡视页： 模板页索引4
     */
    private void addXsItem(Workbook wb, TableDataAll dataAll) {
        List<XsData> xsDataList = dataAll.getXsDataList();
        if (xsDataList==null || xsDataList.size()==0){
            return;
        }
        for (XsData data : xsDataList) {
            Sheet sheet = wb.cloneSheet(4);
            wb.setSheetName(wb.getSheetIndex(sheet), data.getMissionName());
            sheet.getRow(0).getCell(0).setCellValue(dataAll.getProjectName());
            sheet.getRow(1).getCell(1).setCellValue(dataAll.getJcCompany());
            sheet.getRow(2).getCell(1).setCellValue(dataAll.getReportNo());
            sheet.getRow(2).getCell(3).setCellValue(dataAll.getReportCode());
            sheet.getRow(3).getCell(0).setCellValue(data.getMissionName());
            String[] split = data.getInfoStr().split("\\|\\|");
            for (int i = 5; i < 23; i++){
                String[] values = split[i - 5].split("<\\$>");
                sheet.getRow(i).getCell(3).setCellValue(values[0]);
                sheet.getRow(i).getCell(5).setCellValue(values.length==2 ? values[1] : "");
            }
            String empInfo = "巡视："+data.getXsMan()+"                                                                检核："+data.getReviewMan();
            sheet.getRow(23).getCell(0).setCellValue(empInfo);
            String[] dateSplit = dataAll.getReportDate().split("-");
            String dateStr = dateSplit.length==1 ? dateSplit[0] : (dateSplit.length==2 ? dateSplit[1] : "");
            sheet.getRow(24).getCell(0).setCellValue(dateStr);
        }
    }

    /**
     * 添加汇总页： 模板页索引1(10条)、2(15条)、3(20条)
     */
    private void addSummary(Workbook wb, TableDataAll dataAll) {
        int resultRow;
        int sheetIndex;
        List<DataAll> list = dataAll.getDataList();
        if(list.size() <= 10){
            resultRow = 22;
            sheetIndex = 1;
        }else if (list.size() <= 15){
            resultRow = 27;
            sheetIndex = 2;
        }else {
            resultRow = 32;
            sheetIndex = 3;
        }
        Sheet sheet = wb.cloneSheet(sheetIndex);
        wb.setSheetName(wb.getSheetIndex(sheet), "汇总");
        sheet.getRow(0).getCell(0).setCellValue(dataAll.getProjectName());
        sheet.getRow(1).getCell(1).setCellValue(dataAll.getJcCompany());
        sheet.getRow(2).getCell(1).setCellValue(dataAll.getReportNo());
        sheet.getRow(2).getCell(4).setCellValue(dataAll.getReportCode());
        sheet.getRow(5).getCell(0).setCellValue(dataAll.getWorkDesc());
        sheet.getRow(7).getCell(0).setCellValue(dataAll.getStatusDesc());
        int startIndex = 10;
        for (DataAll data : list) {
            startIndex++;
            sheet.getRow(startIndex).getCell(0).setCellValue(data.getMissionName());
            sheet.getRow(startIndex).getCell(1).setCellValue(data.getPtNametTotal());
            sheet.getRow(startIndex).getCell(2).setCellValue(data.getMaxTotalNum());
            sheet.getRow(startIndex).getCell(3).setCellValue(data.getPtNametVdelt());
            sheet.getRow(startIndex).getCell(4).setCellValue(data.getMaxVdeltNum());
            sheet.getRow(startIndex).getCell(5).setCellValue(data.getTotalThreshold());
            sheet.getRow(startIndex).getCell(6).setCellValue(data.getVDeltThreshold());
            sheet.getRow(startIndex).getCell(7).setCellValue(data.getNote());
        }
        String[] dateSplit = dataAll.getReportDate().split("-");
        String dateStr = dateSplit.length==1 ? dateSplit[0] : (dateSplit.length==2 ? dateSplit[1] : "");
        sheet.getRow(resultRow).getCell(0).setCellValue(dataAll.getResult());
        sheet.getRow(resultRow+1).getCell(0).setCellValue(dataAll.getEmpInfo());
        sheet.getRow(resultRow+2).getCell(0).setCellValue(dateStr);
    }

    /**
     * 添加封面页： 模板页索引0
     */
    private void addCover(Workbook wb, TableDataAll dataAll) {
        // 复制sheet
        Sheet sheet = wb.cloneSheet(0);
        wb.setSheetName(wb.getSheetIndex(sheet), "封面");
        // 项目名称 1 0
        sheet.getRow(1).getCell(0).setCellValue(dataAll.getProjectName());
        // 报表类型 3 0
        sheet.getRow(3).getCell(0).setCellValue(dataAll.getReportType());
        // 报表期数 5 2
        sheet.getRow(5).getCell(2).setCellValue(dataAll.getReportNo());
        // 报表编号 6 2
        sheet.getRow(6).getCell(2).setCellValue(dataAll.getReportCode());
        // 监测日期 7 2
        sheet.getRow(7).getCell(2).setCellValue(dataAll.getReportDate());
        // 报警信息 8 2
        sheet.getRow(8).getCell(2).setCellValue(dataAll.getAlarmStr());
        // 监测单位 17 0
        sheet.getRow(17).getCell(0).setCellValue(dataAll.getJcCompany());
    }

    /**
     * 添加EChart图片 根据编码串生成
     */
    private static final String BASE_PATH = "File";
    private void addEChartWithUrl(Workbook wb, Sheet sheet, String dataUrl, int startRow, int endRow, int startCol, int endCol) {
        if (dataUrl == null || !dataUrl.contains("base64,")){
            return;
        }
        //拆分base64编码后部分
        String[] imgUrlArr = dataUrl.split("base64,");
        byte[] buffer = Base64.decode(imgUrlArr[1]);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String picPath = BASE_PATH + File.separatorChar + "temp-chart" + File.separatorChar +  format.format(new Date());
        String picName = UUID.randomUUID() +".png";
        File file = new File(picPath);//图片文件
        if (!file.exists()) {
            if (!file.mkdirs()) {
                // 如果创建失败
                throw new RuntimeException("创建文件夹失败");
            }
        }
        FileOutputStream out = null;
        ByteArrayOutputStream outStream = null;
        try {
            //生成图片
            out = new FileOutputStream(new File(file, picName));
            out.write(buffer);
            outStream = new ByteArrayOutputStream(); // 将图片写入流中
            BufferedImage bufferImg = ImageIO.read(new File(file, picName));
            ImageIO.write(bufferImg, "PNG", outStream);
            Drawing<?> patriarch = sheet.createDrawingPatriarch();
            ClientAnchor anchor = new XSSFClientAnchor(0 ,0 ,0 , 0, startCol , startRow, endCol, endRow);
            patriarch.createPicture(anchor, wb.addPicture(outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加EChart图片 根据路径生成
     */
    private void addEChartWithPath(Workbook wb, Sheet sheet, String path, int startRow, int endRow, int startCol, int endCol) {
        if (path == null || !path.contains("/")){
            return;
        }
        path = "File/temp-chart/2022-06-10/7d5fa47e-ff25-4b0c-9660-df5192c7fc4f.png";
        String[] split = path.split("/");
        String filePath = "";
        for (String str : split) {
            if (StringUtils.isEmpty(filePath)){
                filePath += str;
            }else {
                filePath += File.separatorChar + str;
            }
        }
        ByteArrayOutputStream outStream = null;
        try {
            outStream = new ByteArrayOutputStream(); // 将图片写入流中
            BufferedImage bufferImg = ImageIO.read(new File(filePath));
            ImageIO.write(bufferImg, "PNG", outStream);
            Drawing<?> patriarch = sheet.createDrawingPatriarch();
            ClientAnchor anchor = new XSSFClientAnchor(0 ,0 ,0 , 0, startCol , startRow, endCol, endRow);
            patriarch.createPicture(anchor, wb.addPicture(outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @description 获取非三维位移列表信息
     **/
    private ResponseUtil getCommonList(TableDataCondition condition, List<Long> pointIds) {
        TableDataCommon dataCommon = new TableDataCommon();
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataZ::getPid, pointIds)
                .ge(condition.getStartDate()!=null, PointDataZ::getGetTime, condition.getStartDate())
                .le(condition.getEndDate()!=null, PointDataZ::getGetTime, condition.getEndDate())
                .orderByDesc(PointDataZ::getGetTime).orderByDesc(PointDataZ::getRecycleNum);
        List<PointDataZVO> list = DzBeanUtils.listCopy(dataZService.list(wrapper), PointDataZVO.class);
        if (condition.getIsFc()!=null && condition.getIsFc()){
            list.forEach(item -> {
                if (!item.getName().contains(".")){
                    item.setName(item.getName() + ".0");
                }
            });
        }else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            list.forEach(data -> {
                Date getTime = data.getGetTime();
                if (getTime != null) {
                    data.setGetTimeStr(format.format(getTime) + "\r\n" + format2.format(getTime));
                }
            });
        }
        dataCommon.setDataZList(list);
        getCommonCalculateData(dataCommon, list, condition);
        // 设置报表信息
        setReportInfo(dataCommon, condition);
        if (list.size() > 0){
            dataCommon.setGetTime(list.get(0).getGetTime()).setPreTime(list.get(list.size()-1).getPrevGetTime());
        }
        return ResponseUtil.success(dataCommon);
    }

    /**
     * @description 获取常规计算数据
     **/
    private void getCommonCalculateData(TableDataCommon dataCommon, List<PointDataZVO> list, TableDataCondition condition) {
        if(list.size() == 0){
            dataCommon.setDataCalculateList(new ArrayList<>());
            return;
        }
        List<TableDataCalculate> calculateList = new ArrayList<>();
        List<Long> pidList = list.stream().map(PointDataZVO::getPid).distinct().collect(Collectors.toList());
        ProMission mission = missionService.findById(condition.getMissionId());
        // 规则计算
        pidList.forEach(pid -> {
            TableDataCalculate calculate = new TableDataCalculate();
            List<PointDataZVO> collect = list.stream().filter(item -> item.getPid().equals(pid)).collect(Collectors.toList());
            //点名：直接赋值；
            calculate.setName(collect.get(0).getName());
            calculate.setStatus(collect.get(0).getStatus());
            //初始值：查询结果列表中最后一个监测周期的Z0
            calculate.setValue0(collect.get(0).getZ0());
            calculate.setPreValue(collect.get(collect.size() -1).getZPrev());
            calculate.setValue(collect.get(0).getZ());
            // 上次累计：查询结果列表中最早一个监测周期的TotalZPrev;
            calculate.setTotalPreValue(collect.get(collect.size() -1).getTotalZPrev());
            //本次变化量:  deltZ=查询结果列表中最后一个监测周期的TotalZ - 最早一个监测周期的TotalZPrev；
            calculate.setDeltValue(collect.get(0).getTotalZ() - collect.get(collect.size() -1).getTotalZPrev());
            //累计变化量：查询结果列表中最后一个监测周期的TotalZ;
            calculate.setTotalValue(collect.get(0).getTotalZ());
            //变化速率：VDeltZ=DeltZ/天数（天数=查询结果列表中最后一个监测周期的GetTime - 最早一个监测周期的GetTimePrev,并根据监测任务中设定的间隔计算方式获得天数）;
            double deltTime = getDeltTime(collect.get(0).getGetTime(), list.get(list.size() - 1).getGetTime(), mission);
            calculate.setDeltTime(deltTime);
            if (deltTime > 0){
                double vDeltZ = calculate.getDeltValue() / deltTime;
                calculate.setVDeltValue(Math.round(vDeltZ * 100000000.0)/100000000.0);
            }else {
                calculate.setVDeltValue(0.0);
            }
            calculate.setGetTime(collect.get(0).getGetTime());
            if (condition.getIsFc()==null || !condition.getIsFc()){
                //监测预警值/报警值/控制值：由编组挂接的报警设置中的数值(只考虑1项的设置，按“控制值”->“报警值”->“预警值”顺序查找，找到即可，填数值落入报警区间的低位值；若无报警设置，填“-”)；
                //备注：累计变化量、变化速率有一项超限，填写“超限”，否则，填写查询结果列表中最后一个监测周期的工况状态(填不是“正常”的值，“正常”不用填，保留为空)；
                getThreshold(calculate, mission, condition.getGroupId());
            }
            calculateList.add(calculate);
        });
        dataCommon.setDataCalculateList(calculateList);
    }

    /**
     * @description 获取三维位移列表信息
     **/
    private ResponseUtil getXyzList(TableDataCondition condition, List<Long> pointIds) {
        TableDataCommon dataCommon = new TableDataCommon();
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, pointIds)
                .ge(condition.getStartDate()!=null, PointDataXyzh::getGetTime, condition.getStartDate())
                .le(condition.getEndDate()!=null, PointDataXyzh::getGetTime, condition.getEndDate())
                .orderByDesc(PointDataXyzh::getGetTime).orderByDesc(PointDataXyzh::getRecycleNum);
        List<PointDataXyzhVO> list = DzBeanUtils.listCopy(dataXyzhService.list(wrapper), PointDataXyzhVO.class);
        if (list.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            for (PointDataXyzhVO data : list) {
                Date getTime = data.getGetTime();
                if (getTime != null) {
                    String str = format.format(getTime);
                    String str2 = format2.format(getTime);
                    data.setGetTimeStr(str + "\r\n" + str2);
                }
            }
        }
        dataCommon.setDataXyzList(list);
        getXyzCalculateData(dataCommon, list, condition);
        // 设置报表信息
        setReportInfo(dataCommon, condition);
        if (list.size() > 0){
            dataCommon.setGetTime(list.get(0).getGetTime()).setPreTime(list.get(list.size()-1).getGetTimePrev());
        }
        return ResponseUtil.success(dataCommon);
    }

    /**
     * @description 设置报表页信息
     **/
    private void setReportInfo(TableDataCommon data, TableDataCondition condition) {
        ProMission mission = missionService.findById(condition.getMissionId());
        if (mission == null){
            return;
        }
        Project project = projectService.getById(mission.getProjectId());
        data.setProjectName(project.getName()).setJcCompany(project.getNameJc())
                .setReportCode(project.getReportNoPre()+"")
                .setMissionName(mission.getName()).setZl(MissionTypeConst.ZC_FORCE.equals(mission.getTypeName()))
                .setEquipName(mission.getEquipName()).setEquipNo(mission.getEquipNo())
                .setObserve(mission.getEmpObserve()).setCalculate(mission.getEmpCalculate())
                .setReview(mission.getEmpReview());
    }

    /**
     * @description 获取三维位移计算数据
    **/
    private void getXyzCalculateData(TableDataCommon dataCommon, List<PointDataXyzhVO> list, TableDataCondition condition) {
        if(list.size() == 0){
            dataCommon.setDataCalculateList(new ArrayList<>());
            return;
        }
        List<TableDataCalculate> calculateList = new ArrayList<>();
        List<Long> pidList = list.stream().map(PointDataXyzhVO::getPid).distinct().collect(Collectors.toList());
        ProMission mission = missionService.findById(condition.getMissionId());
        // 规则计算
        pidList.forEach(pid -> {
            TableDataCalculate calculate = new TableDataCalculate();
            List<PointDataXyzhVO> collect = list.stream().filter(item -> item.getPid().equals(pid)).collect(Collectors.toList());
            //点名：直接赋值；
            calculate.setName(collect.get(0).getName());
            calculate.setStatus(collect.get(0).getStatus());
            //初始值：查询结果列表中最后一个监测周期的Z0
            calculate.setValue0(collect.get(0).getP0());
            calculate.setPreValue(collect.get(collect.size() -1).getPPrev());
            calculate.setValue(collect.get(0).getP());
            //本次变化量:  deltZ=查询结果列表中最后一个监测周期的TotalZ - 最早一个监测周期的TotalZPrev；
            calculate.setDeltValue(collect.get(0).getTotalP() - collect.get(collect.size() -1).getTotalPPrev());
            //累计变化量：查询结果列表中最后一个监测周期的TotalZ;
            calculate.setTotalValue(collect.get(0).getTotalP());
            //变化速率：VDeltZ=DeltZ/天数（天数=查询结果列表中最后一个监测周期的GetTime - 最早一个监测周期的GetTimePrev,并根据监测任务中设定的间隔计算方式获得天数）;
            double deltTime = getDeltTime(collect.get(0).getGetTime(), list.get(list.size() - 1).getGetTime(), mission);
            calculate.setDeltTime(deltTime);
            if (deltTime > 0){
                double vDeltZ = calculate.getDeltValue() / deltTime;
                calculate.setVDeltValue(Math.round(vDeltZ * 100000000.0)/100000000.0);
            }else {
                calculate.setVDeltValue(0.0);
            }
            calculate.setGetTime(collect.get(0).getGetTime());
            //监测预警值/报警值/控制值：由编组挂接的报警设置中的数值(只考虑1项的设置，按“控制值”->“报警值”->“预警值”顺序查找，找到即可，填数值落入报警区间的低位值；若无报警设置，填“-”)；
            //备注：累计变化量、变化速率有一项超限，填写“超限”，否则，填写查询结果列表中最后一个监测周期的工况状态(填不是“正常”的值，“正常”不用填，保留为空)；
            getThreshold(calculate, mission, condition.getGroupId());
            calculateList.add(calculate);
        });
        dataCommon.setDataCalculateList(calculateList);
    }

    /**
     * @description 设置阈值和备注信息
     **/
    private void getThreshold(TableDataCalculate calculate, ProMission mission, Long groupId){
        PtGroup group = ptGroupService.getById(groupId);
        LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmItem::getAlarmGroupId, group.getAlarmGroupId())
                .eq(AlarmItem::getAlarmType, "1")
                .eq(AlarmItem::getMonitorType, mission.getTypeName());
        if (MissionTypeConst.HAND_XYZ_OFFSET.equals(mission.getTypeName()) || MissionTypeConst.AUTO_XYZ_OFFSET.equals(mission.getTypeName())){
            wrapper.eq(AlarmItem::getResultType, "平面位移(P)");
        }
        List<AlarmItem> alarmItems = alarmItemService.list(wrapper);
        List<AlarmItem> totalList = alarmItems.stream().filter(item -> "累计变化量".equals(item.getResultItemType())).collect(Collectors.toList());
        List<AlarmItem> deltList = alarmItems.stream().filter(item -> "日变化速率".equals(item.getResultItemType())).collect(Collectors.toList());
        String thTotal = "-";
        String thDelt = "-";
        AtomicBoolean over = new AtomicBoolean(false);
        if (totalList.size() > 0){
            thTotal = findThreshold(totalList, over, calculate.getTotalValue());
        }
        if (deltList.size() > 0){
            thDelt = findThreshold(deltList, over, calculate.getVDeltValue());
        }
        calculate.setThreshold(thTotal + "/" + thDelt);
        calculate.setNote(over.get() ? "超限" : ("正常".equals(calculate.getStatus()) ? "" : calculate.getStatus()));
    }

    /**
     * @description 获取阈值
    **/
    private String findThreshold(List<AlarmItem> totalList, AtomicBoolean over, double compareNum) {
        List<AlarmItem> controlList = totalList.stream().filter(item -> "3".equals(item.getAlarmLevel())).collect(Collectors.toList());
        List<AlarmItem> alarmList = totalList.stream().filter(item -> "1".equals(item.getAlarmLevel())).collect(Collectors.toList());
        List<AlarmItem> preList = totalList.stream().filter(item -> "1".equals(item.getAlarmLevel())).collect(Collectors.toList());
        AlarmItem item = null;
        if (controlList.size() > 0){
            item = controlList.get(0);
        }
        if (item == null && alarmList.size() > 0){
            item = alarmList.get(0);
        }
        if (item == null && preList.size() > 0){
            item = preList.get(0);
        }
        if(item == null){
            return "-";
        }
        String[] thSplit = item.getAlarmThreshold().replace("(", "")
                .replace("]", "").split(",");
        double value = item.getAbsValue() ? Math.abs(compareNum) : compareNum;
        if (thSplit[1].contains("∞")){
            if (!over.get() && "∞".equals(thSplit[1])){
                over.set(value > Double.parseDouble(thSplit[0]));
            }
            if (!over.get() && "-∞".equals(thSplit[1])){
                over.set(value <= Double.parseDouble(thSplit[0]));
            }
            return thSplit[0];
        }else {
            double pre = Double.parseDouble(thSplit[0]);
            double after = Double.parseDouble(thSplit[1]);
            if (!over.get()){
                over.set(value >pre && value <= after);
            }
            return Math.abs(pre) > Math.abs(after) ? thSplit[1] : thSplit[0];
        }
    }

    /**
     * @description 获取时间间隔
    **/
    private double getDeltTime(Date getTime, Date lastGetTime , ProMission mission) {
        double deltTime = 0.0;
        if (getTime == null || lastGetTime == null){
            return deltTime;
        }
        double dayNum = (getTime.getTime() - lastGetTime.getTime()) * 1.0 / (1000 * 60 * 60 * 24);
        if (mission!=null && "1".equals(mission.getCalculateType()))
        {
            deltTime = dayNum;
        }
        else if (mission!=null && "2".equals(mission.getCalculateType()))
        {
            deltTime = Math.ceil(dayNum);
        }
        else
        {
            deltTime = dayNum < 1.0 ? dayNum : Math.ceil(dayNum);
        }
        return Math.round(deltTime * 100)*1.0 / 100;
    }

}
