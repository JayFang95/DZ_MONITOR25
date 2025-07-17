package com.dzkj.biz.equipment.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.IPointDataXyzhBiz;
import com.dzkj.biz.equipment.IEquipManagerBiz;
import com.dzkj.biz.equipment.vo.ControlBoxVO;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.biz.survey.IRobotSurveyDataBiz;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.equipment.ControlBox;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.survey.RobotSurveyControl;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.job.RobotSurveyJobService;
import com.dzkj.robot.survey.SurveyBiz;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.IPointDataXyzhCorrectService;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushTaskOtherService;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.equipment.IControlBoxService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.survey.IRobotSurveyControlGroupService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import com.dzkj.service.survey.ISurveyCycleService;
import com.dzkj.service.system.ICompanyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Service
public class EquipManagerBiz implements IEquipManagerBiz {

    @Autowired
    private IControlBoxService controlBoxService;
    @Autowired
    private ISurveyCycleService surveyCycleService;
    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private IRobotSurveyControlGroupService surveyControlGroupService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IPointDataXyzhBiz dataXyzhBiz;
    @Autowired
    private IRobotSurveyDataBiz surveyDataBiz;
    @Autowired
    private RobotSurveyJobService surveyJobService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IRobotSurveyRecordService recordService;
    @Autowired
    private IPushTaskOtherService pushTaskOtherService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    @Autowired
    private IAlarmInfoService infoService;
    @Autowired
    private IAlarmInfoCorrectService infoCorrectService;
    @Autowired
    private QwMsgService qwMsgService;

    @Override
    public IPage<ControlBoxVO> getPage(Integer pageIndex, Integer pageSize, EquipCondition cond) {
        IPage<ControlBoxVO> page = new Page<>(pageIndex, pageSize);
        if (cond.getMissionIds() == null || cond.getMissionIds().size() == 0){
            return page;
        }
        if (pageSize == CommonConstant.SEARCH_ALL_NO){
            List<ControlBoxVO> list = DzBeanUtils.listCopy(controlBoxService.getList(cond), ControlBoxVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = DzBeanUtils.pageCopy(controlBoxService.getPage(pageIndex, pageSize, cond), ControlBoxVO.class);
        }
        List<ControlBoxVO> records = page.getRecords();
        List<ControlBoxAo> aoList = ControlBoxHandler.getAllControlBoxes();
        for (ControlBoxVO vo : records) {
            Optional<ControlBoxAo> optional = aoList.stream().filter(item -> item.getId().equals(vo.getId())).findAny();
            if (optional.isPresent()){
                vo.setOnlineTime(optional.get().getOnlineTime());
                vo.setStatus(optional.get().getStatus());
                vo.setGroupInfo(optional.get().getGroupInfo());
                vo.setStationInfo(optional.get().getStationInfo());
                vo.setSurveyStatus(optional.get().getSurveyStatus());
            }
            if (!"全站仪控制器".equals(vo.getType())){
                vo.setSurveyStatus("/");
            }
        }
        return page;
    }

    @Override
    public ResponseUtil add(ControlBoxVO data) {
        if(controlBoxService.findSerialNoByMission(data)){
            return ResponseUtil.failure(500, "当前任务已存在设备序列号:" + data.getSerialNo());
        }
        if(0==data.getCompanyId()){
            data.setCompanyId(companyService.getCurrentCompany());
        }
        data.setDeviceType(0);
        ControlBox box = DzBeanUtils.propertiesCopy(data, ControlBox.class);
        boolean b = controlBoxService.save(box);
        // 添加控制器成功
        if (b){
            // 构建并保存control_box AO对象
            ControlBoxAo boxAo = DzBeanUtils.propertiesCopy(box, ControlBoxAo.class);
            ProMission mission = missionService.getById(box.getMissionId());
            if (mission != null){
                boxAo.setMissionName(mission.getName());
                Project project = projectService.getById(mission.getProjectId());
                boxAo.setProjectName(project == null ? "" : project.getName());
            }
            ControlBoxHandler.addControlBoxAo(boxAo);
            if (!"声光报警控制器".equals(box.getType())){
                //给控制器AO绑定业务类
                SurveyBiz surveyBiz = new SurveyBiz(boxAo,
                        dataXyzhBiz,
                        surveyDataBiz,
                        surveyJobService,
                        pushTaskService,
                        recordService,
                        controlBoxService,
                        surveyCycleService,
                        surveyControlService,
                        surveyControlGroupService,
                        pushTaskOtherService,
                        dataXyzhService,
                        dataXyzhCorrectService,
                        infoService,
                        infoCorrectService,
                        qwMsgService);
                boxAo.setSurveyBiz(surveyBiz);
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(ControlBoxVO data) {
        if(controlBoxService.findSerialNoByMission(data)){
            return ResponseUtil.failure(500, "当前任务已存在设备序列号:" + data.getSerialNo());
        }
        boolean b = controlBoxService.updateById(DzBeanUtils.propertiesCopy(data, ControlBox.class));
        if (b){
            /*
             * 如果修改了控制器编号需要同步到AO中
            **/
            Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> item.getId().equals(data.getId())).findAny();
            if (optional.isPresent() && !optional.get().getSerialNo().equals(data.getSerialNo())){
                optional.get().setSerialNo(data.getSerialNo());
                //同步在线状态
                Optional<ControlBoxBo> optional2 = ControlBoxHandler.getOnlineControlBoxes().stream()
                        .filter(item -> Objects.equals(item.getSerialNo(), data.getSerialNo())).findAny();
                if (optional2.isPresent()){
                    optional.get().setStatus("在线");
                    optional.get().setOnlineTime(optional2.get().getOnLineTime());
                }
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = controlBoxService.removeById(id);
        //删除关联的AO对象
        ControlBoxHandler.removeBoxAo(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil bind(Long id) {
        // 一台控制器只能绑定一个监测任务
        int count = controlBoxService.countBindSerialNo(id);
        if (count > 0){
            return ResponseUtil.failure(500, "控制器已经绑定监测任务，请先解绑");
        }
        ControlBox controlBox = controlBoxService.getById(id);
        // 验证通过，绑定控制器到任务
        boolean b = controlBoxService.updateBindStatus(id, true);
        if (b){
            List<ControlBoxAo> list = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> controlBox.getSerialNo().equals(item.getSerialNo())).collect(Collectors.toList());
            //更新AO信息
            for (ControlBoxAo boxAo : list) {
                boxAo.setBindMission(boxAo.getId().equals(id));
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "设备绑定失败");
    }

    @Override
    public ResponseUtil unbind(Long id) {
        ControlBox controlBox = controlBoxService.getById(id);
        if("温度气压控制器".equals(controlBox.getType())){
            if(checkBoxUsed(controlBox)){
                return ResponseUtil.failure(500, "已经绑定温湿度监测任务，无法解绑");
            }
        }
        controlBox.setBindMission(false);
        controlBox.setDeviceType(0);
        controlBox.setDeviceInfo(null);
        boolean b = controlBoxService.updateById(controlBox);
        //解绑后删除关联的配置信息
        if (b){
            surveyControlService.removeBySerialNo(controlBox.getSerialNo());
            //更新AO信息
            List<ControlBoxAo> list = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> controlBox.getSerialNo().equals(item.getSerialNo())).collect(Collectors.toList());
            //更新AO信息
            for (ControlBoxAo boxAo : list) {
                boxAo.setBindMission(false);
                boxAo.setDeviceType(0);
                boxAo.setDeviceInfo(null);
                boxAo.setSurveyConfigInfo(null);
                boxAo.setStationInfo(null);
                if (boxAo.getSurveyBiz() != null){
                    boxAo.getSurveyBiz().setDeviceBiz(null);
                }
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "设备解绑失败");
    }

    @Override
    public String getControlBoxTotalInfo(Long companyId) {
        String totalInfo;
        if(0==companyId){
            companyId = companyService.getCurrentCompany();
        }
        List<ControlBoxAo> list;
        Long finalCompanyId = companyId;
        if (companyId == -1){
            list = ControlBoxHandler.getAllControlBoxes();
        }else {
            list = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> finalCompanyId.equals(item.getCompanyId())).collect(Collectors.toList());
        }
        long totalCount = list.stream().filter(item -> "在线".equals(item.getStatus())).count();
        totalInfo = "[控制器在线情况]--在线数: " + totalCount+" 离线数:"+ (list.size() - totalCount);
        totalCount = list.stream().filter(item -> "在测".equals(item.getSurveyStatus())).count();
        totalInfo += "    [控制器测量情况]--在测数:" + totalCount;
        totalCount = list.stream().filter(item -> "待测".equals(item.getSurveyStatus())).count();
        totalInfo += " 待测数:" + totalCount;
        totalCount = list.stream().filter(item -> "停测".equals(item.getSurveyStatus())).count();
        totalInfo += " 停测数:" + totalCount;
        return totalInfo;
    }

    @Override
    public ControlBoxVO getControlBox(Long id) {
        ControlBoxVO boxVO = DzBeanUtils.propertiesCopy(controlBoxService.getById(id), ControlBoxVO.class);
        Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(item -> item.getId().equals(id)).findAny();
        if (optional.isPresent()){
            boxVO.setOnlineTime(optional.get().getOnlineTime());
            boxVO.setStatus(optional.get().getStatus());
            boxVO.setSurveyStatus(optional.get().getSurveyStatus());
            SurveyBiz surveyBiz = optional.get().getSurveyBiz();
            String statusInfo = surveyBiz.getSurveyStatusInfo();
            boxVO.setGroupId(optional.get().getGroupId());
            boxVO.setGroupInfo(optional.get().getGroupInfo());
            boxVO.setStatusInfo(statusInfo);
            boxVO.setStationInfo(optional.get().getStationInfo());
        }
        return boxVO;
    }

    @Override
    public ResponseUtil updateDeviceInfo(ControlBoxVO data) {
        if (data == null || data.getId() == null){
            return ResponseUtil.failure(500, "参数信息不完整");
        }
        boolean b = controlBoxService.updateDeviceInfo(data);
        if (b){
            Optional<ControlBoxAo> optional = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(item -> item.getId().equals(data.getId())).findAny();
            optional.ifPresent(controlBoxAo -> controlBoxAo.setDeviceInfo(data.getDeviceInfo()));
        }
        return ResponseUtil.success();
    }

    @Override
    public List<ControlBoxVO> getOnlineMeteControlBoxList(Long missionId) {
        List<ControlBoxVO> list = DzBeanUtils.listCopy(controlBoxService.getMeteBoxListByMissionId(missionId), ControlBoxVO.class);
        List<String> serialNoList = ControlBoxHandler.getAllControlBoxes()
                .stream().filter(item -> "在线".equals(item.getStatus()))
                .map(ControlBoxAo::getSerialNo)
                .collect(Collectors.toList());
        return list.stream().filter(item -> serialNoList.contains(item.getSerialNo())).collect(Collectors.toList());
    }

    @Override
    public ControlBoxVO getControlBoxInfo(Long missionId, String serialNo) {
        List<ControlBoxVO> boxVOs = DzBeanUtils.listCopy(controlBoxService.getControlBoxInfo(missionId, serialNo), ControlBoxVO.class);
        return boxVOs.size() > 0 ? boxVOs.get(0) : null;
    }

    @Override
    public List<ControlBoxVO> getListApp(Long missionId) {
        List<ControlBoxVO> list = DzBeanUtils.listCopy(controlBoxService.getBindListByMissionId(missionId), ControlBoxVO.class);
        List<ControlBoxAo> aoList = ControlBoxHandler.getAllControlBoxes();
        for (ControlBoxVO vo : list) {
            Optional<ControlBoxAo> optional = aoList.stream().filter(item -> item.getId().equals(vo.getId())).findAny();
            if (optional.isPresent()){
                vo.setOnlineTime(optional.get().getOnlineTime());
                vo.setStatus(optional.get().getStatus());
                vo.setGroupInfo(optional.get().getGroupInfo());
                vo.setStationInfo(optional.get().getStationInfo());
                vo.setSurveyStatus(optional.get().getSurveyStatus());
            }
        }
        return list;
    }

    @Override
    public List<ControlBoxVO> getSoundControlBoxList(Long missionId) {
        //todo 是否需要过滤在线
        return DzBeanUtils.listCopy(controlBoxService.getSoundControlBoxList(missionId), ControlBoxVO.class);
    }

    /**
     * 验证是否正在使用
     * @param controlBox controlBox
     * @return 验证结果
     */
    private boolean checkBoxUsed(ControlBox controlBox) {
        List<RobotSurveyControl> controls = surveyControlService.findByMissionId(controlBox.getMissionId());
        for (RobotSurveyControl control : controls) {
            if (StringUtils.isNotEmpty(control.getParams())) {
                String[] split = control.getParams().split("\\|");
                String first = split[3].split(",")[0];
                if (controlBox.getSerialNo().equals(first)) {
                    return true;
                }
            }
        }
        return false;
    }

}
