package com.dzkj.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.vo.AppMsgVO;
import com.dzkj.biz.vo.TextVO;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.QwUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.common.util.TokenUtil;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.constant.ControlBoxConstant;
import com.dzkj.entity.equipment.ControlBoxRecord;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.survey.RobotSurveyRecord;
import com.dzkj.entity.system.Online;
import com.dzkj.entity.system.User;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.robot.QwMsgService;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.service.equipment.IControlBoxRecordService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.survey.IRobotSurveyRecordService;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IOnlineService;
import com.dzkj.service.system.IUserGroupService;
import com.dzkj.service.system.IUserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/6/10 13:44
 * @description 定时任务
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
@Component
@EnableScheduling
public class MonitorAutoTask {

    @Autowired
    private IOnlineService onlineService;
    @Autowired
    private RedisTemplate<String , Object> redisTemplate;
    @Autowired
    private IRobotSurveyRecordService surveyRecordService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IUserService userService;
    @Autowired
    private QwUtil qwUtil;
    @Autowired
    private IControlBoxRecordService boxRecordService;
    @Autowired
    private QwMsgService qwMsgService;
    @Autowired
    private ICompanyService companyService;

    /**
     * 清理临时文件
     * 每天夜里零点执行
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void deleteTempFile(){
        log.info("--------------------开始清理临时文件----------------");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH, -1);
        String picPath = "File" + File.separatorChar + "temp-chart" + File.separatorChar
                +  format.format(instance.getTime());
        //图片文件
        File file = new File(picPath);
        File[] listFiles = file.listFiles();
        if (file.isDirectory() && Objects.requireNonNull(listFiles).length>0){
            for (File listFile : listFiles) {
                if(listFile.isFile()){
                    listFile.delete();
                }
            }
        }
        log.info("--------------------临时文件清理完成----------------");
        // 2023/6/20 删除一天前的所有漏测漏传记录
        deleteSurveyRobotRecord();
        // 2023/8/4 删除控制器上下线记录
        deleteControlBoxRecord();
    }

    /**
     * 删除七天前的数据
     */
    private void deleteControlBoxRecord() {
        LambdaQueryWrapper<ControlBoxRecord> wrapper = new LambdaQueryWrapper<>();
        Date date = DateUtil.getDateOfDay(new Date(), -7);
        wrapper.le(ControlBoxRecord::getCreateTime, date);
        boxRecordService.remove(wrapper);
    }

    /**
     * 删除一天前的所有漏测漏传记录
     */
    private void deleteSurveyRobotRecord() {
        LambdaUpdateWrapper<RobotSurveyRecord> wrapper = new LambdaUpdateWrapper<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        wrapper.le(RobotSurveyRecord::getCreateTime, calendar.getTime());
        surveyRecordService.remove(wrapper);
    }

    /**
     * 清除过期在线人员信息
     * 每天夜里1点执行
    **/
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteExpireOnline(){
        log.info("--------------------开始清理过期在线人员----------------");
        List<Online> list = onlineService.list();
        list.forEach(online -> {
            String token = online.getToken();
            if (StringUtils.isEmpty(token) || TokenUtil.isTokenExpired(token)){
                onlineService.removeById(online.getId());
            }
        });
        log.info("--------------------过期在线人员清理完成----------------");
    }

    /**
     * 控制器下线监测
     * 每 5秒执行一次 保活三次
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    public void monitorControlBox(){
        // 获取在线设备序列号
        ConcurrentMap<String, ChannelHandlerContext> onlineChannels = ChannelHandlerUtil.ONLINE_CHANNELS;
        if (onlineChannels.isEmpty()){return;}

        // 离线控制器编号
        List<String> serialNoList = new ArrayList<>();
        Optional<ControlBoxBo> optional;
        ControlBoxBo cBox;
        for (String key : onlineChannels.keySet()) {
            optional = ControlBoxHandler.getOnlineControlBoxes().stream()
                    .filter(item -> Objects.equals(item.getSerialNo(), key)).findAny();
            if (!optional.isPresent()){
                continue;
            }
            cBox = optional.get();
            // 查找是否存在redis中：存在则确认保持心跳
            if (redisTemplate.opsForValue().get(RedisConstant.PREFIX + key) == null){
                int counter = cBox.getKeepAliveCounter();
                if (counter < ControlBoxConstant.SOCKET_KEEP_LIVE_COUNT){
                    cBox.setKeepAliveCounter(counter + 1);
                }else {
                    // 3次保活失败表示box下线--从在线集合中删除，更新控制器状态
                    if (onlineChannels.get(key).channel().isActive()){
                        onlineChannels.get(key).channel().close();
                        onlineChannels.get(key).close();
                    }
                    onlineChannels.remove(key);
                    //下线处理
                    ControlBoxHandler.updateOutlineControlBoxes(cBox);
                    ControlBoxHandler.removeOutlineBox(cBox);
                    serialNoList.add(key);
                    // 2023/7/13 保存下线记录 (修改到netty离线事件中处理)
//                    saveOutlineRecord(key);
                    log.info("控制器:{} 已下线", key);
                }
            }else {
                // 保活成功, 重置保活器
                cBox.setKeepAliveCounter(0);
            }
        }
        // 没有离线控制器，结束
        if(!serialNoList.isEmpty()){
            try {
                // 界面通知 并更新仪器列表状态
                WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_OUT.getCode(),
                        SocketMsgConst.CONTROL_OUT.getMessage(), serialNoList));
            } catch (IOException e) {
                log.warn("monitorControlBox socket发送失败，原因：{}", e.getMessage());
            }
        }
    }

    /**
     * 保存下线记录
     */
    private void saveOutlineRecord(String serialNo){
        ControlBoxRecord record = new ControlBoxRecord();
        record.setSerialNo(serialNo);
        record.setStatus(0);
        boxRecordService.save(record);
    }

    /**
     * 防止未察觉控制器上线状态
     * 30分钟 检查一次
     **/
    @Scheduled(cron = "* 0/30 * * * ?")
    public void checkControlBoxAoStatus(){
        List<ControlBoxBo> boxBos = ControlBoxHandler.getOnlineControlBoxes();
        List<String> onlineSerials = boxBos.stream()
                .map(ControlBoxBo::getSerialNo).collect(Collectors.toList());
        for (ControlBoxAo boxAo : ControlBoxHandler.getAllControlBoxes()) {
            if (onlineSerials.contains(boxAo.getSerialNo())){
                boxAo.setStatus("在线");
                Optional<ControlBoxBo> optional = boxBos.stream().filter(item -> Objects.equals(item.getSerialNo(), boxAo.getSerialNo())).findAny();
                boxAo.setOnlineTime(optional.map(ControlBoxBo::getOnLineTime).orElse(null));
            }
        }
    }

    /**
     * 自动监测漏测漏传监视任务
     * 五分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void monitorRobotSurveyRecord(){
        //1。查询满足漏测的记录
        LambdaUpdateWrapper<RobotSurveyRecord> wrapper1 = new LambdaUpdateWrapper<>();
        wrapper1.eq(RobotSurveyRecord::getSurveyFinish, 0)
                .ne(RobotSurveyRecord::getRecycleNum, 0)
                .eq(RobotSurveyRecord::getOfflineFlg, true)
                .le(RobotSurveyRecord::getSurveyAlarmTime, new Date())
                .orderByDesc(RobotSurveyRecord::getSurveyAlarmTime);
        List<RobotSurveyRecord> list1 = surveyRecordService.list(wrapper1);
        //2。查询满足漏传的记录
        LambdaUpdateWrapper<RobotSurveyRecord> wrapper2 = new LambdaUpdateWrapper<>();
        wrapper2.eq(RobotSurveyRecord::getUploadFinish, 0)
                .ne(RobotSurveyRecord::getRecycleNum, 0)
                .le(RobotSurveyRecord::getUploadAlarmTime, new Date())
                .orderByDesc(RobotSurveyRecord::getUploadAlarmTime);
        List<RobotSurveyRecord> list2 = surveyRecordService.list(wrapper2);
        if (list1.isEmpty() && list2.isEmpty()){
            return;
        }
        List<RobotSurveyRecord> records = new ArrayList<>();
        records.addAll(list1);
        records.addAll(list2);
        //3.发送报警信息
        List<Long> list = new ArrayList<>();
        List<Long> missionIds = records.stream().map(RobotSurveyRecord::getMissionId).distinct().collect(Collectors.toList());
        List<ProMission> missions = missionService.listByIds(missionIds);
        List<UserGroup> userGroups = getUserGroups(missions);
        if (userGroups.isEmpty()) {
            return;
        }
        List<User> users = getUserList(userGroups);
        if (users.isEmpty()) {
            return;
        }
        for (RobotSurveyRecord record : list1) {
            if (list.contains(record.getMissionId())){
                break;
            }
            //发送报警信息
            Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(record.getMissionId()))
                    .findFirst();
            sendQwMsgNoData(1, record, mission, userGroups, users);
            list.add(record.getMissionId());
        }
        list.clear();
        for (RobotSurveyRecord record : list2) {
            if (list.contains(record.getMissionId())){
                break;
            }
            //发送报警信息
            Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(record.getMissionId()))
                    .findFirst();
            sendQwMsgNoData(2, record, mission, userGroups, users);
            list.add(record.getMissionId());
        }
        //4.更新记录信息
        list1.forEach(item -> item.setSurveyFinish(1));
        list2.forEach(item -> item.setUploadFinish(1));
        list2.addAll(list1);
        surveyRecordService.updateBatchById(list2);
    }

    /**
     * 发送漏传漏测微信信息
     *
     * @param type       1：漏测 2；漏传
     * @param record     记录
     * @param missionOpt 任务
     * @param userGroups 用户组
     * @param users      用户
     */
    private void sendQwMsgNoData(int type, RobotSurveyRecord record, Optional<ProMission> missionOpt, List<UserGroup> userGroups, List<User> users) {
        if (!missionOpt.isPresent() || StringUtils.isEmpty(missionOpt.get().getNoDataAlarmGroupIdStr())){
            return;
        }
        //2025-06-23:增加漏测漏传声光报警通知
        ThreadPoolUtil.getPool().execute(() -> qwMsgService.doSendSoundLightCode(missionOpt.get(), record.getSerialNo(), type));

        if (type ==1 && !missionOpt.get().getAlarmSurvey()){
            return;
        }
        if (type ==2 && !missionOpt.get().getAlarmPush()){
            return;
        }
        String groupIdStr = missionOpt.get().getNoDataAlarmGroupIdStr();
        List<String> strings = Arrays.asList(groupIdStr.split(","));
        List<UserGroup> collect = userGroups.stream().filter(item -> strings.contains(item.getGroupId() + ""))
                .collect(Collectors.toList());
        String useridStr = users.stream()
                .filter(item -> collect.stream().map(UserGroup::getUserId)
                        .collect(Collectors.toList()).contains(item.getId())
                        && StringUtils.isNotEmpty(item.getAppId()))
                .map(User::getAppId)
                .collect(Collectors.joining("|"));
        AppMsgVO msgVO = new AppMsgVO();
        msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000002L);
        //构造描述信息
        StringBuilder sb = new StringBuilder();
        sb.append("【").append(type == 1 ? "漏测报警信息】\r\n\r\n" : "漏传报警信息】\r\n\r\n");
        sb.append("监测任务: ").append(missionOpt.get().getName()).append("\r\n");
        sb.append("控制器编号: ").append(record.getSerialNo()).append("\r\n");
        TextVO textVO = new TextVO(sb.toString());
        msgVO.setText(textVO);
        //发送应用信息
        log.info("发送漏传漏测提示信息: {}", sb);
        qwUtil.sendAppTextMsg(msgVO);
    }

    private List<User> getUserList(List<UserGroup> userGroups) {
        List<Long> userIds = userGroups.stream().map(UserGroup::getUserId).collect(Collectors.toList());
        return userService.listByIds(userIds);
    }

    private List<UserGroup> getUserGroups(List<ProMission> missions) {
        List<String> groupIdsStr = missions.stream().filter(item -> StringUtils.isNotEmpty(item.getNoDataAlarmGroupIdStr()))
                .map(ProMission::getNoDataAlarmGroupIdStr).collect(Collectors.toList());
        List<Long> groupIds = new ArrayList<>();
        for (String groupId : groupIdsStr) {
            String[] split = groupId.split(",");
            for (String s : split) {
                groupIds.add(Long.parseLong(s));
            }
        }
        if (groupIds.size() == 0){
            return new ArrayList<>();
        }
        LambdaUpdateWrapper<UserGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(UserGroup::getGroupId, groupIds);
        return userGroupService.list(wrapper);
    }

}
