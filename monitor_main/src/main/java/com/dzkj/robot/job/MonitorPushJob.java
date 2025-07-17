package com.dzkj.robot.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.util.DateUtil;
import com.dzkj.dataSwap.bean.DataSwapResponse;
import com.dzkj.dataSwap.bean.ctce_api.*;
import com.dzkj.dataSwap.bean.data_upload.PointData;
import com.dzkj.dataSwap.bean.data_upload.ResultJn;
import com.dzkj.dataSwap.bean.data_upload.TotalStationData;
import com.dzkj.dataSwap.bean.monitor_data.MonitorData;
import com.dzkj.dataSwap.bean.monitor_data.OriData;
import com.dzkj.dataSwap.bean.monitor_data.ResultData;
import com.dzkj.dataSwap.bean.process_data.ProcessData;
import com.dzkj.dataSwap.enums.DataSwapEnum;
import com.dzkj.dataSwap.utils.DataSwapUtil;
import com.dzkj.dataSwap.utils.Md5Util;
import com.dzkj.dataSwap.utils.MonitorDataUtil;
import com.dzkj.dataSwap.utils.RsaUtil;
import com.dzkj.entity.data.*;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.robot.QwMsgService;
import com.dzkj.service.data.*;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/11 12:30
 * @description 自动推送任务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class MonitorPushJob implements Job {

    @Autowired
    private IPushPointService pushPointService;
    @Autowired
    private IPushPointJnService pushPointJnService;
    @Autowired
    private IPushPointCtceService pushPointCtceService;
    @Autowired
    private IPointDataXyzhService pointDataXyzhService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private RobotSurveyJobService surveyJobService;
    @Autowired
    private DataSwapUtil dataSwapUtil;
    @Autowired
    private RsaUtil rsaUtil;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IPushTaskRecordService pushTaskRecordService;
    @Autowired
    private IRobotSurveyRecordService recordService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private QwMsgService qwMsgService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Long missionId = jobDataMap.getLong("missionId");
        String serialNo = jobDataMap.getString("serialNo");
        int recycleNum = jobDataMap.getInt("recycleNum");
        int thirdPartType = jobDataMap.getInt("thirdPartType");
        String result = null;
        if (thirdPartType == 1) {
            result = doExecute(missionId, serialNo, recycleNum);
        }
        if (thirdPartType == 2) {
            result = doExecuteJn(missionId, serialNo, recycleNum);
        }
        if (thirdPartType == 3) {
            result = doExecuteCtce(missionId, serialNo, recycleNum);
        }
        if (StringUtils.isNotEmpty(result) && !result.contains("duplicate data")){
            log.info("{} 数据上传异常：{}", serialNo, result);
            // 2023/6/19  发送漏传提示信息
            sendAlarmMsg(missionId, serialNo, result);
        }
        // 2023/6/19 更新漏测漏传记录表
        try {
            log.info("{} 数据上传记录更新...", serialNo);
            LambdaUpdateWrapper<RobotSurveyRecord> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(RobotSurveyRecord::getUploadFinish, 1)
                    .eq(RobotSurveyRecord::getMissionId, missionId)
                    .eq(RobotSurveyRecord::getSerialNo, serialNo)
                    .eq(RobotSurveyRecord::getRecycleNum, recycleNum);
            recordService.update(wrapper);
            log.info("{} {} 数据上传记录更新完成", serialNo, recycleNum);
        } catch (Exception e) {
            log.info("{} {} 数据上传记录更新异常: {}", serialNo, recycleNum, e.getMessage());
        }
        // region 2024/11/19  删除定时推送任务
        removePushJob(serialNo, recycleNum);
        // endregion 2024/11/19
    }

    /**
     * 发送数据上传失败提示信息
     * @param missionId missionId
     * @param serialNo serialNo
     * @param result result
     */
    private void sendAlarmMsg(Long missionId, String serialNo, String result) {
        ProMission mission = missionService.getById(missionId);
        if (mission == null || StringUtils.isEmpty(mission.getNoDataAlarmGroupIdStr())) {
            return;
        }
        qwMsgService.sendSurveyOrUploadFailNotify(mission, serialNo, 2, null);
    }

    /**
     * 执行推送工作-济南局
     * @param missionId missionId
     * @param serialNoStr serialNoStr
     * @param recycleNum recycleNum
     */
    public String doExecuteJn(Long missionId, String serialNoStr, int recycleNum) {
        log.info("开始进行济南局监测数据推送: {}_{}...", serialNoStr, recycleNum);
        //获取推送点列表

        List<PushPointJn> pushPointList = pushPointJnService.queryByMissionId(missionId);
        List<Long> pushPintIds = pushPointList.stream().map(PushPointJn::getPointId).collect(Collectors.toList());
        //获取当前任务最新一期采集数据
        List<PointDataXyzh> latestMonitorDataList = pointDataXyzhService.queryLatestData(pushPintIds);
        latestMonitorDataList = latestMonitorDataList.stream().filter(data -> data.getRecycleNum() >= recycleNum).collect(Collectors.toList());
        if (latestMonitorDataList.isEmpty()){
            return "无任何有效数据可推送,请检查数据是否正确";
        }
        //获取推送任务信息
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, missionId);
        PushTask pushTask = pushTaskService.getOne(wrapper);
        if (pushTask == null) {
            removePushJob(serialNoStr, recycleNum);
            return "推送任务信息不存在";
        }
        TotalStationData stationData = creatJnPushData(pushPointList, latestMonitorDataList, pushTask);
        if(stationData == null){
            return "推送数据对象创建失败";
        }
        StringBuilder sb = new StringBuilder();
        pushJnData(stationData, sb, pushTask);
        //推送失败重试5次，每次间隔10秒
        AtomicInteger repeatTime = new AtomicInteger(0);
        while (StringUtils.isNotEmpty(sb.toString()) && repeatTime.get() < 5){
            log.info("准备第{}次重试:{}", repeatTime.get() + 1, sb);
            sb.setLength(0);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            pushJnData(stationData, sb, pushTask);
            repeatTime.getAndIncrement();
        }
        log.info("济南局监测数据推送结束: {}_{}", serialNoStr, recycleNum);
        return null;
    }

    /**
     * 济南局数据推送
     * @param stationData stationData
     * @param sb          sb
     * @param pushTask    pushTask
     */
    private void pushJnData(TotalStationData stationData, StringBuilder sb, PushTask pushTask) {
        try {
            ResponseEntity<ResultJn> response = restTemplate.exchange(pushTask.getStdUrl(), HttpMethod.POST, new HttpEntity<>(stationData), ResultJn.class);
            if (response.getBody() == null) {
                sb.append("济南局监测数据推送返回结果为空");
                log.info("济南局监测数据推送返回结果为空");
                return;
            }
            if (response.getBody().getCode() != 200) {
                sb.append("济南局监测数据推送失败: ").append(response.getBody().getMessage());
                log.info("济南局监测数据推送失败: {}", response.getBody().getMessage());
            }
        } catch (RestClientException e) {
            log.info("济南局监测数据推送异常: {}", e.getMessage());
            sb.append(e.getMessage());
        }
    }

    /**
     * 构造推送数据对象
     *
     * @param pushPointList
     * @param latestMonitorDataList latestMonitorDataList
     * @param pushTask              pushTask
     * @return TotalStationData
     */
    private TotalStationData creatJnPushData(List<PushPointJn> pushPointList, List<PointDataXyzh> latestMonitorDataList, PushTask pushTask) {
        try {
            //过滤有效数据
            List<PointDataXyzh> latestMonitorDataListFilter = new ArrayList<>();
            for (PushPointJn pushPoint : pushPointList) {
                //本次是否采集到推送点数据
                Optional<PointDataXyzh> optional = latestMonitorDataList.stream()
                        .filter(item -> item.getPid().equals(pushPoint.getPointId())).findAny();
                if (optional.isPresent()){
                    //本次采集到的推送点是否超限
                    PointDataXyzh data = optional.get();
                    if (data.getOverLimit()){
                        switch (pushPoint.getAlarmHandler()){
                            case 2:
                                //上传本次数据
                                break;
                            case 3:
                                //上传上传未报警数据
                                data = pointDataXyzhService.getLastNoAlarmData(pushPoint.getPointId());
                                break;
                            case 1:
                                //不上传
                            default:
                                data = null;
                                break;
                        }
                    }
                    //数据存在时判断可以推送
                    if(data != null) {
                        latestMonitorDataListFilter.add(data);
                    }
                }
            }
            if (latestMonitorDataListFilter.isEmpty()) {
                return null;
            }

            TotalStationData stationData = new TotalStationData();
            stationData.setKey(pushTask.getKey());
            stationData.setSecondkey(pushTask.getSecondkey());
            stationData.setMeastime(DateUtil.dateToDateString(latestMonitorDataList.get(0).getGetTime(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN));
            stationData.setSignature(Md5Util.encode(stationData.getKey() + stationData.getSecondkey() + stationData.getMeastime()));
            List<PointData> list = new ArrayList<>();
            for (PointDataXyzh dataXyzh : latestMonitorDataListFilter) {
                PointData pointData = new PointData();
                pointData.setPointcode(dataXyzh.getName());
                pointData.setX(dataXyzh.getX());
                pointData.setY(dataXyzh.getY());
                pointData.setH(dataXyzh.getZ());
                list.add(pointData);
            }
            stationData.setDatalist(list);
            log.info("创建济南局推送数据对象成功: {}", JSON.toJSONString(stationData));
            return stationData;
        } catch (Exception e) {
            log.info("济南局监测数据构造失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 执行推送工作-济南局
     * @param missionId missionId
     * @param serialNoStr serialNoStr
     * @param recycleNum recycleNum
     */
    public String doExecuteCtce(Long missionId, String serialNoStr, int recycleNum) {
        log.info("开始进行中铁四局监测数据推送: {}_{}...", serialNoStr, recycleNum);
        //获取推送点列表

        List<PushPointCtce> pushPointList = pushPointCtceService.queryByMissionId(missionId);
        List<Long> pushPintIds = pushPointList.stream().map(PushPointCtce::getPointId).collect(Collectors.toList());
        //获取当前任务最新一期采集数据
        List<PointDataXyzh> latestMonitorDataList = pointDataXyzhService.queryLatestData(pushPintIds);
        latestMonitorDataList = latestMonitorDataList.stream().filter(data -> data.getRecycleNum() >= recycleNum).collect(Collectors.toList());
        if (latestMonitorDataList.isEmpty()){
            return "无任何有效数据可推送,请检查数据是否正确";
        }
        //获取推送任务信息
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, missionId);
        PushTask pushTask = pushTaskService.getOne(wrapper);
        if (pushTask == null) {
            removePushJob(serialNoStr, recycleNum);
            return "推送任务信息不存在";
        }
        ApiDataDto apiData = creatCtcePushData(pushPointList, latestMonitorDataList, pushTask);
        if(apiData == null){
            return "推送数据对象创建失败";
        }
        StringBuilder sb = new StringBuilder();
        pushCtceData(apiData, sb, pushTask);
        //推送失败重试5次，每次间隔10秒
        AtomicInteger repeatTime = new AtomicInteger(0);
        while (StringUtils.isNotEmpty(sb.toString()) && repeatTime.get() < 5){
            log.info("中铁四准备第{}次重试:{}", repeatTime.get() + 1, sb);
            sb.setLength(0);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            pushCtceData(apiData, sb, pushTask);
            repeatTime.getAndIncrement();
        }
        log.info("中铁四局监测数据推送结束: {}_{}", serialNoStr, recycleNum);
        return null;
    }

    private void pushCtceData(ApiDataDto apiData, StringBuilder sb, PushTask pushTask) {
        try {
            ResponseEntity<ResultCtce> response = restTemplate.exchange(pushTask.getStdUrl(), HttpMethod.POST, new HttpEntity<>(apiData), ResultCtce.class);
            if (response.getBody() == null) {
                sb.append("中铁四局监测数据推送返回结果为空");
                log.info("中铁四局监测数据推送返回结果为空");
                return;
            }
            if (!Objects.equals(response.getBody().getResultCode(), "200")) {
                sb.append("中铁四局监测数据推送失败: ").append(response.getBody().getMessage());
                log.info("中铁四局监测数据推送失败: {}", response.getBody().getMessage());
            }
            log.info("中铁四局监测数据推送结果: {}", response.getBody().getMessage());
        } catch (RestClientException e) {
            log.info("中铁四局监测数据推送异常: {}", e.getMessage());
            sb.append(e.getMessage());
        }
    }

    private ApiDataDto creatCtcePushData(List<PushPointCtce> pushPointList, List<PointDataXyzh> latestMonitorDataList, PushTask pushTask) {
        try {
            //过滤有效数据
            List<PointDataXyzh> latestMonitorDataListFilter = new ArrayList<>();
            for (PushPointCtce pushPoint : pushPointList) {
                //本次是否采集到推送点数据
                Optional<PointDataXyzh> optional = latestMonitorDataList.stream()
                        .filter(item -> item.getPid().equals(pushPoint.getPointId())).findAny();
                if (optional.isPresent()){
                    //本次采集到的推送点是否超限
                    PointDataXyzh data = optional.get();
                    if (data.getOverLimit()){
                        switch (pushPoint.getAlarmHandler()){
                            case 2:
                                //上传本次数据
                                break;
                            case 3:
                                //上传上传未报警数据
                                data = pointDataXyzhService.getLastNoAlarmData(pushPoint.getPointId());
                                break;
                            case 1:
                                //不上传
                            default:
                                data = null;
                                break;
                        }
                    }
                    //数据存在时判断可以推送
                    if(data != null) {
                        latestMonitorDataListFilter.add(data);
                    }
                }
            }
            if (latestMonitorDataListFilter.isEmpty()) {
                return null;
            }

            ProMission mission = missionService.getById(pushTask.getMissionId());
            Project project = null;
            String lng = null;//经度
            String lat = null;//纬度
            if (mission != null) {
                project = projectService.getById(mission.getProjectId());
                lng = project.getLng();
                lat = project.getLat();
            }
            ApiDataDto apiData = new ApiDataDto();
            DataStr dataStr = new DataStr();
            List<DataList> list = new ArrayList<>();
            for (PointDataXyzh dataXyzh : latestMonitorDataListFilter) {
                DataList dataList = new DataList();
                MeasureData data = new MeasureData();
                data.setMONITORCODE(dataXyzh.getName());
                data.setDATAH(String.format("%.2f", dataXyzh.getZ()));
                data.setGXDATA(String.format("%.2f", dataXyzh.getX()));
                data.setGYDATA(String.format("%.2f", dataXyzh.getY()));
                data.setDELTAX(String.format("%.2f", dataXyzh.getTotalX()));
                data.setDELTAY(String.format("%.2f", dataXyzh.getTotalY()));
                data.setDELTAH(String.format("%.2f", dataXyzh.getTotalZ()));
                data.setDATAXSPEED(String.format("%.2f", dataXyzh.getVDeltX()));
                data.setDATAYSPEED(String.format("%.2f", dataXyzh.getVDeltY()));
                data.setDATAHSPEED(String.format("%.2f", dataXyzh.getVDeltZ()));
                data.setLONGITUDE(lng);
                data.setLATITUDE(lat);
                data.setSTATISTICSDATE(DateUtil.dateToDateString(dataXyzh.getCreateTime()));
                dataList.setDATA(data);
                list.add(dataList);
            }
            dataStr.setLIST(list);
            apiData.setDataStr(dataStr);
            log.info("创建中铁四局推送数据对象成功: {}", JSON.toJSONString(apiData));
            return apiData;
        } catch (Exception e) {
            log.error("创建中铁四局推送数据对象失败:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 执行推送工作
     * @param missionId missionId
     * @param serialNoStr serialNoStr
     * @param recycleNum recycleNum
     */
    public String doExecute(Long missionId, String serialNoStr, int recycleNum) {
        log.info("开始进行监测数据推送: {}_{}...", serialNoStr, recycleNum);
        //获取推送点列表
        List<PushPoint> pushPointList = pushPointService.queryByMissionId(missionId);
        List<Long> pushPintIds = pushPointList.stream().map(PushPoint::getPointId).collect(Collectors.toList());
        if (pushPintIds.isEmpty()){
            return "无任何有效数据可推送,请检查数据是否正确";
        }
        //获取当前任务最新一期采集数据
        List<RobotSurveyData> surveyDataList = surveyDataService.getLatestSurveyData(missionId);
        List<PointDataXyzh> latestMonitorDataList = pointDataXyzhService.queryLatestData(pushPintIds);
        latestMonitorDataList = latestMonitorDataList.stream().filter(data -> data.getRecycleNum() >= recycleNum).collect(Collectors.toList());

        // region: 2024/11/29 验证是否有推送数据
        JSONArray resultData = getResultData(pushPointList, latestMonitorDataList, new StringBuilder(), new ArrayList<>());
        // region: 2024/11/29 验证是否有推送数据
        if (surveyDataList.isEmpty() || latestMonitorDataList.isEmpty() || resultData.isEmpty()){
            return "无任何有效数据可推送,请检查数据是否正确";
        }
        StringBuilder sb = new StringBuilder();
        String projectCode = pushPointList.get(0).getProjectCode();

        //获取推送任务信息
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, missionId)
                .eq(PushTask::getCode, pushPointList.get(0).getProjectCode());
        PushTask pushTask = pushTaskService.getOne(wrapper);
        if (pushTask == null) {
            removePushJob(serialNoStr, recycleNum);
            return "推送任务信息不存在";
        }
        pushData(projectCode, pushPointList, latestMonitorDataList, surveyDataList, sb, pushTask);
        //推送失败重试5次，每次间隔10秒
        AtomicInteger repeatTime = new AtomicInteger(0);
        while (StringUtils.isNotEmpty(sb.toString()) && repeatTime.get() < 5){
            log.info("准备第{}次重试:{}", repeatTime.get() + 1, sb);
            //刷新token
            redisTemplate.delete(RedisConstant.PREFIX + "_STD_TOKEN");
            sb.setLength(0);
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            pushData(projectCode, pushPointList, latestMonitorDataList, surveyDataList, sb, pushTask);
            repeatTime.getAndIncrement();
        }

        if (StringUtils.isEmpty(sb.toString())){
            //3.推送记录保存
            savePushRecord(missionId, recycleNum);
        }
        log.info("监测数据推送结束: {}_{}", serialNoStr, recycleNum);
        return sb.toString();
    }

    private void pushData(String projectCode, List<PushPoint> pushPointList, List<PointDataXyzh> latestMonitorDataList,
                          List<RobotSurveyData> surveyDataList, StringBuilder sb, PushTask pushTask) {
        //1.推送过程数据
        pushProgressData(projectCode, surveyDataList, sb, pushTask);
        //2.推送监测数据
        pushMonitorData(projectCode, pushPointList, latestMonitorDataList, surveyDataList, sb, pushTask);
    }

    /**
     * 推送记录入库
     * @param missionId missionId
     * @param recycleNum recycleNum
     */
    private void savePushRecord(Long missionId, int recycleNum) {
        PushTaskRecord record = new PushTaskRecord();
        record.setMissionId(missionId);
        record.setRecycleNum(recycleNum);
        pushTaskRecordService.save(record);
    }

    //region 过程数据
    /**
     * 获取测量过程数据
     *
     * @param projectCode projectCode
     * @param surveyDataList  surveyData
     * @param sb          sb
     * @param pushTask    pushTask
     */
    private void pushProgressData(String projectCode, List<RobotSurveyData> surveyDataList, StringBuilder sb, PushTask pushTask) {
        try {
            log.info("正在进行 {} 过程数据上报...", projectCode);
            List<ProcessData> processDataList = new ArrayList<>();
            for (RobotSurveyData surveyData : surveyDataList) {
                MonitorDataUtil.getProcessDataList(surveyData.getRawData(), projectCode, processDataList);
            }
            for (ProcessData processData : processDataList) {
                log.info("本次上传过程数据：{}", processData);
                doPushProgressData(processData, sb, pushTask);
            }
            log.info("{} 过程数据上报完成", projectCode);
        } catch (Exception e) {
            log.error("过程数据上报出现错误: {}", e.getMessage() );
            sb.append("过程数据上报出现错误: ").append(e.getMessage()).append("\r\n");
        }
    }

    /**
     * 推送过程数据到铁路局系统
     *
     * @param processData processData
     * @param sb          sb
     * @param pushTask    pushTask
     */
    private void doPushProgressData(ProcessData processData, StringBuilder sb, PushTask pushTask) {
        try {
            String time = DateUtil.dateToDateString(new Date(), DateUtil.yyyyMMddHHmmss_EN);
            String data = rsaUtil.encryptByPublicKey(JSON.toJSONString(processData), pushTask.getPublicKey());
            String token = dataSwapUtil.getToken(pushTask);
            DataSwapResponse response = dataSwapUtil.doRequest(DataSwapEnum.UPLOAD_PROCESS_DATA.getAction(), time, data, token, pushTask);
            log.info("{} {} {} 过程数据上报成功", processData.getProjectCode(), processData.getPCode()
                    , processData.getSeq());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}_{}_{} 过程数据上报异常：{}", processData.getProjectCode(), processData.getPCode()
                    , processData.getSeq(), e.getMessage());
        }
    }
    //endregion

    //region 监测数据
    /**
     * 监测数据上报
     *
     * @param projectCode           项目编号
     * @param pushPointList         可推送测点
     * @param latestMonitorDataList 最新测量测点信息集合
     * @param surveyDataList        最新测量原始信息
     * @param sb                    sb
     * @param pushTask              pushTask
     */
    private void pushMonitorData(String projectCode, List<PushPoint> pushPointList,
                                 List<PointDataXyzh> latestMonitorDataList, List<RobotSurveyData> surveyDataList, StringBuilder sb, PushTask pushTask) {
        try {
            log.info("正在进行 {} 监测数据上报...", projectCode);
            StringBuilder uid = new StringBuilder();
            List<String> resPtCodes = new ArrayList<>();
            JSONArray resultData = getResultData(pushPointList, latestMonitorDataList, uid, resPtCodes);
            JSONArray oriData = getOriData(surveyDataList, pushPointList, resPtCodes);
            log.info("resultData: {}", resultData);
            log.info("oriData: {}", oriData);
            MonitorData monitorData = new MonitorData();
            monitorData.setProjectCode(projectCode);
            monitorData.setDataTime(latestMonitorDataList.get(0).getGetTime());
            monitorData.setEnv(new JSONObject());
            monitorData.setResultData(resultData);
            monitorData.setOriData(oriData);
            monitorData.setUid(uid.toString());
            doPushMonitorData(monitorData, sb, pushTask);
            log.info("{} 监测数据上报结束", projectCode);
        } catch (Exception e) {
            log.error("上海局监测数据上报异常: {}", e.getMessage());
            sb.append("监测数据上报异常: ").append(e.getMessage()).append("\r\n");
        }
    }

    /**
     * 推送过程数据到铁路局系统
     *
     * @param monitorData monitorData
     * @param sb          sb
     * @param pushTask    pushTask
     */
    private void doPushMonitorData(MonitorData monitorData, StringBuilder sb, PushTask pushTask) {
        try {
            String time = DateUtil.dateToDateString(new Date(), DateUtil.yyyyMMddHHmmss_EN);
            String data = rsaUtil.encryptByPublicKey(JSON.toJSONString(monitorData), pushTask.getPublicKey());
            String token = dataSwapUtil.getToken(pushTask);
            DataSwapResponse response = dataSwapUtil.doRequest(DataSwapEnum.UPLOAD_MONITOR_DATA.getAction(), time, data, token, pushTask);
            log.info("{}监测数据上报结果：{}_{}", monitorData.getProjectCode(), response.getHead().getResult(), response.getHead().getReason());
            if (0 != response.getHead().getResult()){
                sb.append("监测数据上报结果错误>>").append(response.getHead().getReason()).append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("监测数据上报异常: {}", e.getMessage());
            sb.append("监测数据上报异常: ").append(e.getMessage()).append("\r\n");
        }
    }

    /**
     * 获取原始数据
     *
     * @param surveyDataList  rawData
     * @param pushPointList   pushPointList
     * @param resPtCodes      resPtCodes
     * @return 原始数据
     */
    private JSONArray getOriData(List<RobotSurveyData> surveyDataList, List<PushPoint> pushPointList, List<String> resPtCodes) {
        JSONArray oriDataArr = new JSONArray();
        List<OriData> oriDataList = new ArrayList<>();
        for (RobotSurveyData surveyData : surveyDataList) {
            MonitorDataUtil.getUploadMonitorDataOriDataList(surveyData, oriDataList);
        }
        oriDataList = oriDataList.stream()
                .filter(item -> resPtCodes.contains(item.getPCode()))
                .collect(Collectors.toList());
        oriDataArr.addAll(oriDataList);
        return oriDataArr;
    }

    /**
     * 获取结果数据
     *
     * @param pushPointList         pushPointList
     * @param latestMonitorDataList latestMonitorDataList
     * @param uid                   uid
     * @param resPtCodes            resPtCodes
     * @return JSONArray
     */
    private JSONArray getResultData(List<PushPoint> pushPointList,
                                    List<PointDataXyzh> latestMonitorDataList, StringBuilder uid,
                                    List<String> resPtCodes) {
        JSONArray resultDataList = new JSONArray();
        for (PushPoint pushPoint : pushPointList) {
            //本次是否采集到推送点数据
            Optional<PointDataXyzh> optional = latestMonitorDataList.stream()
                    .filter(item -> item.getPid().equals(pushPoint.getPointId())).findAny();
            if (optional.isPresent()){
                //本次采集到的推送点是否超限
                PointDataXyzh data = optional.get();
                if (data.getOverLimit()){
                    switch (pushPoint.getAlarmHandler()){
                        case 2:
                            //上传本次数据
                            break;
                        case 3:
                            //上传上传未报警数据
                            data = pointDataXyzhService.getLastNoAlarmData(pushPoint.getPointId());
                            break;
                        case 1:
                            //不上传
                        default:
                            data = null;
                            break;
                    }
                }
                //数据存在时判断可以推送
                if(data != null) {
                    if (resultDataList.size() > 0){
                        uid.append(",");
                    }
                    resPtCodes.add(data.getName());
                    uid.append(pushPoint.getPointId());
                    resultDataList.add(createResultDate(data, pushPoint));
                }
            }
        }
        return resultDataList;
    }

    private ResultData createResultDate(PointDataXyzh data, PushPoint pushPoint) {
        ResultData resultData = new ResultData();
        resultData.setPCode(pushPoint.getPtCode());
        //坐标单位：m, 变化量单位： mm
        resultData.setX((int)(data.getX() * 100000));
        resultData.setY((int)(data.getY() * 100000));
        resultData.setZ((int)(data.getZ() * 100000));
        resultData.setXOffset((int)(data.getDeltX() * 100));
        resultData.setYOffset((int)(data.getDeltY() * 100));
        resultData.setZOffset((int)(data.getDeltZ() * 100));
        //  2024/11/28 应上海局要求，不上传，全站仪容易出错
//        resultData.setXyOffset((int)(data.getDeltP() * 100));
        resultData.setXTotal((int)(data.getTotalX() * 100));
        resultData.setYTotal((int)(data.getTotalY() * 100));
        resultData.setZTotal((int)(data.getTotalZ() * 100));
//        resultData.setXyTotal((int)(data.getTotalP() * 100));
        return resultData;
    }
    //endregion

    /**
     * 删除定时任务
     */
    private void removePushJob(String serialNo, int recycleNum) {
        surveyJobService.removeJob(MonitorJobConst.PUSh_JOB_PREFIX + serialNo,
                MonitorJobConst.PUSh_TRIGGER_PREFIX + serialNo);
    }

}
