package com.dzkj.dataSwap.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.bean.DataSwapResponse;
import com.dzkj.dataSwap.bean.basic_info.DeviceInfo;
import com.dzkj.dataSwap.bean.basic_info.PointArr;
import com.dzkj.dataSwap.bean.basic_info.PointBasicInfo;
import com.dzkj.dataSwap.bean.init_data.PointInitInfo;
import com.dzkj.dataSwap.bean.init_data.UploadInitData;
import com.dzkj.dataSwap.bean.monitor_error.MonitorError;
import com.dzkj.dataSwap.biz.IDataSwapBiz;
import com.dzkj.dataSwap.enums.DataSwapEnum;
import com.dzkj.dataSwap.utils.AngleUtil;
import com.dzkj.dataSwap.utils.DataSwapUtil;
import com.dzkj.dataSwap.utils.MonitorDataUtil;
import com.dzkj.dataSwap.utils.RsaUtil;
import com.dzkj.dataSwap.vo.*;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.robot.job.MonitorPushJob;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskRecordService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 13:44
 * @description 数据交换接口业务接口实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Service
public class DataSwapBizImpl implements IDataSwapBiz {

    @Autowired
    private DataSwapUtil dataSwapUtil;
    @Autowired
    private RsaUtil rsaUtil;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IPushTaskRecordService pushTaskRecordService;
    @Autowired
    private MonitorPushJob monitorPushJob;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IRobotSurveyRecordService recordService;
    @Autowired
    private IRobotSurveyControlService surveyControlService;

    @Override
    public ResponseUtil uploadPoint(List<PushPointVO> pointList) {
        if (pointList == null || pointList.isEmpty()){
            return ResponseUtil.failure(500, "无任何可更新测点");
        }
        PushTask pushTask = pushTaskService.getByCode(pointList.get(0).getProjectCode());
        if (pushTask == null) {
            return ResponseUtil.failure(500, "推送任务不存在");
        }
        //1.监测点信息上报
        String basicInfoData = createUploadPointBasicInfo(pointList);
        DataSwapResponse basicInfoResponse = dataSwapUtil.doRequest(
                DataSwapEnum.UPLOAD_POINT_BASIC_INFO.getAction(),
                DateUtil.dateToDateString(new Date(), DateUtil.yyyyMMddHHmmss_EN),
                rsaUtil.encryptByPublicKey(basicInfoData, pushTask.getPublicKey()),
                dataSwapUtil.getToken(pushTask), pushTask);
        //2.监测初始数据上报
        String uploadInitData = createUploadInitData(pointList);
        DataSwapResponse initDataResponse = dataSwapUtil.doRequest(
                DataSwapEnum.UPLOAD_INIT_DATA.getAction(),
                DateUtil.dateToDateString(new Date(), DateUtil.yyyyMMddHHmmss_EN),
                rsaUtil.encryptByPublicKey(uploadInitData, pushTask.getPublicKey()),
                dataSwapUtil.getToken(pushTask), pushTask);
        if (0 == basicInfoResponse.getHead().getResult()
                && 0 == initDataResponse.getHead().getResult()){
            return ResponseUtil.success();
        }
        String msg;
        if (0 != basicInfoResponse.getHead().getResult()
                && 0 != initDataResponse.getHead().getResult()){
            msg = "监测点信息上报: " + basicInfoResponse.getHead().getReason()
                    + "\r\n监测初始数据上报异常: " + initDataResponse.getHead().getReason();
        }else if (0 != basicInfoResponse.getHead().getResult()){
            msg = "监测点信息上报异常: " + basicInfoResponse.getHead().getReason();
        }else {
            msg = "监测初始数据上报异常: " + initDataResponse.getHead().getReason();
        }
        return ResponseUtil.failure(500, msg);
    }

    @Override
    public MonitorDataMap queryLatestMonitorData(List<Long> pointIds, Long missionId) {
        MonitorDataMap map = new MonitorDataMap();
        if (pointIds == null || pointIds.isEmpty()){
            map.setMonitorDataList(new ArrayList<>());
            map.setOriDataList(new ArrayList<>());
            return map;
        }
        List<MonitorDataVO> monitorDataVOList = DzBeanUtils.listCopy(dataXyzhService.queryLatestData(pointIds), MonitorDataVO.class);
        map.setMonitorDataList(monitorDataVOList);
        if(!monitorDataVOList.isEmpty()){
            int recycleNum = monitorDataVOList.get(0).getRecycleNum();
            List<RobotSurveyData> surveyDataList = surveyDataService.findByRecycleNum(recycleNum, missionId);
            if (surveyDataList.isEmpty()){
                map.setOriDataList(new ArrayList<>());
            }else {
                List<OriOffsetInfoVO> offsetInfos = new ArrayList<>();
                for (RobotSurveyData surveyData : surveyDataList) {
                    MonitorDataUtil.getShowOriOffsetInfos(surveyData.getRawData(), offsetInfos);
                }
                map.setOriDataList(offsetInfos);
            }
        }else {
            map.setOriDataList(new ArrayList<>());
        }
        return map;
    }

    @Override
    public boolean checkPushTask(Long missionId, Integer recycleNum) {
        return pushTaskRecordService.exitPushTaskRecord(missionId, recycleNum);
    }

    @Override
    public ResponseUtil manualPushMonitorData(Long missionId, Integer recycleNum, Integer thirdPartType) {
        // 2024/3/15 修改:只有配置了控制配置的设备才会进行数据采集和推送，多站联测设备号采用|连接成新的设备号
        List<String> serialNoList = controlBoxService.getSerialNoList(missionId);
        List<RobotSurveyControlVO> surveyControlVOS = DzBeanUtils.listCopy(surveyControlService.findByMissionIdAndSerialNo(missionId, null), RobotSurveyControlVO.class);
        List<Long> groupIds = surveyControlVOS.stream().map(RobotSurveyControlVO::getGroupId)
                .filter(groupId -> -1 != groupId).collect(Collectors.toList());
        for (RobotSurveyControlVO controlVO : surveyControlVOS) {
            if (-1 == controlVO.getGroupId()) {
                serialNoList.add(controlVO.getSerialNo());
            }
        }
        for (Long groupId : groupIds) {
            List<RobotSurveyControlVO> collect = surveyControlVOS.stream()
                    .filter(item -> groupId.equals(item.getGroupId())).collect(Collectors.toList());
            String serialNo = collect.stream().map(RobotSurveyControlVO::getSerialNo).collect(Collectors.joining("|"));
            serialNoList.add(serialNo);
        }
        StringBuilder serialNoStr = new StringBuilder();
        for (int i = 0; i < serialNoList.size(); i++) {
            if (i > 1){
                serialNoStr.append(",");
            }
            serialNoStr.append(serialNoList.get(i));
        }
        String result = "未执行数据推送";
        if (thirdPartType == 1) {
            result = monitorPushJob.doExecute(missionId, serialNoStr.toString(), recycleNum);
        }
        if (thirdPartType == 2) {
            result = monitorPushJob.doExecuteJn(missionId, serialNoStr.toString(), recycleNum);
        }
        if (thirdPartType == 3) {
            result = monitorPushJob.doExecuteCtce(missionId, serialNoStr.toString(), recycleNum);
        }
        if (StringUtils.isNotEmpty(result) && !result.contains("duplicate data")){
            log.info("手动上传监测数据出现错误: {}",result);
            return ResponseUtil.failure(500, result);
        }
        // 2023/6/19 更新漏测漏传记录表
        try {
            log.info("{} 数据上传记录更新...", serialNoStr);
            LambdaUpdateWrapper<RobotSurveyRecord> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(RobotSurveyRecord::getUploadFinish, 1).set(RobotSurveyRecord::getSurveyFinish, 1)
                    .eq(RobotSurveyRecord::getMissionId, missionId)
                    .in(RobotSurveyRecord::getSerialNo, serialNoList);
            recordService.update(wrapper);
            log.info("{} 数据上传记录更新完成", serialNoStr);
        } catch (Exception e) {
            log.info("{} 数据上传记录更新异常: {}", serialNoStr, e.getMessage());
        }
        return ResponseUtil.success();
    }

    @Override
    public ResponseUtil uploadMonitorError(MonitorErrorVO monitorErrorVO) {
        try {
            PushTask pushTask = pushTaskService.getByCode(monitorErrorVO.getProjectCode());
            if (pushTask == null) {
                return ResponseUtil.failure(500, "推送任务不存在");
            }
            MonitorError monitorError = DzBeanUtils.propertiesCopy(monitorErrorVO, MonitorError.class);
            String time = DateUtil.dateToDateString(new Date(), DateUtil.yyyyMMddHHmmss_EN);
            String data = rsaUtil.encryptByPublicKey(JSON.toJSONString(monitorError), pushTask.getPublicKey());
            String token = dataSwapUtil.getToken(pushTask);
            DataSwapResponse response = dataSwapUtil.doRequest(DataSwapEnum.UPLOAD_MONITOR_ERR.getAction(), time, data, token, pushTask);
            log.info("监测延测上报结果: {}", response);
            return ResponseUtil.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("监测延测上报发生异常: {}", e.getMessage());
            return ResponseUtil.failure(500, e.getMessage());
        }
    }

    //region 私有方法
    /**
     * 从推送点列表获取监测点上报data JSONString
     * @param pointList pointList
     * @return JSONString
     */
    private String createUploadPointBasicInfo(List<PushPointVO> pointList) {
        List<String> codeList = pointList.stream().map(PushPointVO::getCode).distinct().collect(Collectors.toList());
        JSONArray deviceInfos = new JSONArray();
        for (String code : codeList) {
            List<PushPointVO> list = pointList.stream().filter(item -> code.equals(item.getCode())).collect(Collectors.toList());
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setCode(code);
            deviceInfo.setDeviceCatagory(list.get(0).getDeviceCatagory());
            deviceInfo.setPArr(getPArr(list));
            deviceInfos.add(deviceInfo);
        }

        PointBasicInfo basicInfo = new PointBasicInfo();
        basicInfo.setProjectCode(pointList.get(0).getProjectCode());
        basicInfo.setDeviceInfo(deviceInfos);
        basicInfo.setIsReplace(true);
        return JSON.toJSONString(basicInfo);
    }

    /**
     * 创建测点信息对象
     * @param list list
     * @return 多个监测点的信息
     */
    private JSONArray getPArr(List<PushPointVO> list) {
        JSONArray pArrays = new JSONArray();
        for (PushPointVO data : list) {
            PointArr arr = new PointArr();
            arr.setPCode(data.getPtCode());
            //m->0.01mm
            arr.setHeight((int) Math.floor(data.getHeight() * 100000));
            arr.setMainCatagory(data.getMainCatagory());
            arr.setSubCatagory(data.getSubCatagory());
            arr.setRailwayType(data.getRailwayType());
            arr.setFuncType(data.getFuncType());
            arr.setKiloMark(data.getKiloMark());
            arr.setFuncAttr(data.getFuncAttr());
            pArrays.add(arr);
        }
        return pArrays;
    }

    /**
     * 从推送点列表获取监测初始数据上报data JSONString
     * @param pointList pointList
     * @return JSONString
     */
    private String createUploadInitData(List<PushPointVO> pointList) {
        JSONArray pointInitInfos = new JSONArray();
        for (PushPointVO data : pointList) {
            PointInitInfo initInfo = new PointInitInfo();
            initInfo.setPCode(data.getPtCode());
            initInfo.setOffsetInit(getOffsetInit(data));
            initInfo.setDataTime(data.getDataTime());
            pointInitInfos.add(initInfo);
        }

        UploadInitData initData = new UploadInitData();
        initData.setProjectCode(pointList.get(0).getProjectCode());
        initData.setPointInitInfo(pointInitInfos);
        initData.setIsReplace(true);
        return JSON.toJSONString(initData);
    }

    /**
     * 获取测点位移初始数据
     * @param data data
     * @return JSONArray
     */
    private JSONObject getOffsetInit(PushPointVO data) {
        JSONObject offsetInit = new JSONObject();
        //m -> 0.01mm
        offsetInit.put("distance", (int)Math.floor(data.getDistance() * 100000));
        //D.mmss -> 0.01s
        offsetInit.put("hAngle", AngleUtil.transDmsTos(data.getHa() + "") * 100);
        offsetInit.put("vAngle", AngleUtil.transDmsTos(data.getVa() + "") * 100);
        //m -> 0.01mm
        offsetInit.put("x", (int)Math.floor(data.getX() * 100000));
        offsetInit.put("y", (int)Math.floor(data.getY() * 100000));
        offsetInit.put("z", (int)Math.floor(data.getZ() * 100000));
        return offsetInit;
    }
    //endregion

}
