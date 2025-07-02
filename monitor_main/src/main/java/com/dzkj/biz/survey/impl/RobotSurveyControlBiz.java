package com.dzkj.biz.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.biz.base.BaseFunction;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.survey.IRobotSurveyControlBiz;
import com.dzkj.biz.survey.vo.OnlineCfgResultVo;
import com.dzkj.biz.survey.vo.RobotSurveyCond;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.biz.survey.vo.SurveyBackInfoVo;
import com.dzkj.common.Angle;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PointDataStation;
import com.dzkj.entity.survey.RobotSurveyControl;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.box.ControlBoxMete;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.robot.survey.IDeviceMeteBiz;
import com.dzkj.robot.survey.SurveyBiz;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhRealService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.param_set.IPointDataStationService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.system.ICompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Service
@Slf4j
public class RobotSurveyControlBiz implements IRobotSurveyControlBiz {

    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IPointDataXyzhBiz pointDataXyzhBiz;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private IPointDataStationService pointDataStationService;

    @Override
    public List<RobotSurveyControlVO> getList(Long companyId) {
        if(0==companyId){
            companyId = companyService.getCurrentCompany();
        }
        return DzBeanUtils.listCopy(surveyControlService.findByCompanyId(companyId), RobotSurveyControlVO.class);
    }

    @Override
    public RobotSurveyControlVO findControlInfo(RobotSurveyCond surveyCond) {
        return DzBeanUtils.propertiesCopy(surveyControlService.findControlInfo(surveyCond), RobotSurveyControlVO.class);
    }

    @Override
    public ResponseUtil saveControlInfo(RobotSurveyControlVO surveyControlVO) {
        //修改(24.02.20)：修改增加了多站联测验证测点是否已经配置无意义了
//        if (surveyControlVO.getPidList()!=null && surveyControlVO.getPidList().size() > 0){
//            String result = checkPtIdUsedInMission(surveyControlVO);
//            if (result != null){
//                return ResponseUtil.failure(500, result);
//            }
//        }
        RobotSurveyControl surveyControl = DzBeanUtils.propertiesCopy(surveyControlVO, RobotSurveyControl.class);
        boolean b = surveyControlService.saveOrUpdate(surveyControl);
        if (b){
            surveyControlVO.setId(surveyControl.getId());
            //更新AO中的配置信息
            List<ControlBoxAo> aoList = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> item.getMissionId().equals(surveyControlVO.getMissionId())
                            && item.getSerialNo().equals(surveyControlVO.getSerialNo())).collect(Collectors.toList());
            for (ControlBoxAo boxAo : aoList) {
                boxAo.setSurveyConfigInfo(surveyControlVO.getParams());
                boxAo.setStationInfo(surveyControlVO.getStationConfig());
                boxAo.getSurveyBiz().initData();
            }
            if (surveyControlVO.getDataStationList() != null && !surveyControlVO.getDataStationList().isEmpty()){
                pointDataStationService.saveOrUpdateBatch(DzBeanUtils.listCopy(surveyControlVO.getDataStationList(), PointDataStation.class));
            }
        }
        surveyControlVO.setPidList(null);
        return ResponseUtil.success(surveyControlVO);
    }

    @Override
    public boolean checkStation(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            //驱动仪器进行测站验证
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().checkStation();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean checkPoint(Long controlBoxId, OnlineCfgResultVo resultVo) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            List<Double> calcResults = new ArrayList<>();
            BaseFunction.calcHaVa(resultVo, calcResults);
            //驱动仪器进行测点验证
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().checkMeasureAndGetFullResult(0.05, 0.05,
                    calcResults.get(0), calcResults.get(1), resultVo.getHt(), 0, true);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean refreshControlBox(Long controlBoxId, Integer deviceType) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            ControlBoxAo boxAo = optional.get();
            boxAo.setDeviceType(deviceType);
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz != null){
                surveyBiz.refreshCurrentBoxBo();
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean openDevice(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().openDevice(true);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean closeDevice(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().closeDevice();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean openLaser(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().openLaser();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean closeLaser(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().closeLaser();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean changeFace(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().changeFace();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean openComp(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().openComp();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean surveyTest(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.getControlBoxBo().setProcessCommandStatus(0);
            surveyBiz.getDeviceBiz().surveyTest();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public OnlineCfgResultVo calculateHv(OnlineCfgResultVo cfgResultVo) {
        cfgResultVo.setHa(Angle.rad2Dms(cfgResultVo.getHa(), false));
        cfgResultVo.setVa(Angle.rad2Dms(cfgResultVo.getVa(), false));
        return cfgResultVo;
    }

    @Override
    public OnlineCfgResultVo calculate(OnlineCfgResultVo result) {
        double dx = result.getPtX() - result.getStX();
        double dy = result.getPtY() - result.getStY();
        double dz = result.getPtZ() - result.getStZ() + result.getHt() - result.getHi();

        double sd = Math.sqrt(dx * dx + dy * dy + dz * dz);
        List<Double> calcResults = new ArrayList<>();
        BaseFunction.calcHaVa(result, calcResults);
        double ha = calcResults.get(0);
        double va = calcResults.get(1);
        ha = Angle.rad2Dms(ha, false);
        va = Angle.rad2Dms(va, false);

        result.setHa(ha);
        result.setVa(va);
        result.setSd(sd);
        return result;
    }

    @Override
    public OnlineCfgResultVo calculatePoint(OnlineCfgResultVo resultVo) {
        List<Double> calcResults = new ArrayList<>();
        BaseFunction.calcHaVa(resultVo, calcResults);
        resultVo.setHa(calcResults.get(0));
        resultVo.setVa(calcResults.get(1));
        return resultVo;
    }

    @Override
    public boolean startSurvey(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.start(new Date());
            return true;
        }else {
            return false;
        }
    }

    @Override
    public SurveyBackInfoVo getSurveyInfo(Long controlBoxId) {
        SurveyBackInfoVo backInfoVo = new SurveyBackInfoVo();
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream().filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null){
                return null;
            }
            String statusInfo = surveyBiz.getSurveyStatusInfo();
            backInfoVo.setStatusInfo(statusInfo);
            backInfoVo.setSurveyStatus(optional.get().getSurveyStatus());
            backInfoVo.setStartSurveying(surveyBiz.isStartSurveying());
            backInfoVo.setBackInfos(surveyBiz.getSurveyBackInfos());
            return backInfoVo;
        }else {
            return null;
        }
    }

    @Override
    public boolean stopSurvey(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                optional.get().setSurveyStatus("停测");
                // 2023/6/26 记录状态
                controlBoxService.updateSurvey(controlBoxId, 0);
                log.info("控制器已经下线或不存在");
                return true;
            }
            surveyBiz.stop();
        }
        return true;
    }

    @Override
    public boolean startOnce(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()){
            if (!"待测".equals(optional.get().getSurveyStatus())){
                return false;
            }
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null || surveyBiz.getDeviceBiz() == null){
                return false;
            }
            surveyBiz.setSurveyAtOnce(true);
            surveyBiz.startAtOnce();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<SurveyOnceResult> getOnceResult(Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(it -> it.getId().equals(controlBoxId)).findAny();
        List<SurveyOnceResult> list = new ArrayList<>();
        if (optional.isPresent()){
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            if (surveyBiz == null){
                return list;
            }
            //id,name,x,y,z,ha,va,sd
            List<String> finalResults = surveyBiz.getFinalResults();
            //原始测量数据 monitorItemId|stCfg|recycleNum|rawDatas|calDatas|ajReport
            String surveyData = surveyBiz.getSurveyData();
            if (finalResults.size() == 0 || StringUtils.isEmpty(surveyData)){
                return list;
            }
            List<Long> pidList = finalResults.stream().map(item -> Long.valueOf(item.split(",")[0])).collect(Collectors.toList());
            String[] split = surveyData.split("\\|");
            list = pointDataXyzhBiz.getSurveyOnceResult(finalResults, pidList, Integer.parseInt(split[0]), Integer.parseInt(split[2]));
        }
        return list;
    }

    @Override
    public boolean deleteOnce(List<SurveyOnceResult> resultList) {
        if (resultList == null || resultList.size() == 0){
            return false;
        }
        List<Long> pidList = resultList.stream().map(SurveyOnceResult::getId).collect(Collectors.toList());
        //删除point_data_xyzh表数据
        dataXyzhService.removeOnceData(pidList, resultList.get(0).getRecycleNum());
        dataXyzhRealService.removeOnceData(pidList, resultList.get(0).getRecycleNum());
        //删除alarm_info表数据
        alarmInfoService.removeOnceData(resultList.get(0).getRecycleNum(), resultList.get(0).getMissionId());
        //删除robot_survey_data表数据
        surveyDataService.removeOnceData(resultList.get(0).getRecycleNum(), resultList.get(0).getMissionId());
        return true;
    }

    @Override
    public boolean meteSurvey(String serialNo, Long controlBoxId) {
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(it -> it.getId().equals(controlBoxId)).findAny();
        if (optional.isPresent()) {
            ControlBoxAo item = optional.get();
            SurveyBiz surveyBiz = item.getSurveyBiz();
            if (surveyBiz == null) {
                return false;
            }
            Optional<ControlBoxMete> boxMete = ControlBoxHandler.getOnlineMeteBoxes().stream()
                    .filter(it -> it.getSerialNo().equals(serialNo)).findAny();
            if (boxMete.isPresent() && boxMete.get().isOnLine()) {
                IDeviceMeteBiz meteBiz = surveyBiz.getDeviceMeteBiz();
                meteBiz.refreshCurrentBoxMete(boxMete.get());
                meteBiz.survey(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean vibrationWireSurvey(String serialNo, Long controlBoxId) {
        Optional<ControlBoxBo> boxBo = ControlBoxHandler.getOnlineControlBoxes().stream()
                .filter(it -> it.getSerialNo().equals(serialNo)).findAny();
        if (boxBo.isPresent()) {
            String clientId = boxBo.get().getClientId();
            return ChannelHandlerUtil.sendCommand(clientId, serialNo, "010300000002C40B");
        }
        return false;
    }

    @Override
    public boolean soundLightTest(List<String> serialNoList) {
        ChannelHandlerUtil.sendSoundAlarmCode(String.join(",", serialNoList) + ";10;1,1,1", 0);
        return true;
    }
    //region 私有方法
    /**
     * 验证测站中点是否已经配置
    **/
    private String checkPtIdUsedInMission(RobotSurveyControlVO surveyControlVO) {
        List<RobotSurveyControl> controls = surveyControlService.findByMissionId(surveyControlVO.getMissionId());
        List<String> stConfigStrList = controls.stream().filter(item -> !item.getId().equals(surveyControlVO.getId())
                        && StringUtils.isNotEmpty(item.getParams()) && StringUtils.isNotEmpty(item.getParams().split("\\|")[0]))
                .map(item -> item.getParams().split("\\|")[0])
                .collect(Collectors.toList());
        if (stConfigStrList.size() == 0){
            return null;
        }
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getId, surveyControlVO.getPidList());
        List<Point> points = pointService.list(wrapper);
        StringBuilder ptName = new StringBuilder();
        for (String configStr : stConfigStrList) {
            String[] split = configStr.split(";");
            for (int i = 1; i < split.length; i++) {
                Long pid = Long.valueOf(split[i].split(",")[0]);
                Optional<Point> optional = points.stream().filter(item -> item.getId().equals(pid)).findAny();
                if (optional.isPresent()){
                    if (ptName.length() > 0){
                        ptName.append(",");
                    }
                    ptName.append(optional.get().getName());
                }
            }
        }
        if (ptName.length() > 0){
            return "测点 " + ptName + " 已经在其他测站中配置";
        }
        return null;
    }
    //endregion
}
