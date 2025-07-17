package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.bean.SurveyPoint;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.data.common.BaseDataBiz;
import com.dzkj.biz.data.vo.PointDataXyzhDto;
import com.dzkj.biz.data.vo.PointDataXyzhVO;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataXyzhReal;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.robot.QwMsgService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.data.IPointDataXyzhRealService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/16 17:57
 * @description 三维位移自动监测业务查询
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
@Slf4j
public class PointDataXyzhBiz implements IPointDataXyzhBiz {

    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IAlarmItemService alarmItemService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private DataBizImpl dataBiz;
    @Autowired
    private QwMsgService qwMsgService;

    @Override
    public List<SurveyPoint> findFirstByPIds(List<Long> pIds, boolean hasGroupSurvey, int recycleNum) {
        List<SurveyPoint> surveyPts = new ArrayList<>();
        if (pIds == null || pIds.size() == 0){
            return surveyPts;
        }
        //按工况"破坏重埋"或 "基准点修正"查询，采集时间倒序
//        List<String> statusList = Arrays.asList("破坏重埋", "基准点修正");
//        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(PointDataXyzh::getPid, pIds)
//                .in(PointDataXyzh::getStatus, statusList)
//                .ne(hasGroupSurvey, PointDataXyzh::getRecycleNum, recycleNum)
//                .orderByDesc(PointDataXyzh::getGetTime);
//        List<PointDataXyzh> points = dataXyzhService.list(wrapper);
        List<PointDataXyzh> points = dataXyzhService.getEarliestResetRecycleData(pIds);
        if (hasGroupSurvey) {
            points = points.stream().filter(point -> point.getRecycleNum() != recycleNum).collect(Collectors.toList());
        }
        //保存第1次查询结果中不存在的pid列表
        List<Long> nullPids1 = new ArrayList<>();
        //遍历Pids,过滤出nullPids1以及找到的点列表；
        filterPid(pIds, nullPids1, surveyPts, points);
        //如果所有pid都已经有对应的点，返回
        if (nullPids1.size() == 0)
        {
            return surveyPts;
        }
        //按采集时间升序排列
//        LambdaQueryWrapper<PointDataXyzh> wrapper2 = new LambdaQueryWrapper<>();
//        wrapper2.in(PointDataXyzh::getPid, nullPids1)
//                .ne(hasGroupSurvey, PointDataXyzh::getRecycleNum, recycleNum)
//                .orderByAsc(PointDataXyzh::getGetTime);
//        points = dataXyzhService.list(wrapper2);
        points = dataXyzhService.getEarliestRecycleData(nullPids1);
        if (hasGroupSurvey) {
            points = points.stream().filter(point -> point.getRecycleNum() != recycleNum).collect(Collectors.toList());
        }
        //保存第2次查询结果中不存在的pid列表
        List<Long> nullPids2 = new ArrayList<>();
        //遍历nullPids1,过滤出nullPids2以及找到的点列表；
        filterPid(nullPids1, nullPids2, surveyPts, points);
        //如果nullPids1中所有pid都已经有对应的点，返回
        if (nullPids2.size() == 0)
        {
            return surveyPts;
        }
        //对没有初始值的pId，创建对应的新的SurveyPt（First = true）
        for (long pId : nullPids2)
        {
            SurveyPoint surveyPt = new SurveyPoint();
            surveyPt.setId(pId);
            surveyPt.setFirst(true);
            surveyPts.add(surveyPt);
        }
        return surveyPts;
    }

    @Override
    public List<SurveyPoint> findLastByPIds(List<Long> pIds, int recycleNum){
        List<SurveyPoint> surveyPts = new ArrayList<>();
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, pIds).ne(PointDataXyzh::getRecycleNum, recycleNum)
                .orderByDesc(PointDataXyzh::getGetTime);
        List<PointDataXyzh> points = dataXyzhService.list(wrapper);

        //保存询结果中不存在的pid列表
        List<Long> nullPids = new ArrayList<>();
        //遍历Pids,过滤出nullPids以及找到的点列表；
        filterPid(pIds, nullPids, surveyPts, points);

        //如果所有pid都已经有对应的点，返回
        if (nullPids.size() == 0)
        {
            return surveyPts;
        }

        //对没有初始值的pId，创建对应的新的SurveyPt（First = true）
        for (Long pId : nullPids)
        {
            SurveyPoint surveyPt = new SurveyPoint();
            surveyPt.setId(pId);
            surveyPt.setX(0);
            surveyPt.setY(0);
            surveyPt.setZ(0);
            surveyPt.setFirst(true);
            surveyPts.add(surveyPt);
        }
        return surveyPts;
    }

    @Override
    public List<SurveyOnceResult> getSurveyOnceResult(List<String> finalResults, List<Long> pidList, int missionId, int recycleNum) {
        List<SurveyOnceResult> onceResults = new ArrayList<>();

        List<PointDataXyzh> points = new ArrayList<>();
        for (String finalResult : finalResults) {
            String[] split = finalResult.split(",");
            PointDataXyzh data = getPointDataXyzh(split);
            points.add(data);
        }

        List<SurveyPoint> surveyPoints = findLastByPIds(pidList, recycleNum);
        for (PointDataXyzh dataXyzh : points) {
            Optional<SurveyPoint> optional = surveyPoints.stream().filter(item -> item.getId() == dataXyzh.getPid()).findAny();
            SurveyOnceResult result = new SurveyOnceResult();
            result.setId(dataXyzh.getPid());
            result.setMissionId(missionId);
            result.setRecycleNum(recycleNum);
            result.setName(dataXyzh.getName());
            if (!optional.isPresent() || optional.get().isFirst()){
                result.setDeltX(0);
                result.setDeltY(0);
                result.setDeltZ(0);
            }else {
                result.setDeltX(Math.round((dataXyzh.getX() - optional.get().getX()) * 100000) / 100.0);
                result.setDeltY(Math.round((dataXyzh.getY() - optional.get().getY()) * 100000) / 100.0);
                result.setDeltZ(Math.round((dataXyzh.getZ() - optional.get().getZ()) * 100000) / 100.0);
            }
            onceResults.add(result);
        }
        return onceResults;
    }

    /**
     * xyz成果数据列表(id,name,x,y,z,ha,va,sd)
     * 原始测量数据 monitorItemId|stCfg|recycleNum|rawDatas|calDatas|adjReport
    **/
    @Override
    public PointDataXyzhDto saveRobotResultOnSuccess(List<String> finalResults, String surveyData, boolean hasGroup,
                                                        boolean surveyAtOnce, boolean hasGroupSurvey, List<Long> surveyCfgPointIds, int groupIndex) {
        RobotSurveyData robotSurveyData = getRobotSurveyData(surveyData, hasGroup);
        //抽取xyz成果数据
        List<PointDataXyzh> dataList = new ArrayList<>();
        List<AlarmInfo> alarmInfoList = new ArrayList<>();
        getSurveyResultDataList(finalResults, dataList, alarmInfoList, robotSurveyData);
        if(hasGroupSurvey && groupIndex != -1){
            qwMsgService.sendSurveyResultMsg(dataList, robotSurveyData.getMissionId(), groupIndex);
        }
        //测量结果保存
        robotSurveyData.setCreateTime(dataList.get(0).getGetTime());
        surveyDataService.save(robotSurveyData);
        if(!dataList.isEmpty()){
            //判断数据是否存在本次测点数据，如果存在根据超限情况进行数据更新
            if (hasGroupSurvey) {
                dataList = updateDataList(dataList);
            }
            dataXyzhService.saveBatch(dataList);
            dataXyzhRealService.saveBatch(DzBeanUtils.listCopy(dataList, PointDataXyzhReal.class));
        }
        if(!alarmInfoList.isEmpty()) {
            alarmInfoService.saveBatch(alarmInfoList);
            //发送报警短信
            if (!surveyAtOnce) {
                dataBiz.createAlarmMsg(DzBeanUtils.listCopy(alarmInfoList, AlarmInfoVO.class));
            }
        }
        if (hasGroupSurvey){
            //更新所有的数据时间一致
            if (!surveyCfgPointIds.isEmpty()) {
                LambdaUpdateWrapper<PointDataXyzh> wrapper1 = new LambdaUpdateWrapper<>();
                wrapper1.eq(PointDataXyzh::getRecycleNum, dataList.get(0).getRecycleNum())
                        .in(PointDataXyzh::getPid, surveyCfgPointIds)
                        .set(PointDataXyzh::getGetTime, dataList.get(0).getGetTime());
                dataXyzhService.update(wrapper1);
                LambdaUpdateWrapper<PointDataXyzhReal> wrapper11 = new LambdaUpdateWrapper<>();
                wrapper11.eq(PointDataXyzhReal::getRecycleNum, dataList.get(0).getRecycleNum())
                        .in(PointDataXyzhReal::getPid, surveyCfgPointIds)
                        .set(PointDataXyzhReal::getGetTime, dataList.get(0).getGetTime());
                dataXyzhRealService.update(wrapper11);
            }
            LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataXyzh::getRecycleNum, dataList.get(0).getRecycleNum())
                    .in(PointDataXyzh::getPid, surveyCfgPointIds)
                    .select(PointDataXyzh::getName,
                            PointDataXyzh::getPid,
                            PointDataXyzh::getRecycleNum,
                            PointDataXyzh::getGetTime,
                            PointDataXyzh::getTotalX,
                            PointDataXyzh::getTotalY,
                            PointDataXyzh::getTotalZ,
                            PointDataXyzh::getTotalP,
                            PointDataXyzh::getDeltX,
                            PointDataXyzh::getDeltY,
                            PointDataXyzh::getDeltZ,
                            PointDataXyzh::getDeltP);
            dataList = dataXyzhService.list(wrapper);
        }
        return new PointDataXyzhDto(dataList, !alarmInfoList.isEmpty());
    }

    /**
     * 判断是否是编组之前已经存在保存点
     * @param dataList dataList
     */
    private List<PointDataXyzh> updateDataList(List<PointDataXyzh> dataList) {
        List<Long> pidList = dataList.stream().map(PointDataXyzh::getPid).collect(Collectors.toList());
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, pidList)
                .eq(PointDataXyzh::getRecycleNum, dataList.get(0).getRecycleNum());
        List<PointDataXyzh> pointDataXyzhList = dataXyzhService.list(wrapper);
        if (pointDataXyzhList.isEmpty()) {
            return dataList;
        }
        List<Long> deleteIds = new ArrayList<>();
        List<Long> deletePidList = new ArrayList<>();
        List<Long> deletePtIds = new ArrayList<>();
        //判断是否删除上次的测点：如果上次超限，直接删除；如果上次正常，本次超限，剔除本次数据, 否则删除上次
        for (PointDataXyzh pointDataXyzh : pointDataXyzhList) {
            if (pointDataXyzh.getOverLimit()){
                deleteIds.add(pointDataXyzh.getId());
                deletePidList.add(pointDataXyzh.getPid());
            } else {
                Optional<PointDataXyzh> any = dataList.stream().filter(item -> Objects.equals(item.getPid(), pointDataXyzh.getPid())).findAny();
                if (any.isPresent() && any.get().getOverLimit()) {
                    deletePtIds.add(pointDataXyzh.getPid());
                }else{
                    deleteIds.add(pointDataXyzh.getId());
                    deletePidList.add(pointDataXyzh.getPid());
                }
            }
        }
        if (!deletePtIds.isEmpty()){
            dataList = dataList.stream().filter(item -> !deletePtIds.contains(item.getPid())).collect(Collectors.toList());
        }
        if (deleteIds.isEmpty()){
            return dataList;
        }
        dataXyzhService.removeByIds(deleteIds);
        LambdaQueryWrapper<PointDataXyzhReal> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(PointDataXyzhReal::getPid, deletePidList)
                .eq(PointDataXyzhReal::getRecycleNum, dataList.get(0).getRecycleNum());
        dataXyzhRealService.remove(wrapper1);
        return dataList;
    }


    @Override
    public List<Long> getPointIdWithMission(Long missionId){
        return pointService.queryByMissionId(missionId).stream().map(Point::getId).collect(Collectors.toList());
    }

    @Override
    public void saveRobotData(Date getTime, Long missionId, String surveyData){
        RobotSurveyData robotSurveyData = new RobotSurveyData();
        String[] splitData = surveyData.split("\\|");
        robotSurveyData.setMissionId(missionId);
        robotSurveyData.setRawData(surveyData.length() > 4 ? splitData[3] : "测量失败，无数据");
        robotSurveyData.setCalcData(surveyData.length() > 5 ? splitData[4] : "测量失败，无数据");
        robotSurveyData.setAuto(true);
        robotSurveyData.setCreateTime(getTime);
        surveyDataService.save(robotSurveyData);
    }
    //region 私有方法

    /**
     * 过滤测点
     * @param pidList 带选择测点id集合
     * @param nullPidList 未选中测点id集合
     * @param surveyPts 选中的测点集合
     * @param points 所有可选测点信息
     */
    private void filterPid(List<Long> pidList, List<Long> nullPidList, List<SurveyPoint> surveyPts, List<PointDataXyzh> points) {
        for (long pId : pidList)
        {
            Optional<PointDataXyzh> optional = points.stream().filter(it -> it.getPid() == pId).findFirst();
            if (!optional.isPresent())
            {
                nullPidList.add(pId);
            }
            else
            {
                SurveyPoint surveyPt = createSurveyPointFromPtXyz(optional.get());
                surveyPts.add(surveyPt);
            }
        }
    }

    /**
     * 从PointDataXyz类型的测点中创建SurveyPoint的点
     * @param pointDataXyzh pointDataXyzh
     * @return SurveyPoint
     */
    private SurveyPoint createSurveyPointFromPtXyz(PointDataXyzh pointDataXyzh) {
        SurveyPoint surveyPt = new SurveyPoint();
        surveyPt.setId(pointDataXyzh.getPid());
        surveyPt.setName(pointDataXyzh.getName());
        surveyPt.setX(pointDataXyzh.getX());
        surveyPt.setY(pointDataXyzh.getY());
        surveyPt.setZ(pointDataXyzh.getZ());
        surveyPt.setHa(pointDataXyzh.getHa());
        surveyPt.setVa(pointDataXyzh.getVa());
        surveyPt.setSd(pointDataXyzh.getSd());
        return surveyPt;
    }

    /**
     * 获取测点原始数据
     * @param surveyData  monitorItemId|stCfg|recycleNum|rawDatas|calDatas|adjReport
     * @return RobotSurveyData
     */
    private RobotSurveyData getRobotSurveyData(String surveyData, boolean hasGroup) {
        String[] splitData = surveyData.split("\\|");
        RobotSurveyData robotSurveyData = new RobotSurveyData();
        robotSurveyData.setMissionId(Long.parseLong(splitData[0]));
        robotSurveyData.setStationName(splitData[1]);
        robotSurveyData.setRecycleNum(Integer.parseInt(splitData[2]));
        robotSurveyData.setRawData(splitData[3]);
        robotSurveyData.setCalcData(splitData[4]);
        robotSurveyData.setAdjReport(splitData[5]);
        robotSurveyData.setAuto(true);
        robotSurveyData.setHasGroup(hasGroup);
        return robotSurveyData;
    }

    /**
     * 测量结果字符串集合转换及结果超限验证
     *
     * @param finalResults  finalResults
     * @param dataList      测点数据集合
     * @param alarmInfoList 报警信息集合
     * @param surveyData     surveyData
     */
    private void getSurveyResultDataList(List<String> finalResults, List<PointDataXyzh> dataList,
                                         List<AlarmInfo> alarmInfoList, RobotSurveyData surveyData) {
        if (finalResults == null || finalResults.isEmpty()){return;}
        List<Long> pidList = new ArrayList<>();
        finalResults.forEach(result -> pidList.add(Long.parseLong(result.split(",")[0])));
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, pidList).ne(PointDataXyzh::getRecycleNum, surveyData.getRecycleNum())
                .orderByDesc(PointDataXyzh::getGetTime);
        List<PointDataXyzh> list = dataXyzhService.list(wrapper);
        List<Point> points = pointService.listByIds(pidList);
        ProMission mission = missionService.findById(surveyData.getMissionId());
        Integer recycleNum = surveyData.getRecycleNum();
        for (String finalResult : finalResults) {
            String[] split = finalResult.split(",");
            PointDataXyzh data = getPointDataXyzh(split);
            data.setRecycleNum(recycleNum);
            List<PointDataXyzh> collect = list.stream().filter(it -> it.getPid().equals(data.getPid()))
                    .collect(Collectors.toList());
            PointDataXyzh lastData  = !collect.isEmpty() ? collect.get(0) : null;
            PointDataXyzhVO dataVO = DzBeanUtils.propertiesCopy(data, PointDataXyzhVO.class);
            if(lastData == null){
                // 设置初始值
                BaseDataBiz.setFirstData(dataVO, points, true);
            }else {
                // 设置初始值
                BaseDataBiz.setNewData(dataVO, lastData, mission, points, true);
            }
            // 赋值测量周期
            dataVO.setRecycleNum(recycleNum);
            // 验证测点是否超限，记录报警信息
            List<AlarmItem> alarmItems = alarmItemService.getByPid(data.getPid());
            List<AlarmItem> items = alarmItems.stream().filter(item -> item.getMonitorType().equals(mission.getTypeName()))
                    .collect(Collectors.toList());
            List<String> nameList = items.stream().filter(item -> item.getAlarmType() == 2).map(AlarmItem::getResultItemType).collect(Collectors.toList());
            AtomicInteger atomicInteger = new AtomicInteger(0);
            StringBuilder sb = new StringBuilder();
            List<String> infoList = new ArrayList<>();
            List<String> totalAlarm = BaseDataBiz.checkValueOverXyz(dataVO, items, "累计变化量", atomicInteger, sb, infoList);
            List<String> singleAlarm = BaseDataBiz.checkValueOverXyz(dataVO, items, "单次变化量", atomicInteger, sb, infoList);
            BaseDataBiz.checkValueOverXyz(dataVO, items, "日变化速率", atomicInteger, sb, infoList);
            BaseDataBiz.checkValueOverXyz(dataVO, items, "测量值", atomicInteger, sb, infoList);
            BaseDataBiz.checkValueOverXyz2(dataVO, items, nameList, atomicInteger, sb, infoList);
            dataVO.setRecycleNum(recycleNum);
            if(atomicInteger.get() > 0){
                dataVO.setOverLimit(true);
                dataVO.setOverLimitInfo(sb.substring(0, sb.toString().length()-1));
                // 生成报警信息对象
                if (!infoList.isEmpty()){
                    for (String checkInfo : infoList) {
                        alarmInfoList.add(getAlarmDataXyz(checkInfo, mission, dataVO));
                    }
                }
                System.out.println("报警测点信息:" + finalResult);
            }else {
                dataVO.setOverLimit(false);
            }
            // region: 2024/11/22 判断任务是否启用推送规则 ：开启根据报警阈值修正测量数据
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataVO.getPid())).findAny();
            if (optional.isPresent() && optional.get().getEnableRule()
                    && (!totalAlarm.isEmpty() || !singleAlarm.isEmpty())){
                Point point = optional.get();
                //只统计X Y Z四个值的单次和累计变化量超限情况
                List<String> allAlarmName = new ArrayList<>();
                //2025-06-09：区间验证时不在区间的不允许修正超限值，标记点超限
                //x超限检查
                boolean xOver = false;
                if (singleAlarm.contains("0")){
                    if(totalAlarm.contains("0")){
                        //单次和累计都超限:单次和累计区间验证
                        if (checkDeltEnable(dataVO.getDeltX(), point) && checkTotalEnable(dataVO.getTotalX(), point)){
                            allAlarmName.add("0");
                        } else {
                            xOver = true;
                        }
                    } else {
                        //单次超限累计未超限:单次区间验证
                        if (checkDeltEnable(dataVO.getDeltX(), point)){
                            allAlarmName.add("0");
                        } else {
                            xOver = true;
                        }
                    }
                } else {
                    if(totalAlarm.contains("0")){
                        //单次未超限累计超限:累计区间验证
                        if (checkTotalEnable(dataVO.getTotalX(), point)){
                            allAlarmName.add("0");
                        } else {
                            xOver = true;
                        }
                    }
                }
                //y超限检查
                boolean yOver = false;
                if (singleAlarm.contains("1")){
                    if(totalAlarm.contains("1")){
                        //单次和累计都超限:单次和累计区间验证
                        if (checkDeltEnable(dataVO.getDeltY(), point) && checkTotalEnable(dataVO.getTotalY(), point)){
                            allAlarmName.add("1");
                        } else {
                            yOver = true;
                        }
                    } else {
                        //单次超限累计未超限:单次区间验证
                        if (checkDeltEnable(dataVO.getDeltY(), point)){
                            allAlarmName.add("1");
                        } else {
                            yOver = true;
                        }
                    }
                } else {
                    if(totalAlarm.contains("1")){
                        //单次未超限累计超限:单次和累计区间验证
                        if (checkTotalEnable(dataVO.getTotalY(), point)){
                            allAlarmName.add("1");
                        } else {
                            yOver = true;
                        }
                    }
                }
                //z超限检查
                boolean zOver = false;
                if (singleAlarm.contains("2")){
                    if(totalAlarm.contains("2")){
                        //单次和累计都超限:单次和累计区间验证
                        if (checkDeltEnable(dataVO.getDeltZ(), point) && checkTotalEnable(dataVO.getTotalZ(), point)){
                            allAlarmName.add("2");
                        } else {
                            zOver = true;
                        }
                    } else {
                        //单次超限累计未超限:单次区间验证
                        if (checkDeltEnable(dataVO.getDeltZ(), point)){
                            allAlarmName.add("2");
                        } else {
                            zOver = true;
                        }
                    }
                } else {
                    if(totalAlarm.contains("2")){
                        //单次未超限累计超限:累计区间验证
                        if (checkTotalEnable(dataVO.getTotalZ(), point)){
                            allAlarmName.add("2");
                        } else {
                            zOver = true;
                        }
                    }
                }

//                List<String> allAlarmName = Stream.concat(totalAlarm.stream(), singleAlarm.stream()).distinct().collect(Collectors.toList());
                if (!allAlarmName.isEmpty()) {
                    correctTotalAndSingleSurveyData(dataVO, lastData, items, allAlarmName, mission, points, 1);
                }
                dataVO.setOverLimit(xOver || yOver || zOver);
            }
            // endregion: 2024/11/22 判断任务是否启用推送规则 ：开启根据报警阈值修正测量数据
            dataList.add(DzBeanUtils.propertiesCopy(dataVO, PointDataXyzh.class));
        }
        // 2023/5/30 统一入库观测时间
        for (PointDataXyzh dataXyzh : dataList) {
            dataXyzh.setGetTime(dataList.get(0).getGetTime());
            dataXyzh.setRecycleNum(recycleNum);
            log.info("测量结果信息:{}-{}", dataXyzh.getName(), dataXyzh.getRecycleNum());
        }
    }

    /**
     * 单次超限区间验证
     * @param value 超限值
     * @param pt 测点
     * @return 验证结果
     */
    private boolean checkDeltEnable(double value, Point pt){
        if(value > 0){
            return pt.getMinPosDelt() <= value && value <= pt.getMaxPosDelt();
        } else {
            return pt.getMinNegDelt() <= value && value <= pt.getMaxNegDelt();
        }
    }

    /**
     * 累计超限区间验证
     * @param value 超限值
     * @param pt 测点
     * @return 验证结果
     */
    private boolean checkTotalEnable(double value, Point pt){
        if(value > 0){
            return pt.getMinPosTotal() <= value && value <= pt.getMaxPosTotal();
        } else {
            return pt.getMinNegTotal() <= value && value <= pt.getMaxNegTotal();
        }
    }

    /**
     * 修正单次和累计变化量
     *
     * @param dataVO      dataVO
     * @param lastData    lastData
     * @param items       items
     * @param allAlarmName  allAlarmName (1-x , 2-y , 3-z , 4-p, 累计报警类型)
     * @param i 计算次数
     */
    private void correctTotalAndSingleSurveyData(PointDataXyzhVO dataVO, PointDataXyzh lastData, List<AlarmItem> items
            , List<String> allAlarmName, ProMission mission, List<Point> points, int i) {
        //数据处理
        doCorrectData(dataVO, lastData, allAlarmName, mission);

        //重新计算
        BaseDataBiz.setNewData(dataVO, lastData, mission, points, true);
        //验证新数据是否符合报警阈值
        List<String> correctTotalAlarm = BaseDataBiz.checkValueOverXyz(dataVO, items, "累计变化量", new AtomicInteger(), new StringBuilder(), new ArrayList<>());
        List<String> correctSingleAlarm = BaseDataBiz.checkValueOverXyz(dataVO, items, "单次变化量", new AtomicInteger(), new StringBuilder(), new ArrayList<>());
        if ((!correctTotalAlarm.isEmpty() || !correctSingleAlarm.isEmpty()) && i <= 5) {
            correctTotalAndSingleSurveyData(dataVO, lastData, items, allAlarmName, mission,  points, i + 1);
        }
    }

    /**
     * 测量数据修正
     *
     * @param items        items
     * @param points       points
     * @param dataVO       dataVO
     * @param lastData     lastData
     * @param allAlarmName allAlarmName (0-x, 1-y , 2-z , 3-p, 报警类型)
     * @param mission      mission
     */
    private void doCorrectData(PointDataXyzhVO dataVO, PointDataXyzh lastData, List<String> allAlarmName, ProMission mission) {
        /*
         * 修正逻辑
         * x超限: 上次累计变化量变化0.5-1mm,正负取值和累计变化量相反
         * y超限: 上次累计变化量变化0.5-1mm,正负取值和累计变化量相反
         * z超限: 上次累计变化量变化0-1mm,正负取值和累计变化量相反
         * p超限: x, y 同时上面变化 (取消修正，上海局平台不传了 24.11.29)
         */
        double ratio = mission != null ? mission.getRatio() : 1000.0;
        if (allAlarmName.contains("0")) {
            correctX(dataVO, lastData, ratio);
        }
        if (allAlarmName.contains("1")) {
            correctY(dataVO, lastData, ratio);
        }
        if (allAlarmName.contains("2")) {
            correctZ(dataVO, lastData, ratio);
        }
    }

    private void correctX(PointDataXyzhVO dataVO, PointDataXyzh lastData, double ratio) {
        Double x = lastData.getX();
        Double totalX = lastData.getTotalX();
        double changeX = new Random().nextDouble() * 0.5 + 0.5;
        double deltX = totalX > 0 ? -changeX : changeX;
        System.out.println("X修正前数据:" + dataVO.getX() + "---" + dataVO.getDeltX() + "---" + dataVO.getTotalX());
        dataVO.setTotalX(totalX + deltX);
        dataVO.setDeltX(deltX);
        dataVO.setX(x + deltX / ratio);
        System.out.println("修正值:" + deltX);
        System.out.println("X修正后数据:" + dataVO.getX() + "---" + dataVO.getDeltX() + "---" + dataVO.getTotalX());
    }

    private void correctY(PointDataXyzhVO dataVO, PointDataXyzh lastData, double ratio) {
        Double y = lastData.getY();
        Double totalY = lastData.getTotalY();
        double changeY = new Random().nextDouble() * 0.5 + 0.5;
        double deltY = totalY > 0 ? -changeY : changeY;
        System.out.println("Y修正前数据:" + dataVO.getY() + "---" + dataVO.getDeltY() + "---" + dataVO.getTotalY());
        dataVO.setTotalY(totalY + deltY);
        dataVO.setDeltY(deltY);
        dataVO.setY(y + deltY / ratio);
        System.out.println("修正值:" + deltY);
        System.out.println("Y修正后数据:" + dataVO.getY() + "---" + dataVO.getDeltY() + "---" + dataVO.getTotalY());
    }

    private void correctZ(PointDataXyzhVO dataVO, PointDataXyzh lastData, double ratio) {
        Double z = lastData.getZ();
        Double totalZ = lastData.getTotalZ();
        double changeZ = new Random().nextDouble();
        double deltZ = totalZ > 0 ? -changeZ : changeZ;
        System.out.println("Z修正前数据:" + dataVO.getZ() + "---" + dataVO.getDeltZ() + "---" + dataVO.getTotalZ());
        dataVO.setTotalZ(totalZ + deltZ);
        dataVO.setDeltZ(deltZ);
        dataVO.setZ(z + deltZ / ratio);
        System.out.println("修正值:" + deltZ);
        System.out.println("Z修正后数据:" + dataVO.getZ() + "---" + dataVO.getDeltZ() + "---" + dataVO.getTotalZ());
    }

    private void correctP(PointDataXyzhVO dataVO, PointDataXyzh lastData) {
        correctX(dataVO, lastData, 1000.0);
        correctY(dataVO, lastData, 1000.0);
    }

    /**
     * 获取X Y Z P单项报警项的单次和累计安全阈值区间
     * @param allSafeThresholdVal allSafeThresholdVal
     * @param items items
     */
    private void setAllSafeThresholdVal(Double[][] allSafeThresholdVal, List<AlarmItem> items) {
        List<AlarmItem> xAlarm = BaseDataBiz.filterAlarmItem2(0, items);
        List<AlarmItem> yAlarm = BaseDataBiz.filterAlarmItem2(1, items);
        List<AlarmItem> zAlarm = BaseDataBiz.filterAlarmItem2(2, items);
        List<AlarmItem> pAlarm = BaseDataBiz.filterAlarmItem2(3, items);
        setSafeThresholdVal(xAlarm, allSafeThresholdVal, 0);
        setSafeThresholdVal(yAlarm, allSafeThresholdVal, 1);
        setSafeThresholdVal(zAlarm, allSafeThresholdVal, 2);
        setSafeThresholdVal(pAlarm, allSafeThresholdVal, 3);
    }

    private void setSafeThresholdVal(List<AlarmItem> items, Double[][] allSafeThresholdVal, int i) {
        Double[] safeVal = allSafeThresholdVal[i];
        List<AlarmItem> singles = items.stream().filter(item -> "单次变化量".equals(item.getResultItemType())).collect(Collectors.toList());
        List<AlarmItem> totals = items.stream().filter(item -> "累计变化量".equals(item.getResultItemType())).collect(Collectors.toList());
        if (singles.size() > 0) {
            Double[] safeThVal = getSafeThVal(totals);
            safeVal[1] = safeThVal[0];
            safeVal[2] = safeThVal[1];
        }
        if (totals.size() > 0) {
            Double[] safeThVal = getSafeThVal(totals);
            safeVal[3] = safeThVal[0];
            safeVal[4] = safeThVal[1];
        }
    }

    /**
     * 提取安全阈值区间
     * @param filterList filterList
     */
    private Double[] getSafeThVal(List<AlarmItem> filterList) {
        Double[] safeDouble = {null, null};
        for (AlarmItem item : filterList) {
            String[] thValArray = item.getAlarmThreshold()
                    .replace("(", "")
                    .replace("]", "").split(",");
            if (item.getAbsValue()) {
                if (safeDouble[1]== null) {
                    safeDouble[1] = Double.parseDouble(thValArray[0]);
                    safeDouble[0] = - Double.parseDouble(thValArray[0]);
                } else {
                    if (Double.parseDouble(thValArray[0]) < safeDouble[1]) {
                        safeDouble[1] = Double.parseDouble(thValArray[0]);
                        safeDouble[0] = - Double.parseDouble(thValArray[0]);
                    }
                }
            } else {
                if (thValArray[1].contains("∞")) {
                    if("∞".equals(thValArray[1])) {
                        if (safeDouble[1]== null) {
                            safeDouble[1] = Double.parseDouble(thValArray[0]);
                        } else {
                            if (Double.parseDouble(thValArray[0]) < safeDouble[1]) {
                                safeDouble[1] = Double.parseDouble(thValArray[0]);
                            }
                        }
                    } else {
                        if (safeDouble[0]== null) {
                            safeDouble[0] = Double.parseDouble(thValArray[0]);
                        } else {
                            if (Double.parseDouble(thValArray[0]) > safeDouble[0]) {
                                safeDouble[0] = Double.parseDouble(thValArray[0]);
                            }
                        }
                    }
                } else {
                    double val = Double.parseDouble(thValArray[0]);
                    if(val < 0) {
                        if (safeDouble[0]== null) {
                            safeDouble[0] = val;
                        } else {
                            if (val > safeDouble[0]) {
                                safeDouble[0] = val;
                            }
                        }
                    } else {
                        if (safeDouble[1]== null) {
                            safeDouble[1] = val;
                        } else {
                            if (val < safeDouble[1]) {
                                safeDouble[1] = val;
                            }
                        }
                    }
                }
            }
        }
        return safeDouble;
    }

    /**
     * 获取报警对象
     */
    private AlarmInfo getAlarmDataXyz(String alarmInfoStr, ProMission mission, PointDataXyzhVO dataXyzh) {
        String[] split = alarmInfoStr.split(";");
        AlarmInfo info = new AlarmInfo();
        info.setProjectId(mission.getProjectId());
        info.setMissionId(mission.getId());
        info.setPtId(dataXyzh.getPid());
        info.setRecycleNum(dataXyzh.getRecycleNum());
        info.setAlarmOrigin(split[0]);
        info.setInfo(split[1]);
        info.setAlarmLevel(split[2]);
        info.setThreshold(split[3]);
        info.setAbs("true".equals(split[4]));
        if (split.length == 6) {
            info.setVal(Double.parseDouble( split[5]));
        }
        info.setAlarmTime(new Date());
        info.setHandle(false);
        return info;
    }

    /**
     * 获取测点数据
     * @param split (id,name,x,y,z,ha,va,sd)
     * @return PointDataXyzh
     */
    private PointDataXyzh getPointDataXyzh(String[] split) {
        PointDataXyzh data = new PointDataXyzh();
        data.setPid(Long.parseLong(split[0]));
        data.setName(split[1]);
        data.setX(Double.parseDouble(split[2]));
        data.setY(Double.parseDouble(split[3]));
        data.setZ(Double.parseDouble(split[4]));
        data.setHa(Double.parseDouble(split[5]));
        data.setVa(Double.parseDouble(split[6]));
        data.setSd(Double.parseDouble(split[7]));
        data.setStop(false);
        data.setStatus("正常");
        data.setGetTime(new Date());
        data.setAuto(true);
        return data;
    }
    //endregion

}
