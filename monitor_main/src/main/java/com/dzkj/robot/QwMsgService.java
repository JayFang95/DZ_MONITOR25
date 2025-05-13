package com.dzkj.robot;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.vo.AppMsgVO;
import com.dzkj.biz.vo.TextVO;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.QwUtil;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PushTask;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.entity.system.User;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.service.data.IPushTaskService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import com.dzkj.service.system.IUserGroupService;
import com.dzkj.service.system.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/26 13:48
 * @description 企业微信消息服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
@Slf4j
public class QwMsgService {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IRobotSurveyRecordService robotSurveyRecordService;
    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    protected IPointService pointService;
    @Autowired
    private QwUtil qwUtil;

    public void sendSurveyProcessMsg(String msg, Long missionId){
        try {
            ProMission proMission = missionService.findById(missionId);
            List<User> users;
            Project project;
            if(proMission != null){
                if(!proMission.getSurveyInfo()){
                    log.info("任务不存在或未开启实测信息推送");
                    return;
                }
                project = projectService.getById(proMission.getProjectId());
                if(project != null){
                    users = getUserList(project.getCompanyId());
                } else {
                    List<UserGroup> userGroupList = getUserGroupList(proMission);
                    if (userGroupList.isEmpty()) {
                        log.info("无任何可推送人员");
                        return;
                    }
                    users = getUserList(userGroupList);
                }
                String useridStr = users.stream().map(User::getAppId)
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.joining("|"));
                AppMsgVO msgVO = new AppMsgVO();
                msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000005L);
                //构造描述信息
                StringBuilder sb = new StringBuilder();
                sb.append("【").append("实测信息】\r\n\r\n");
                sb.append("项目: ").append(project == null ? "null" : project.getName()).append("\r\n");
                sb.append("任务: ").append(proMission.getName()).append("\r\n");
                sb.append("信息: ").append(msg).append("\r\n");

                TextVO textVO = new TextVO(sb.toString());
                msgVO.setText(textVO);
                //发送应用信息
                log.info("发送实测提示信息: {}", sb);
                qwUtil.sendAppTextMsgProcess(msgVO);
            } else {
                log.info("任务不存在, 不发送实测提示信息");
            }
        } catch (Exception e) {
            log.error("发送实测提示信息异常: {}", e.getMessage());
        }
    }

    public void sendSurveyResultMsg(List<PointDataXyzh> dataXyzhs, Long missionId, int groupIndex){
        try {
            //过滤监测点
            dataXyzhs = filterSurveyPointData(dataXyzhs);
            ProMission proMission = missionService.findById(missionId);
            List<User> users;
            Project project;
            if(proMission != null){
                project = projectService.getById(proMission.getProjectId());
                if(project != null){
                    users = getUserList(project.getCompanyId());
                } else {
                    List<UserGroup> userGroupList = getUserGroupList(proMission);
                    if (userGroupList.isEmpty()) {
                        log.info("无任何可推送人员");
                        return;
                    }
                    users = getUserList(userGroupList);
                }
                String useridStr = users.stream().map(User::getAppId)
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.joining("|"));
                AppMsgVO msgVO = new AppMsgVO();
                msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000004L);
                if (dataXyzhs.size() <= 15) {
                    StringBuilder sb = getInfoText(dataXyzhs, groupIndex, project, proMission, 0);
                    TextVO textVO = new TextVO(sb.toString());
                    msgVO.setText(textVO);
                    //发送应用信息
                    log.info("发送漏传漏测提示信息: {}", sb);
                    qwUtil.sendAppTextMsgSurvey(msgVO);
                } else {
                    int idx = 1;
                    for (int i = 0; i < dataXyzhs.size(); i+=15) {
                        //构造描述信息
                        List<PointDataXyzh> list = dataXyzhs.subList(i, Math.min(i + 15, dataXyzhs.size()));
                        StringBuilder sb = getInfoText(list, groupIndex, project, proMission, idx);
                        TextVO textVO = new TextVO(sb.toString());
                        msgVO.setText(textVO);
                        //发送应用信息
                        log.info("发送漏传漏测提示信息: {}", sb);
                        qwUtil.sendAppTextMsgSurvey(msgVO);
                        idx++;
                    }
                }
            } else {
                log.info("任务不存在, 不发送漏传漏测提示信息");
            }
        } catch (Exception e) {
            log.error("发送漏传漏测提示信息异常: {}", e.getMessage());
        }
    }

    private static StringBuilder getInfoText(List<PointDataXyzh> dataXyzhs, int groupIndex, Project project, ProMission proMission, int index) {
        //构造描述信息
        StringBuilder sb = new StringBuilder();
        if (index == 0) {
            sb.append("【").append("测量结果信息】\r\n\r\n");
        } else {
            sb.append("【").append("测量结果信息").append(index).append("】\r\n\r\n");
        }
        sb.append("项目: ").append(project == null ? "null" : project.getName()).append("\r\n");
        sb.append("任务: ").append(proMission.getName()).append("\r\n");
        if (groupIndex != -1) {
            sb.append("编组: ").append(groupIndex + 1).append("\r\n");
        }
        sb.append("测量时间: ").append(DateUtil.dateToDateString(new Date())).append("\r\n");
        if(!dataXyzhs.isEmpty()){
            dataXyzhs.forEach(dataXyz -> {
                sb.append("测点: ").append(dataXyz.getName()).append("\r\n")
                        .append("    ").append("X本次").append(String.format("%.1f", dataXyz.getDeltX()))
                        .append("mm,累计").append(String.format("%.1f", dataXyz.getTotalX())).append("mm\r\n")
                        .append("    ").append("Y本次").append(String.format("%.1f", dataXyz.getDeltY()))
                        .append("mm,累计").append(String.format("%.1f", dataXyz.getTotalY())).append("mm\r\n")
                        .append("    ").append("Z本次").append(String.format("%.1f", dataXyz.getDeltZ()))
                        .append("mm,累计").append(String.format("%.1f", dataXyz.getTotalZ())).append("mm\r\n");
            });
        } else {
            sb.append("测点名称: 无任何监测点测量数据").append("\r\n");
        }
        return sb;
    }

    private List<PointDataXyzh> filterSurveyPointData(List<PointDataXyzh> dataXyzhs) {
        if (dataXyzhs.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> pidList = dataXyzhs.stream().map(PointDataXyzh::getPid).collect(Collectors.toList());
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getId, pidList)
                .eq(Point::getType, "监测点")
                .orderByAsc(Point::getSeq);
        List<Point> points = pointService.list(wrapper);
        List<Long> ids = points.stream().map(Point::getId).collect(Collectors.toList());
        if (ids.isEmpty()) {
            dataXyzhs.clear();
            return new ArrayList<>();
        }
        return dataXyzhs.stream().filter(dataXyz -> ids.contains(dataXyz.getPid())).collect(Collectors.toList());
    }

    /**
     * 自动化测量漏测处理
     *
     * @param missionId  missionId
     * @param serialNo   serialNo
     * @param recycleNum recycleNum
     */
    public void handleSurveyFail(Long missionId, String serialNo, int recycleNum){
        synchronized (this) {
            try {
                if (hasRobotSurveyRecord(missionId, serialNo, recycleNum)) {
                    log.info("{}{}第{}期漏测报警已处理过, 不再处理", missionId, serialNo, recycleNum);
                    return;
                }
                log.info("开始处理{}{}漏测报警", missionId, serialNo);
                //记录漏传信息
                ProMission mission = missionService.getById(missionId);
                addRobotSurveyRecord(mission, missionId, serialNo, recycleNum);
                if (mission == null || StringUtils.isEmpty(mission.getNoDataAlarmGroupIdStr()) || !mission.getAlarmSurvey()) {
                    log.info("{}{}漏测报警处理结束,无推送对象", missionId, serialNo);
                    return;
                }
                //发送漏测报警
                sendSurveyOrUploadFailNotify(mission, serialNo, 1);
                log.info("{}{}漏测报警处理结束", missionId, serialNo);
            } catch (Exception e) {
                log.error("{}{}漏测报警处理异常:{}", missionId, serialNo, e.getMessage());
            }
        }
    }

    private boolean hasRobotSurveyRecord(Long missionId, String serialNo, int recycleNum){
        LambdaQueryWrapper<RobotSurveyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyRecord::getMissionId, missionId)
                .eq(RobotSurveyRecord::getSerialNo, serialNo)
                .eq(RobotSurveyRecord::getRecycleNum, recycleNum)
                .eq(RobotSurveyRecord::getSurveyFinish, 1)
                .ge(RobotSurveyRecord::getCreateTime, new Date(System.currentTimeMillis() - 3 * 60 * 1000L));
        return robotSurveyRecordService.count(wrapper) > 0;
    }

    public void saveSurveyOfflineRecord(Long missionId, String serialNo, int recycleNum){
        log.info("开始记录{}{}离线漏测报警", missionId, serialNo);
        ProMission mission = missionService.getById(missionId);
        //记录离线漏测信息
        PushTask task = getPushTask(missionId);

        //设置最小延迟时间 5 分钟
        int delayTime = task==null ? 5 : task.getDelayUploadTime() >= 5 ? task.getDelayUploadTime() : 5;
        long timeMillis = System.currentTimeMillis();
        RobotSurveyRecord record = new RobotSurveyRecord();
        record.setMissionId(missionId);
        record.setMissionName(mission == null ? "未知" :mission.getName());
        record.setSerialNo(serialNo);
        record.setSurveyFinish(0);
        record.setSurveyAlarmTime(new Date(timeMillis + 60 * 1000));
        record.setUploadFinish(0);
        record.setUploadAlarmTime(new Date(timeMillis + delayTime * 60 * 1000L));
        record.setRecycleNum(recycleNum);
        record.setOfflineFlg(true);
        robotSurveyRecordService.save(record);
        log.info("{}{}离线漏测报警记录完成", missionId, serialNo);
    }

    public void delSurveyOfflineRecord(Long missionId, String serialNo){
        log.info("开始删除{}{}离线漏测报警", missionId, serialNo);
        LambdaQueryWrapper<RobotSurveyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyRecord::getMissionId, missionId)
                .eq(RobotSurveyRecord::getSerialNo, serialNo)
                .eq(RobotSurveyRecord::getOfflineFlg, true);
        robotSurveyRecordService.remove(wrapper);
        log.info("{}{}离线漏测报警删除完成", missionId, serialNo);
    }

    /**
     * 保存漏传记录
     *
     * @param mission    mission
     * @param missionId  missionId
     * @param serialNo   serialNo
     * @param recycleNum recycleNum
     */
    private void addRobotSurveyRecord(ProMission mission, Long missionId, String serialNo, int recycleNum){
        PushTask task = getPushTask(missionId);
        int uploadFinish = 0;
        if (task == null || task.getStatus() != 1) {
            uploadFinish = 1;
        }
        //设置最小延迟时间
        int delayTime = task == null ? 5 :  task.getDelayUploadTime() >= 5 ? task.getDelayUploadTime() : 5;
        RobotSurveyRecord record = new RobotSurveyRecord();
        record.setMissionId(missionId);
        record.setMissionName(mission != null ? mission.getName() : "null");
        record.setSerialNo(serialNo);
        record.setSurveyFinish(1);
        record.setSurveyAlarmTime(new Date());
        record.setUploadFinish(uploadFinish);
        record.setRecycleNum(recycleNum);
        record.setUploadAlarmTime(new Date(System.currentTimeMillis() + delayTime * 60 * 1000L));
        robotSurveyRecordService.save(record);
    }

    private PushTask getPushTask(Long missionId) {
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getMissionId, missionId);
        return pushTaskService.getOne(wrapper);
    }

    /**
     * 发送漏测漏传报警信息
     * @param mission mission
     * @param serialNo serialNo
     * @param type type 1-漏测  2-漏传
     */
    public void sendSurveyOrUploadFailNotify(ProMission mission, String serialNo, int type){
        if (type == 1 && !mission.getAlarmSurvey()){
            log.info("未开启漏测报警推送");
            return;
        }
        if (type == 2 && !mission.getAlarmPush()){
            log.info("未开启漏传报警推送");
            return;
        }
        List<UserGroup> userGroupList = getUserGroupList(mission);
        if (userGroupList.isEmpty()) {
            return;
        }
        List<User> users = getUserList(userGroupList);
        if (users.isEmpty()) {
            return;
        }
        String useridStr = users.stream().map(User::getAppId)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining("|"));
        AppMsgVO msgVO = new AppMsgVO();
        msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000002L);
        //构造描述信息
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(type == 1 ? "漏测报警信息】\r\n\r\n" : "漏传报警信息】\r\n\r\n");
        sb.append("监测任务: ").append(mission.getName()).append("\r\n");
        sb.append("控制器编号: ").append(serialNo).append("\r\n");
        TextVO textVO = new TextVO(sb.toString());
        msgVO.setText(textVO);
        //发送应用信息
        log.info("发送漏传漏测提示信息: {}", sb);
        qwUtil.sendAppTextMsg(msgVO);
    }

    private List<User> getUserList(List<UserGroup> userGroupList) {
        List<Long> userIds = userGroupList.stream()
                .map(UserGroup::getUserId).collect(Collectors.toList());
        return userService.listByIds(userIds);
    }

    private List<User> getUserList(Long companyId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getCompanyId, companyId);
        return userService.list(wrapper);
    }

    private List<UserGroup> getUserGroupList(ProMission mission) {
        if (StringUtils.isEmpty(mission.getNoDataAlarmGroupIdStr())) {
            return new ArrayList<>();
        }
        List<Long> alarmGroupIds = Arrays.stream(mission.getNoDataAlarmGroupIdStr().split(","))
                .map(Long::parseLong).collect(Collectors.toList());
        if (alarmGroupIds.isEmpty()){
            return new ArrayList<>();
        }
        LambdaUpdateWrapper<UserGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(UserGroup::getGroupId, alarmGroupIds);
        return userGroupService.list(wrapper);
    }

}
